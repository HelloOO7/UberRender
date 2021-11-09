package urender.api.backend;

import com.jogamp.opengl.GL4;
import java.nio.Buffer;
import org.joml.Matrix4f;
import urender.api.UBufferType;
import urender.api.UBufferUsageHint;
import urender.api.UDataType;
import urender.api.UObjHandle;
import urender.api.UPrimitiveType;
import urender.api.UShaderType;
import urender.api.UTextureFaceAssignment;
import urender.api.UTextureFormat;
import urender.api.UTextureType;

public class GLRenderingBackend implements RenderingBackend {

	private GL4 gl;

	private APITranslator api = new GLAPITranslator();

	private int currentTexture = -1;
	private int currentVBO = -1;
	private int currentIBO = -1;

	private final int[] glAllocTemp = new int[1];

	public GLRenderingBackend(GL4 gl) {
		setGL(gl);
	}

	public final void setGL(GL4 gl) {
		this.gl = gl;
	}

	@Override
	public Object getIdent() {
		return gl;
	}

	private void texSetCurrent(UTextureType type, UObjHandle handle) {
		if (!handle.isCurrent(this, currentTexture)) {
			currentTexture = handle.getValue(this);
			gl.glBindTexture(api.getTextureType(type), currentTexture);
		}
	}

	private void vboSetCurrent(UObjHandle handle) {
		if (!handle.isCurrent(this, currentVBO)) {
			currentVBO = handle.getValue(this);
			System.out.println("GL Bind VBO " + currentVBO);
			gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, currentVBO);
		}
	}

	private void iboSetCurrent(UObjHandle handle) {
		if (!handle.isCurrent(this, currentIBO)) {
			currentIBO = handle.getValue(this);
			System.out.println("GL Bind IBO " + currentIBO);
			gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, currentIBO);
		}
	}

	@Override
	public void texInit(UObjHandle tex, UTextureType type) {
		synchronized (glAllocTemp) {
			gl.glGenTextures(1, glAllocTemp, 0);
			tex.initialize(this, glAllocTemp[0]);
		}
	}

	@Override
	public void texUploadData2D(UObjHandle tex, int width, int height, UTextureFormat format, UTextureFaceAssignment faceAsgn, Buffer data) {
		texSetCurrent(UTextureType.TEX2D, tex);
		gl.glTexImage2D(
			api.getTextureFaceAssignment(faceAsgn),
			0,
			api.getTextureFormatInternalFormat(format),
			width,
			height,
			0,
			api.getTextureFormatExternalFormat(format),
			api.getTextureFormatDataType(format),
			data
		);
	}

	@Override
	public void bufferInit(UObjHandle buffer) {
		synchronized (glAllocTemp) {
			gl.glGenBuffers(1, glAllocTemp, 0);
			buffer.initialize(this, glAllocTemp[0]);
		}
	}

	@Override
	public void buffersDrawIndexed(UObjHandle vbo, UPrimitiveType primitiveType, UObjHandle ibo, UDataType iboFormat, int count) {
		vboSetCurrent(vbo);
		iboSetCurrent(ibo);
		System.out.println("GL DrawElements (vbo = " + vbo.getValue(this) + ", PrimitiveType = " + primitiveType + ", Count = " + count + ", ibo = " + ibo.getValue(this) + ", IBOFormat = " + iboFormat + ")");
		gl.glDrawElements(api.getPrimitiveType(primitiveType), count, api.getDataType(iboFormat, true), 0);
	}

	@Override
	public void buffersDrawInline(UObjHandle vbo, UPrimitiveType primitiveType, int count) {
		vboSetCurrent(vbo);
		gl.glDrawArrays(api.getPrimitiveType(primitiveType), 0, count);
	}

	@Override
	public void bufferUploadData(UBufferType target, UObjHandle buffer, UBufferUsageHint usage, Buffer data, int size) {
		switch (target) {
			case IBO:
				iboSetCurrent(buffer);
				break;
			case VBO:
				vboSetCurrent(buffer);
				break;
		}
		System.out.println("GL BufferData (target = " + target + ", size = " + size + ", usage = " + usage);
		gl.glBufferData(api.getBufferTargetType(target), size, data, api.getBufferUsage(usage));
	}
	
	@Override
	public void bufferAttribPointer(UObjHandle vbo, int index, int size, UDataType type, boolean unsigned, boolean normalized, int stride, long offset) {
		vboSetCurrent(vbo);
		System.out.println("GL VertexAttrib (index = " + index + ", size = " + size + ", type = " + type + " (unsigned " + unsigned + ", normalized " + normalized + "), stride = " + stride + ", offset = " + offset + ")");
		gl.glEnableVertexAttribArray(index);
		gl.glVertexAttribPointer(
			index, 
			size, 
			api.getDataType(type, unsigned), 
			normalized, 
			stride, 
			offset
		);
	}

	@Override
	public void shaderInit(UObjHandle shader, UShaderType type) {
		shader.initialize(this, gl.glCreateShader(api.getShaderType(type)));
	}

	@Override
	public void programInit(UObjHandle program) {
		program.initialize(this, gl.glCreateProgram());
	}

	@Override
	public void programUse(UObjHandle program) {
		gl.glUseProgram(program.getValue(this));
	}

	@Override
	public void programLink(UObjHandle program) {
		gl.glLinkProgram(program.getValue(this));
	}

	private final String[] shaderStrTemp = new String[1];
	private final int[] shaderIntTemp = new int[1];

	@Override
	public void shaderCompileSource(UObjHandle shader, String source) {
		synchronized (shaderStrTemp) {
			shaderStrTemp[0] = source;
			shaderIntTemp[0] = source.length();
			gl.glShaderSource(shader.getValue(this), 1, shaderStrTemp, shaderIntTemp, 0);
			gl.glCompileShader(shader.getValue(this));
		}
	}

	@Override
	public void programAttachShader(UObjHandle program, UObjHandle shader) {
		gl.glAttachShader(program.getValue(this), shader.getValue(this));
	}

	@Override
	public void uniformLocationInit(UObjHandle program, UObjHandle uniform, String name) {
		int loc = gl.glGetUniformLocation(program.getValue(this), name);
		uniform.initialize(this, loc);
		if (loc == -1) {
			System.err.println("Unresolved uniform: " + name + " for program " + program.getValue(this));
		}
	}
	
	private final float[] mat4_tmp = new float[16];

	@Override
	public void uniformMat4(UObjHandle location, Matrix4f matrix) {
		matrix.get(mat4_tmp);
		gl.glUniformMatrix4fv(location.getValue(this), 1, false, mat4_tmp, 0);
	}
	
	@Override
	public void flush() {
		currentIBO = -1;
		currentTexture = -1;
		currentVBO = -1;
		gl.glFlush();
	}

	@Override
	public void bufferAttribDisable(UObjHandle vbo, int index) {
		vboSetCurrent(vbo);
		gl.glDisableVertexAttribArray(index);
	}
}
