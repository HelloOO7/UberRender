package urender.demo;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import javax.swing.SwingUtilities;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import urender.api.UClearMode;
import urender.api.UFaceCulling;
import urender.api.UFramebufferAttachment;
import urender.api.UTestFunction;
import urender.api.UTextureFormat;
import urender.api.backend.GLRenderingBackend;
import urender.api.backend.RenderingBackend;
import urender.demo.perf.IPerfMonitor;
import urender.scenegraph.UDrawSources;
import urender.engine.UFramebuffer;
import urender.scenegraph.UGfxRenderer;
import urender.engine.UMaterialDrawLayer;
import urender.engine.URenderTarget;
import urender.g3dio.ugfx.UGfxResource;
import urender.scenegraph.UCameraLookAtOrbit;
import urender.scenegraph.URenderQueue;
import urender.scenegraph.io.USceneNodeGfxResourceAdapter;
import urender.scenegraph.USceneNode;
import urender.scenegraph.io.UScenegraphGfxResourceLoader;

public class GLJPanelDummy extends GLJPanel implements GLAutoDrawable, GLEventListener {

	private static final int SUPERSAMPLING_SCALE = 2;

	private FPSAnimator animator;

	private GLRenderingBackend backend = new GLRenderingBackend(null);

	public final DemoScene rootScene = new DemoScene();
	private TurboLightManager lightMgr = new TurboLightManager();

	private final USceneNode GBUFFER_COMPOSE_MODEL = new USceneNode();
	private final USceneNode FILL_SCREEN_MODEL = new USceneNode();

	private final boolean LOAD_RESOURCES_FROM_CLASSPATH = false;

	private void loadResource(USceneNode dest, String fileName) {
		if (LOAD_RESOURCES_FROM_CLASSPATH) {
			UGfxResource.loadResourceClasspath("urender/demo/model/" + fileName, UScenegraphGfxResourceLoader.getInstance(), new USceneNodeGfxResourceAdapter(dest));
		} else {
			UGfxResource.loadResourceFile(new File(fileName), UScenegraphGfxResourceLoader.getInstance(), new USceneNodeGfxResourceAdapter(dest));
		}
	}

	protected static class DefaultCaps extends GLCapabilities {

		public DefaultCaps(GLProfile glp) throws GLException {
			super(glp);
			setHardwareAccelerated(true);
			setDoubleBuffered(false);
			setAlphaBits(0);
			setRedBits(8);
			setBlueBits(8);
			setGreenBits(8);
			setStencilBits(8);
		}
	}

	float tx = 0f;
	int lastMX = -1;
	float ty = 0.5f;
	int lastMY = -1;
	float tz = 2f;
	int lastMZ = -1;
	float rx = 0f;
	float ry = 0f;

	public GLJPanelDummy() {
		this(new DefaultCaps(GLProfile.get(GLProfile.GL4)));
	}

	public GLJPanelDummy(GLCapabilities caps) {
		super(caps);
		super.addGLEventListener(this);
		prepareGfxResources();
		animator = new FPSAnimator(this, 75);
		animator.start();

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				lastMX = e.getX();
				lastMY = e.getY();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				lastMX = -1;
				lastMY = -1;
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				boolean right = SwingUtilities.isRightMouseButton(e);
				if (lastMX != -1) {
					if (right) {
						tx -= (e.getX() - lastMX) / 1000f * tz;
					} else {
						ry -= (e.getX() - lastMX) / 100f;
					}
					lastMX = e.getX();
				}
				if (lastMY != -1) {
					if (right) {
						ty += (e.getY() - lastMY) / 1000f * tz;
					} else {
						rx -= (e.getY() - lastMY) / 100f;
					}
					lastMY = e.getY();
				}
			}
		});

		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				tz += e.getWheelRotation() / 10f * tz;
			}
		});
	}

	private void prepareGfxResources() {
		try {
			loadResource(GBUFFER_COMPOSE_MODEL, "GBufferComposeQuad.gfx");
			loadResource(FILL_SCREEN_MODEL, "FillScreenQuad.gfx");
		} catch (Exception ex) {
			System.err.println("Error while loading critical system resources!");
		}
	}

	@Override
	public void init(GLAutoDrawable glad) {
		backend.setGL(glad.getGL().getGL4());

		GL4 gl = glad.getGL().getGL4();

		gl.setSwapInterval(1);
	}

	@Override
	public void dispose(GLAutoDrawable glad) {

	}

	//The g-buffer pass renders to these targets
	private URenderTarget rtPosition = new URenderTarget(0, "PositionTexture", UFramebufferAttachment.COLOR, UTextureFormat.RGBA16F);
	private URenderTarget rtNormal = new URenderTarget(1, "NormalTexture", UFramebufferAttachment.COLOR, UTextureFormat.RGBA16F);
	private URenderTarget rtAlbedo = new URenderTarget(2, "AlbedoTexture", UFramebufferAttachment.COLOR, UTextureFormat.RGBA8);
	private URenderTarget rtSpecular = new URenderTarget(3, "SpecularTexture", UFramebufferAttachment.COLOR, UTextureFormat.RGBA8); //allow modifying each color intensity separately
	private URenderTarget rtEmission = new URenderTarget(4, "EmissionTexture", UFramebufferAttachment.COLOR, UTextureFormat.RGBA8);
	private URenderTarget rtSharedDepth = new URenderTarget(0, "DepthTexture", UFramebufferAttachment.DEPTH_STENCIL, UTextureFormat.DEPTH24_STENCIL8);
	//We also have to store certain material properties in a G-buffer. For our shader, that will be mainly specular/emission intensity etc.

	private UFramebuffer framebufferGbuffer = new UFramebuffer(rtPosition, rtSharedDepth, rtNormal, rtAlbedo, rtSpecular, rtEmission);

	//The forward-rendered pass renders directly to this target
	//The deferred-rendered pass should render to it prior with setting gl_FragDepth to the depth texture value
	private URenderTarget rtForward = new URenderTarget(0, "ForwardSurface", UFramebufferAttachment.COLOR, UTextureFormat.RGBA8);
	private UFramebuffer framebufferForward = new UFramebuffer(rtForward, rtSharedDepth); //shared depth buffer for both deferred and forward shading

	//A fullscreen quad should blit ForwardSurface to the screen, possibly through a postprocessing shader
	private void clearViewport(RenderingBackend backend, boolean keepDepth, boolean transparentClearColor) {
		if (transparentClearColor) {
			backend.clearColorSet(0f, 0.0f, 0.0f, 0.0f);
		} else {
			backend.clearColorSet(0f, 0.5f, 0.9f, 1.0f);
		}
		backend.clearDepthSet(1.0f);

		backend.renderStateDepthMaskSet(true);
		backend.renderStateColorMaskSet(true, true, true, true);
		backend.renderStateDepthTestSet(true, UTestFunction.LEQUAL);
		backend.renderStateCullingSet(UFaceCulling.BACK);
		if (keepDepth) {
			backend.clear(UClearMode.COLOR);
		} else {
			backend.clear(UClearMode.COLOR, UClearMode.DEPTH);
		}
	}

	private void drawScenePass(UGfxRenderer rnd, URenderQueue queue, int priority) {
		for (URenderQueue.URenderQueueMeshState state : queue.queue()) {
			if (priority == -1 || state.getDrawPriority() == priority) {
				rootScene.setGlobalMatrices(state);

				state.draw(rnd);
			}
		}
	}

	private void drawScene(RenderingBackend backend, UGfxRenderer rnd) {
		URenderQueue queue = rootScene.calcRenderQueue();
		queue.sort();

		for (UDrawSources drawSources : queue.drawSources()) {
			drawSources.setup(rnd);
		}

		rnd.changeShadingMethod(UMaterialDrawLayer.ShadingMethod.FORWARD);
		clearViewport(backend, false, false);

		int deferredLayerMax = queue.getMaxRenderPriority(UMaterialDrawLayer.ShadingMethod.DEFERRED);
		for (int layer = 0; layer <= deferredLayerMax; layer++) {
			//Draw geometry to G-buffer layer
			rnd.changeShadingMethod(UMaterialDrawLayer.ShadingMethod.DEFERRED);
			rnd.disableAlphaBlend();
			clearViewport(backend, true, true); //clear G-buffer, but retain depth
			drawScenePass(rnd, queue, layer);	//draw G-buffer layer

			//Blit G-buffer layer
			//if (layer == 0) {
			rnd.changeShadingMethod(UMaterialDrawLayer.ShadingMethod.FORWARD);
			backend.renderStateDepthMaskSet(false);
			rnd.enableAlphaBlend();
			GBUFFER_COMPOSE_MODEL.drawHeadless(rnd); //blit deferred framebuffer to forward surface
			backend.renderStateDepthMaskSet(true);
			//}
		}

		rnd.changeShadingMethod(UMaterialDrawLayer.ShadingMethod.FORWARD);
		rnd.enableAlphaBlend();
		drawScenePass(rnd, queue, -1);
	}
	
	private IPerfMonitor perfMonitor;
	
	public void bindPerfMonitor(IPerfMonitor m) {
		perfMonitor = m;
	}

	@Override
	public void display(GLAutoDrawable glad) {
		UCameraLookAtOrbit camera = rootScene.orbitCamera;
		camera.FOV = (float) Math.toRadians(60f);
		camera.aspect = getWidth() / (float) getHeight();
		camera.zNear = 0.1f;
		camera.zFar = 5000f;

		camera.rotation.set(rx, ry, 0f);
		camera.target.set(0f, 0f, 0f);
		camera.postTranslation.set(tx, ty, tz);

		rootScene.setTimeCounter((float) (System.currentTimeMillis() % 86400000));

		UGfxRenderer renderer = new UGfxRenderer(backend, framebufferGbuffer, framebufferForward);
		renderer.bindLightAdapter(lightMgr);

		backend.viewport(0, 0, getWidth() * SUPERSAMPLING_SCALE, getHeight() * SUPERSAMPLING_SCALE);
		renderer.setAllFramebufferResolution(getWidth() * SUPERSAMPLING_SCALE, getHeight() * SUPERSAMPLING_SCALE);

		renderer.beginScene(rootScene);
		drawScene(backend, renderer);

		renderer.changeShadingMethod(null);

		backend.viewport(0, 0, getWidth(), getHeight());
		clearViewport(backend, false, false);
		FILL_SCREEN_MODEL.drawHeadless(renderer);

		backend.flush();
		if (perfMonitor != null) {
			perfMonitor.newFrame();
		}
	}

	@Override
	public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {

	}
}
