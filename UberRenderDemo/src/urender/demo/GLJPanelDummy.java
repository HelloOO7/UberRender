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
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import urender.api.UFramebufferAttachment;
import urender.api.UTextureFormat;
import urender.api.backend.GLRenderingBackend;
import urender.engine.UDrawSources;
import urender.engine.UDrawState;
import urender.engine.UFramebuffer;
import urender.engine.UGfxRenderer;
import urender.engine.UMaterial;
import urender.engine.UMaterialDrawLayer;
import urender.engine.URenderTarget;
import urender.engine.shader.UUniform;
import urender.engine.shader.UUniformFloat;
import urender.engine.shader.UUniformMatrix3;
import urender.engine.shader.UUniformMatrix4;
import urender.g3dio.ugfx.UGfxResource;
import urender.scenegraph.UCameraLookAtOrbit;
import urender.scenegraph.URenderQueue;
import urender.scenegraph.UScene;
import urender.scenegraph.io.USceneNodeGfxResourceAdapter;
import urender.scenegraph.USceneNode;
import urender.scenegraph.io.UScenegraphGfxResourceLoader;

public class GLJPanelDummy extends GLJPanel implements GLAutoDrawable, GLEventListener {

	private FPSAnimator animator;

	private GLRenderingBackend backend = new GLRenderingBackend(null);

	//private static final USceneNode RENDER_TEST_MODEL = OBJModelLoader.createOBJModelSceneNode("urender/demo/model", "untitled_uv.obj");
	private static final USceneNode RENDER_TEST_MODEL = new USceneNode();
	private static final USceneNode TORUS_MODEL = new USceneNode();
	private static final USceneNode GBUFFER_COMPOSE_MODEL = new USceneNode();
	private static final USceneNode FILL_SCREEN_MODEL = new USceneNode();

	static {
		//UGfxResource.loadResourceClasspath("urender/demo/model/Demo.gfx", UScenegraphGfxResourceLoader.getInstance(), new USceneNodeGfxResourceAdapter(RENDER_TEST_MODEL));
		UGfxResource.loadResourceFile(new File("Demo.gfx"), UScenegraphGfxResourceLoader.getInstance(), new USceneNodeGfxResourceAdapter(RENDER_TEST_MODEL));
		UGfxResource.loadResourceFile(new File("Torus.gfx"), UScenegraphGfxResourceLoader.getInstance(), new USceneNodeGfxResourceAdapter(TORUS_MODEL));
		UGfxResource.loadResourceFile(new File("GBufferComposeQuad.gfx"), UScenegraphGfxResourceLoader.getInstance(), new USceneNodeGfxResourceAdapter(GBUFFER_COMPOSE_MODEL));
		UGfxResource.loadResourceFile(new File("FillScreenQuad.gfx"), UScenegraphGfxResourceLoader.getInstance(), new USceneNodeGfxResourceAdapter(FILL_SCREEN_MODEL));
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

	@Override
	public void init(GLAutoDrawable glad) {
		backend.setGL(glad.getGL().getGL4());

		GL4 gl = glad.getGL().getGL4();

		worldMtx.identity();

		gl.setSwapInterval(1);
		gl.glDepthMask(true);
		gl.glColorMask(true, true, true, true);
		gl.glEnable(GL4.GL_DEPTH_TEST);
		gl.glDepthFunc(GL4.GL_LEQUAL);
		gl.glClear(GL4.GL_DEPTH_BUFFER_BIT | GL4.GL_COLOR_BUFFER_BIT | GL4.GL_STENCIL_BUFFER_BIT);

		rootScene.addGlobalUniform(worldMtxU);
		rootScene.addGlobalUniform(projMtxU);
		rootScene.addGlobalUniform(normMtxU);
		rootScene.addGlobalUniform(time);

		rootScene.addChild(RENDER_TEST_MODEL);
		rootScene.addChild(TORUS_MODEL);
		rootScene.camera = camera;
	}

	@Override
	public void dispose(GLAutoDrawable glad) {

	}

	private UScene rootScene = new UScene();
	private UCameraLookAtOrbit camera = new UCameraLookAtOrbit();

	private Matrix4f worldMtx = new Matrix4f();
	private Matrix4f projMtx = new Matrix4f();
	private Matrix3f normMtx = new Matrix3f();

	private UUniformMatrix4 worldMtxU = new UUniformMatrix4("UBR_WorldMatrix", worldMtx);
	private UUniformMatrix4 projMtxU = new UUniformMatrix4("UBR_ProjectionMatrix", projMtx);
	private UUniformMatrix3 normMtxU = new UUniformMatrix3("UBR_NormalMatrix", normMtx);
	private UUniformFloat time = new UUniformFloat("time", 0f);

	//The g-buffer pass renders to these targets
	private URenderTarget rtPosition = new URenderTarget(0, "PositionTexture", UFramebufferAttachment.COLOR, UTextureFormat.RGBA16F);
	private URenderTarget rtNormal = new URenderTarget(1, "NormalTexture", UFramebufferAttachment.COLOR, UTextureFormat.RGBA16F);
	private URenderTarget rtAlbedo = new URenderTarget(2, "AlbedoTexture", UFramebufferAttachment.COLOR, UTextureFormat.RGBA8);
	private URenderTarget rtDepth = new URenderTarget(0, "DepthTexture", UFramebufferAttachment.DEPTH_STENCIL, UTextureFormat.DEPTH24_STENCIL8);

	private UFramebuffer framebufferGbuffer = new UFramebuffer(rtPosition, rtDepth, rtNormal, rtAlbedo);

	//The forward-rendered pass renders directly to this target
	//The deferred-rendered pass should render to it prior with setting gl_FragDepth to the depth texture value
	private URenderTarget rtForward = new URenderTarget(0, "ForwardSurface", UFramebufferAttachment.COLOR, UTextureFormat.RGBA8);
	private UFramebuffer framebufferForward = new UFramebuffer(rtForward, rtDepth); //shared depth buffer for both deferred and forward shading

	//A fullscreen quad should blit ForwardSurface to the screen, possibly through a postprocessing shader
	private void clearViewport(GL4 gl, boolean keepDepth) {
		gl.glViewport(0, 0, getWidth(), getHeight());

		gl.glClearColor(0f, 0.5f, 0.9f, 1f);
		if (!keepDepth) {
			gl.glClearDepthf(1f);
		}

		gl.glDepthMask(true);
		gl.glColorMask(true, true, true, true);
		gl.glEnable(GL4.GL_DEPTH_TEST);
		gl.glDepthFunc(GL4.GL_LEQUAL);
		gl.glEnable(GL4.GL_CULL_FACE);
		gl.glCullFace(GL4.GL_BACK);
		gl.glClear((keepDepth ? 0 : GL4.GL_DEPTH_BUFFER_BIT) | GL4.GL_COLOR_BUFFER_BIT);
	}

	private void drawScenePass(UGfxRenderer rnd, URenderQueue queue) {
		for (URenderQueue.URenderQueueMeshState state : queue.queue()) {
			projMtx.set(state.nodeState.projectionMatrix);
			worldMtx.set(state.nodeState.viewMatrix);
			worldMtx.mul(state.nodeState.modelMatrix);
			state.nodeState.modelMatrix.normal(normMtx);

			state.draw(rnd);
		}
	}

	private void drawScene(GL4 gl, UGfxRenderer rnd) {
		URenderQueue queue = rootScene.calcRenderQueue();

		for (UDrawSources drawSources : queue.drawSources()) {
			drawSources.setup(rnd);
		}

		rnd.changeShadingMethod(UMaterialDrawLayer.ShadingMethod.DEFERRED);
		clearViewport(gl, false);
		drawScenePass(rnd, queue);
		rnd.changeShadingMethod(UMaterialDrawLayer.ShadingMethod.FORWARD);
		clearViewport(gl, true);
		gl.glDepthMask(false);
		GBUFFER_COMPOSE_MODEL.drawHeadless(rnd); //blit deferred framebuffer to forward surface
		gl.glDepthMask(true);
		gl.glEnable(GL4.GL_BLEND);
		gl.glBlendFunc(GL4.GL_SRC_ALPHA, GL4.GL_ONE_MINUS_SRC_ALPHA);
		drawScenePass(rnd, queue);
	}

	@Override
	public void display(GLAutoDrawable glad) {
		camera.FOV = (float) Math.toRadians(60f);
		camera.aspect = getWidth() / (float) getHeight();
		camera.zNear = 0.1f;
		camera.zFar = 500f;

		camera.rotation.set(rx, ry, 0f);
		camera.target.set(0f, 0f, 0f);
		camera.postTranslation.set(tx, ty, tz);
		
		time.set((float)(System.currentTimeMillis() % 86400000));

		GL4 gl = glad.getGL().getGL4();

		UGfxRenderer renderer = new UGfxRenderer(backend, framebufferGbuffer, framebufferForward);

		renderer.setAllFramebufferResolution(getWidth(), getHeight());

		drawScene(gl, renderer);

		renderer.changeShadingMethod(null);

		clearViewport(gl, false);
		FILL_SCREEN_MODEL.drawHeadless(renderer);

		backend.flush();
	}

	@Override
	public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {

	}
}
