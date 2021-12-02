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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import javax.swing.SwingUtilities;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import urender.api.UDataType;
import urender.api.UPrimitiveType;
import urender.api.backend.GLRenderingBackend;
import urender.engine.UGfxRenderer;
import urender.engine.UMesh;
import urender.engine.UVertexAttribute;
import urender.engine.shader.UUniformMatrix3;
import urender.engine.shader.UUniformMatrix4;
import urender.g3dio.generic.OBJModelLoader;
import urender.scenegraph.USceneNode;

public class GLJPanelDummy extends GLJPanel implements GLAutoDrawable, GLEventListener {

	private FPSAnimator animator;

	private GLRenderingBackend backend = new GLRenderingBackend(null);

	private static final UMesh RENDER_TEST_MESH = generateRenderTestMesh();

	private static final USceneNode RENDER_TEST_MODEL = OBJModelLoader.createOBJModelSceneNode("urender/demo/model", "untitled_uv.obj");

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

	static UMesh generateRenderTestMesh() {
		UMesh mesh = new UMesh();
		mesh.name = "RenderDemo";
		mesh.primitiveType = UPrimitiveType.TRIS;
		mesh.indexBufferFormat = UDataType.INT8;
		UVertexAttribute pos = new UVertexAttribute();
		pos.elementCount = 3;
		pos.format = UDataType.FLOAT32;
		pos.normalized = false;
		pos.unsigned = false;
		pos.offset = 0;
		pos.shaderAttrName = "a_Position";
		mesh.vertexAttributes.add(pos);

		mesh.vertexBuffer = ByteBuffer.allocateDirect(6 * 3 * Float.BYTES);
		mesh.vertexBuffer.order(ByteOrder.LITTLE_ENDIAN);

		mesh.vertexBuffer.putFloat(-.5f);
		mesh.vertexBuffer.putFloat(1f);
		mesh.vertexBuffer.putFloat(-.5f);

		mesh.vertexBuffer.putFloat(-.5f);
		mesh.vertexBuffer.putFloat(0f);
		mesh.vertexBuffer.putFloat(0f);

		mesh.vertexBuffer.putFloat(.5f);
		mesh.vertexBuffer.putFloat(0f);
		mesh.vertexBuffer.putFloat(-.5f);

		mesh.vertexBuffer.putFloat(.5f);
		mesh.vertexBuffer.putFloat(0f);
		mesh.vertexBuffer.putFloat(-.5f);

		mesh.vertexBuffer.putFloat(.5f);
		mesh.vertexBuffer.putFloat(2f);
		mesh.vertexBuffer.putFloat(-.5f);

		mesh.vertexBuffer.putFloat(-.5f);
		mesh.vertexBuffer.putFloat(1f);
		mesh.vertexBuffer.putFloat(-.5f);

		mesh.vertexBuffer.flip();

		/*mesh.vertexBuffer.putFloat(.5f);
		mesh.vertexBuffer.putFloat(1f);
		mesh.vertexBuffer.putFloat(-.5f);*/
		mesh.indexBuffer = ByteBuffer.allocateDirect(6);
		mesh.indexBuffer.order(ByteOrder.LITTLE_ENDIAN);
		mesh.indexBuffer.put((byte) 0);
		mesh.indexBuffer.put((byte) 1);
		mesh.indexBuffer.put((byte) 2);
		mesh.indexBuffer.put((byte) 3);
		mesh.indexBuffer.put((byte) 4);
		mesh.indexBuffer.put((byte) 5);
		mesh.indexBuffer.flip();

		return mesh;
	}

	private String readStringResource(String path) {
		Scanner s = new Scanner(GLJPanelDummy.class.getClassLoader().getResourceAsStream(path), StandardCharsets.UTF_8);
		String text = s.useDelimiter("\\A").next();
		s.close();
		return text;
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
		RENDER_TEST_MODEL.uniforms.add(worldMtxU);
		RENDER_TEST_MODEL.uniforms.add(projMtxU);
		RENDER_TEST_MODEL.uniforms.add(normMtxU);
	}

	@Override
	public void dispose(GLAutoDrawable glad) {

	}

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

		projMtx.setPerspective((float) Math.toRadians(60f), getWidth() / (float) getHeight(), 0.1f, 500f);
		worldMtx.identity();
		worldMtx.rotateYXZ(ry, rx, 0f);
		worldMtx.translate(tx, ty, tz);
		worldMtx.invert();
		worldMtx.normal(normMtx);

		/*RENDER_TEST_MESH.setData(renderer);
		RENDER_TEST_MESH.draw(renderer);*/
		RENDER_TEST_MODEL.drawAllModels(renderer);

		backend.flush();
	}

	@Override
	public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {

	}
}
