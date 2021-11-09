package urender.demo;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GL4bc;
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
import org.joml.Matrix4f;
import urender.api.UDataType;
import urender.api.UObjHandle;
import urender.api.UPrimitiveType;
import urender.api.UShaderType;
import urender.api.backend.GLRenderingBackend;
import urender.engine.UGfxRenderer;
import urender.engine.UMesh;
import urender.engine.UVertexAttribute;

public class GLJPanelDummy extends GLJPanel implements GLAutoDrawable, GLEventListener {

	private FPSAnimator animator;

	private GLRenderingBackend backend = new GLRenderingBackend(null);

	private static final UMesh RENDER_TEST_MESH = generateRenderTestMesh();

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
						tx -= (e.getX() - lastMX) / 1000f;
					}
					else {
						ry -= (e.getX() - lastMX) / 100f;
					}
					lastMX = e.getX();
				}
				if (lastMY != -1) {
					if (right) {
						ty += (e.getY() - lastMY) / 1000f;
					}
					else {
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
				tz += e.getWheelRotation() / 10f;
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

	private UObjHandle shaderProgram = new UObjHandle();
	private UObjHandle worldMatrixUniform = new UObjHandle();
	private UObjHandle projMatrixUniform = new UObjHandle();

	private String readStringResource(String path) {
		Scanner s = new Scanner(GLJPanelDummy.class.getResourceAsStream(path), StandardCharsets.UTF_8);
		String text = s.useDelimiter("\\A").next();
		s.close();
		return text;
	}

	@Override
	public void init(GLAutoDrawable glad) {
		backend.setGL(glad.getGL().getGL4());

		UObjHandle vsh = new UObjHandle();
		UObjHandle fsh = new UObjHandle();

		backend.shaderInit(vsh, UShaderType.VERTEX);
		backend.shaderInit(fsh, UShaderType.FRAGMENT);

		backend.shaderCompileSource(vsh, readStringResource("/urender/demo/shader/DemoShader.vsh"));
		backend.shaderCompileSource(fsh, readStringResource("/urender/demo/shader/DemoShader.fsh"));

		backend.programInit(shaderProgram);
		backend.programAttachShader(shaderProgram, vsh);
		backend.programAttachShader(shaderProgram, fsh);
		backend.programLink(shaderProgram);

		GL4 gl = glad.getGL().getGL4();

		int[] status = new int[1];
		int hnd = shaderProgram.getValue(backend);
		gl.glGetProgramiv(hnd, GL4.GL_LINK_STATUS, status, 0);
		if (status[0] == GL4.GL_FALSE) {
			int[] maxErrLen = new int[1];
			gl.glGetProgramiv(hnd, GL4.GL_INFO_LOG_LENGTH, maxErrLen, 0);
			if (maxErrLen[0] > 0) {
				byte[] infoLog = new byte[maxErrLen[0]];
				gl.glGetProgramInfoLog(hnd, maxErrLen[0], maxErrLen, 0, infoLog, 0);
				throw new RuntimeException(new String(infoLog, StandardCharsets.US_ASCII));
			}
		}

		backend.uniformLocationInit(shaderProgram, worldMatrixUniform, "UBR_WorldMatrix");
		backend.uniformLocationInit(shaderProgram, projMatrixUniform, "UBR_ProjectionMatrix");

		worldMtx.identity();

		gl.setSwapInterval(1);
		gl.glDepthMask(true);
		gl.glColorMask(true, true, true, true);
		gl.glEnable(GL4.GL_DEPTH_TEST);
		gl.glDepthFunc(GL4.GL_LEQUAL);
		gl.glClear(GL4.GL_DEPTH_BUFFER_BIT | GL4.GL_COLOR_BUFFER_BIT | GL4.GL_STENCIL_BUFFER_BIT);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
	}

	@Override
	public void dispose(GLAutoDrawable glad) {

	}

	private Matrix4f worldMtx = new Matrix4f();
	private Matrix4f projMtx = new Matrix4f();

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

		backend.programUse(shaderProgram);

		projMtx.setPerspective((float) Math.toRadians(60f), getWidth() / (float) getHeight(), 0.0001f, 500f);
		worldMtx.identity();
		worldMtx.rotateYXZ(ry, rx, 0f);
		worldMtx.translate(tx, ty, tz);
		worldMtx.invert();

		backend.uniformMat4(worldMatrixUniform, worldMtx);
		backend.uniformMat4(projMatrixUniform, projMtx);

		RENDER_TEST_MESH.setup(renderer);
		RENDER_TEST_MESH.draw(renderer);

		backend.flush();
	}

	@Override
	public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {

	}
}
