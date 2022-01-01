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
import javax.swing.SwingUtilities;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import urender.api.backend.GLRenderingBackend;
import urender.engine.UGfxRenderer;
import urender.engine.shader.UUniformMatrix3;
import urender.engine.shader.UUniformMatrix4;
import urender.g3dio.ugfx.UGfxResource;
import urender.scenegraph.UCamera;
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
	
	static {
		UGfxResource.loadResourceClasspath("urender/demo/model/Demo.gfx", UScenegraphGfxResourceLoader.getInstance(), new USceneNodeGfxResourceAdapter(RENDER_TEST_MODEL));
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
				display();
			}
		});

		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				tz += e.getWheelRotation() / 10f * tz;
				display();
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

		RENDER_TEST_MODEL.setup(new UGfxRenderer(backend));
		
		rootScene.addGlobalUniform(worldMtxU);
		rootScene.addGlobalUniform(projMtxU);
		rootScene.addGlobalUniform(normMtxU);
		
		rootScene.addChild(RENDER_TEST_MODEL);
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

	@Override
	public void display(GLAutoDrawable glad) {
		GL4 gl = glad.getGL().getGL4();

		gl.glViewport(0, 0, getWidth(), getHeight());

		gl.glClearColor(0f, 0.5f, 0.9f, 1f);
		gl.glClearDepthf(1f);

		gl.glDepthMask(true);
		gl.glColorMask(true, true, true, true);
		gl.glEnable(GL4.GL_DEPTH_TEST);
		gl.glDepthFunc(GL4.GL_LEQUAL);
		gl.glClear(GL4.GL_DEPTH_BUFFER_BIT | GL4.GL_COLOR_BUFFER_BIT);

		UGfxRenderer renderer = new UGfxRenderer(backend);
		
		camera.FOV = (float) Math.toRadians(60f);
		camera.aspect = getWidth() / (float) getHeight();
		camera.zNear = 0.1f;
		camera.zFar = 500f;

		camera.rotation.set(rx, ry, 0f);
		camera.target.set(0f, 0f, 0f);
		camera.postTranslation.set(tx, ty, tz);
				
		URenderQueue queue = rootScene.calcRenderQueue();
		
		for (URenderQueue.URenderQueueNodeState state : queue.queue()) {
			worldMtx.set(state.viewMatrix);
			worldMtx.mul(state.modelMatrix);
			state.modelMatrix.normal(normMtx);
			projMtx.set(state.projectionMatrix);
			state.node.drawAllModels(renderer, state);
		}

		backend.flush();
	}

	@Override
	public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {

	}
}
