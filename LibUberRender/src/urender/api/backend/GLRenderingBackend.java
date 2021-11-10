package urender.api.backend;

import com.jogamp.opengl.GL4;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import urender.api.UBufferType;
import urender.api.UBufferUsageHint;
import urender.api.UDataType;
import urender.api.UObjHandle;
import urender.api.UPrimitiveType;
import urender.api.UShaderType;
import urender.api.UTextureFaceAssignment;
import urender.api.UTextureFormat;
import urender.api.UTextureMagFilter;
import urender.api.UTextureMinFilter;
import urender.api.UTextureType;
import urender.api.UTextureWrap;

public class GLRenderingBackend implements RenderingBackend {

	private static final boolean DEBUG = false;

	private GL4 gl;

	private APITranslator api = new GLAPITranslator();

	private int currentTexture = -1;
	private int currentVBO = -1;
	private int currentIBO = -1;
	private int currentTexUnit = -1;

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
			if (DEBUG) {
				System.out.println("GL Bind VBO " + currentVBO);
			}
			gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, currentVBO);
		}
	}

	private void iboSetCurrent(UObjHandle handle) {
		if (!handle.isCurrent(this, currentIBO)) {
			currentIBO = handle.getValue(this);
			if (DEBUG) {
				System.out.println("GL Bind IBO " + currentIBO);
			}
			gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, currentIBO);
		}
	}

	private void texUnitSetCurrent(UObjHandle handle) {
		if (!handle.isCurrent(this, currentTexUnit)) {
			currentTexUnit = handle.getValue(this);
			if (DEBUG) {
				System.out.println("GL Bind TexUnit " + currentTexUnit);
			}
			gl.glActiveTexture(currentTexUnit);
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
		data.rewind();
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
		if (DEBUG) {
			System.out.println("GL DrawElements (vbo = " + vbo.getValue(this) + ", PrimitiveType = " + primitiveType + ", Count = " + count + ", ibo = " + ibo.getValue(this) + ", IBOFormat = " + iboFormat + ")");
		}
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
		data.rewind();
		if (DEBUG) {
			System.out.println("GL BufferData (target = " + target + ", size = " + size + ", usage = " + usage);
		}
		gl.glBufferData(api.getBufferTargetType(target), size, data, api.getBufferUsage(usage));
	}

	@Override
	public void bufferAttribPointer(UObjHandle vbo, UObjHandle index, int size, UDataType type, boolean unsigned, boolean normalized, int stride, long offset) {
		vboSetCurrent(vbo);
		int ival = index.getValue(this);
		if (DEBUG) {
			System.out.println("GL VertexAttrib (index = " + index.getValue(this) + ", size = " + size + ", type = " + type + " (unsigned " + unsigned + ", normalized " + normalized + "), stride = " + stride + ", offset = " + offset + ")");
		}
		gl.glEnableVertexAttribArray(ival);
		gl.glVertexAttribPointer(
			ival,
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
		int hnd = program.getValue(this);
		gl.glLinkProgram(hnd);
		int[] status = new int[1];

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
	public void bufferAttribDisable(UObjHandle vbo, UObjHandle index) {
		vboSetCurrent(vbo);
		gl.glDisableVertexAttribArray(index.getValue(this));
	}

	@Override
	public void attributeLocationInit(UObjHandle program, UObjHandle atribute, String name) {
		atribute.initialize(this, gl.glGetAttribLocation(program.getValue(this), name));
	}

	private final float[] mat3_tmp = new float[9];

	@Override
	public void uniformMat3(UObjHandle location, Matrix3f matrix) {
		matrix.get(mat3_tmp);
		gl.glUniformMatrix3fv(location.getValue(this), 1, false, mat3_tmp, 0);
	}

	@Override
	public void texUnitInit(UObjHandle texUnit, int unitIndex) {
		texUnit.initialize(this, unitIndex);
	}

	@Override
	public void uniformSampler(UObjHandle location, UObjHandle texUnit) {
		gl.glUniform1i(location.getValue(this), texUnit.getValue(this));
	}

	@Override
	public void texUnitSetTexture(UObjHandle texUnit, UTextureType type, UObjHandle texture) {
		texUnitSetCurrent(texUnit);
		texSetCurrent(type, texture);
	}

	@Override
	public void texSetParams(UObjHandle texture, UTextureType type, UTextureWrap wrapU, UTextureWrap wrapV, UTextureMagFilter magFilter, UTextureMinFilter minFilter) {
		texSetCurrent(type, texture);
		int tt = api.getTextureType(type);
		gl.glTexParameteri(tt, GL4.GL_TEXTURE_WRAP_S, api.getTextureWrap(wrapU));
		gl.glTexParameteri(tt, GL4.GL_TEXTURE_WRAP_T, api.getTextureWrap(wrapV));
		gl.glTexParameteri(tt, GL4.GL_TEXTURE_MAG_FILTER, api.getTextureMagFilter(magFilter));
		gl.glTexParameteri(tt, GL4.GL_TEXTURE_MIN_FILTER, api.getTextureMinFilter(minFilter));
	}
}
