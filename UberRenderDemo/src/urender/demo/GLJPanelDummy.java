package urender.demo;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.AnimatorBase;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.SwingUtilities;
import urender.api.backend.GLRenderingBackend;
import urender.demo.perf.IPerfMonitor;
import urender.scenegraph.UGfxRenderer;
import urender.scenegraph.UCameraLookAtOrbit;

public class GLJPanelDummy extends GLJPanel implements GLAutoDrawable, GLEventListener {

	private static final int SUPERSAMPLING_SCALE = 2;

	private AnimatorBase animator;

	private GLRenderingBackend backend = new GLRenderingBackend();
	private UGfxRenderer renderer;

	public final DemoScene rootScene = new DemoScene();

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
		animator = new Animator(this);
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

		renderer = new DemoRenderEngine(backend);
	}

	private Integer backendIdentity = System.identityHashCode(this);

	@Override
	public void init(GLAutoDrawable glad) {
		backendIdentity *= 7;
		backend.setGL(glad.getGL().getGL4(), backendIdentity);

		GL4 gl = glad.getGL().getGL4();

		gl.setSwapInterval(1);
	}

	public int getBackendIdentity() {
		return backendIdentity;
	}

	@Override
	public void dispose(GLAutoDrawable glad) {

	}

	private IPerfMonitor perfMonitor;

	public void bindPerfMonitor(IPerfMonitor m) {
		perfMonitor = m;
	}

	@Override
	public void display(GLAutoDrawable glad) {
		long displayLoopStart = System.nanoTime();

		UCameraLookAtOrbit camera = rootScene.orbitCamera;
		camera.FOV = (float) Math.toRadians(60f);
		camera.aspect = getWidth() / (float) getHeight();
		camera.zNear = 0.1f;
		camera.zFar = 5000f;

		camera.rotation.set(rx, ry, 0f);
		camera.target.set(0f, 0f, 0f);
		camera.postTranslation.set(tx, ty, tz);

		rootScene.setTimeCounter((float) (System.currentTimeMillis() % 86400000));

		backend.viewport(0, 0, getWidth() * SUPERSAMPLING_SCALE, getHeight() * SUPERSAMPLING_SCALE);
		renderer.setAllFramebufferResolution(getWidth() * SUPERSAMPLING_SCALE, getHeight() * SUPERSAMPLING_SCALE);

		renderer.drawScene(rootScene);

		backend.viewport(0, 0, getWidth(), getHeight());

		renderer.blitScreen();

		backend.flush();

		long displayLoopTime = System.nanoTime() - displayLoopStart;

		if (perfMonitor != null) {
			perfMonitor.newFrame();
			perfMonitor.setDisplayLoopTime(displayLoopTime);
		}
	}

	@Override
	public void reshape(GLAutoDrawable glad, int x, int y, int width, int height) {

	}
}
