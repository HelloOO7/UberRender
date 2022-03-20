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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.SwingUtilities;
import urender.api.backend.GLRenderingBackend;
import urender.demo.perf.IPerfMonitor;
import urender.engine.UShadingMethod;
import urender.scenegraph.UGfxRenderer;
import urender.scenegraph.UCameraLookAtOrbit;

public class DemoSurface extends GLJPanel implements GLAutoDrawable, GLEventListener {

	private static final int SUPERSAMPLING_SCALE = 4;
	private static final boolean FIX_RESOLUTION_USE = true;
	private static final int FIX_RESOLUTION_W = 3840;
	private static final int FIX_RESOLUTION_H = 2160;

	private AnimatorBase animator;

	private GLRenderingBackend backend = new GLRenderingBackend();
	private UGfxRenderer renderer;

	private DemoScene rootScene;
	public final DemoAnimator animation = new DemoAnimator();

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

	public DemoSurface() {
		this(new DefaultCaps(GLProfile.get(GLProfile.GL4)));
	}

	private DemoSurface(GLCapabilities caps) {
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

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE && rootScene != null) {
					rootScene.camera = rootScene.camera == rootScene.animationCamera ? rootScene.orbitCamera : rootScene.animationCamera;
				}
			}
		});

		renderer = new DemoRenderEngine(backend);
	}

	public void loadScene(DemoScene scene) {
		this.rootScene = scene;
	}

	private Integer backendIdentity = System.identityHashCode(this);

	@Override
	public void init(GLAutoDrawable glad) {
		backendIdentity *= 7;
		backend.setGL(glad.getGL().getGL4(), backendIdentity);

		renderer.changeShadingMethod(UShadingMethod.FORWARD); //setup forward framebuffer

		GL4 gl = glad.getGL().getGL4();

		gl.setSwapInterval(1);
	}

	public int getBackendIdentity() {
		return backendIdentity;
	}

	@Override
	public void dispose(GLAutoDrawable glad) {
		if (rootScene != null) {
			rootScene.deleteAll(renderer);
		}
		renderer.shutdown();
	}

	private IPerfMonitor perfMonitor;

	public void bindPerfMonitor(IPerfMonitor m) {
		perfMonitor = m;
	}

	@Override
	public void display(GLAutoDrawable glad) {
		long displayLoopStart = System.nanoTime();
		
		int w = FIX_RESOLUTION_USE ? FIX_RESOLUTION_W : (getWidth() * SUPERSAMPLING_SCALE);
		int h = FIX_RESOLUTION_USE ? FIX_RESOLUTION_H : (getHeight() * SUPERSAMPLING_SCALE);

		backend.viewport(0, 0, w, h);
		renderer.setAllFramebufferResolution(w, h);

		if (rootScene != null) {
			UCameraLookAtOrbit camera = rootScene.orbitCamera;
			camera.FOV = (float) Math.toRadians(60f);
			camera.aspect = getWidth() / (float) getHeight();
			camera.zNear = 0.1f;
			camera.zFar = 5000f;

			camera.rotation.set(rx, ry, 0f);
			camera.target.set(0f, 0f, 0f);
			camera.postTranslation.set(tx, ty, tz);

			animation.apply(rootScene.animationCamera);

			rootScene.setTimeCounter((float) (System.currentTimeMillis() % 86400000));

			renderer.drawScene(rootScene);
		}

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
