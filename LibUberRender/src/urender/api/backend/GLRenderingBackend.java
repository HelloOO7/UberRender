package urender.api.backend;

import com.jogamp.opengl.GL4;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import urender.api.UBufferType;
import urender.api.UBufferUsageHint;
import urender.api.UDataType;
import urender.api.UFramebufferAttachment;
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

	private final Map<UTextureType, TextureStateManager> currentTextures = new HashMap<>();

	private final StateManager currentVBO = new StateManager() {
		@Override
		public void bind(int handle) {
			gl.glBindBuffer(GL4.GL_ARRAY_BUFFER, handle);
		}
	};
	private final StateManager currentIBO = new StateManager() {
		@Override
		public void bind(int handle) {
			gl.glBindBuffer(GL4.GL_ELEMENT_ARRAY_BUFFER, handle);
		}
	};
	private final StateManager currentTexUnit = new StateManager() {
		@Override
		public void bind(int handle) {
			gl.glActiveTexture(GL4.GL_TEXTURE0 + handle);
		}
	};
	private final StateManager currentFramebuffer = new StateManager() {
		@Override
		public void bind(int handle) {
			gl.glBindFramebuffer(GL4.GL_FRAMEBUFFER, handle);
		}
	};
	private final StateManager currentRenderBuffer = new StateManager() {
		@Override
		public void bind(int handle) {
			gl.glBindRenderbuffer(GL4.GL_RENDERBUFFER, handle);
		}
	};

	private void initState() {
		for (UTextureType t : UTextureType.values()) {
			currentTextures.put(t, new TextureStateManager(api.getTextureType(t)));
		}
	}

	private final int[] glAllocTemp = new int[1];

	public GLRenderingBackend(GL4 gl) {
		initState();
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
		currentTextures.get(type).setCurrent(handle);
	}

	private void vboSetCurrent(UObjHandle handle) {
		currentVBO.setCurrent(handle);
	}

	private void iboSetCurrent(UObjHandle handle) {
		currentIBO.setCurrent(handle);
	}

	private void texUnitSetCurrent(UObjHandle handle) {
		currentTexUnit.setCurrent(handle);
	}

	private void fbSetCurrent(UObjHandle handle) {
		if (handle == null) {
			currentFramebuffer.setCurrent(0);
		} else {
			currentFramebuffer.setCurrent(handle);
		}
	}

	private void rbSetCurrent(UObjHandle handle) {
		currentRenderBuffer.setCurrent(handle);
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
		if (data != null) {
			data.rewind();
		}
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

	public static boolean printShaderError(GL4 gl, int shader) {
		int[] status = new int[1];
		gl.glGetShaderiv(shader, GL4.GL_COMPILE_STATUS, status, 0);
		if (status[0] == GL4.GL_FALSE) {
			int[] maxErrLen = new int[1];
			gl.glGetShaderiv(shader, GL4.GL_INFO_LOG_LENGTH, maxErrLen, 0);
			if (maxErrLen[0] > 0) {
				byte[] infoLog = new byte[maxErrLen[0]];
				gl.glGetShaderInfoLog(shader, maxErrLen[0], maxErrLen, 0, infoLog, 0);
				System.err.println(new String(infoLog, StandardCharsets.US_ASCII));
				return true;
			}
		}
		return false;
	}

	@Override
	public void shaderCompileSource(UObjHandle shader, String source) {
		synchronized (shaderStrTemp) {
			shaderStrTemp[0] = source;
			shaderIntTemp[0] = source.length();
			int handle = shader.getValue(this);
			gl.glShaderSource(handle, 1, shaderStrTemp, shaderIntTemp, 0);
			gl.glCompileShader(handle);
			if (printShaderError(gl, handle)) {
				System.err.println("- SOURCE DUMP -");
				System.err.println(source);
				System.err.println("---------------");
			}
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
	public void uniformMat4v(UObjHandle location, Matrix4f... matrices) {
		float[] tmp = new float[matrices.length << 4];
		for (int i = 0; i < matrices.length; i++) {
			matrices[i].get(tmp, i << 4);
		}
		gl.glUniformMatrix4fv(location.getValue(this), matrices.length, false, tmp, 0);
	}

	@Override
	public void flush() {
		currentIBO.flush();
		currentVBO.flush();
		currentTexUnit.flush();
		for (TextureStateManager tex : currentTextures.values()) {
			tex.flush();
		}
		gl.glFlush();
	}

	@Override
	public void bufferAttribDisable(UObjHandle vbo, UObjHandle index) {
		vboSetCurrent(vbo);
		gl.glDisableVertexAttribArray(index.getValue(this));
	}

	@Override
	public void attributeLocationInit(UObjHandle program, UObjHandle atribute, String name) {
		int loc = gl.glGetAttribLocation(program.getValue(this), name);
		/*if (loc == -1) {
			throw new RuntimeException("Could not resolve shader attribute " + name);
		}*/
		atribute.initialize(this, loc);
	}

	private final float[] mat3_tmp = new float[9];

	@Override
	public void uniformMat3(UObjHandle location, Matrix3f matrix) {
		matrix.get(mat3_tmp);
		gl.glUniformMatrix3fv(location.getValue(this), 1, false, mat3_tmp, 0);
	}

	@Override
	public void uniformMat3v(UObjHandle location, Matrix3f... matrices) {
		float[] tmp = new float[9 * matrices.length];
		for (int i = 0; i < matrices.length; i++) {
			matrices[i].get(tmp, i * 9);
		}
		gl.glUniformMatrix3fv(location.getValue(this), matrices.length, false, tmp, 0);
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

	@Override
	public void uniformInt(UObjHandle location, int value) {
		gl.glUniform1i(location.getValue(this), value);
	}

	@Override
	public void uniformIntv(UObjHandle location, int... value) {
		gl.glUniform1iv(location.getValue(this), value.length, value, 0);
	}

	@Override
	public void uniformFloat(UObjHandle location, float value) {
		gl.glUniform1f(location.getValue(this), value);
	}

	@Override
	public void uniformFloatv(UObjHandle location, float... value) {
		gl.glUniform1fv(location.getValue(this), value.length, value, 0);
	}

	@Override
	public void uniformVec2(UObjHandle location, Vector2f vector) {
		gl.glUniform2f(location.getValue(this), vector.x, vector.y);
	}

	@Override
	public void uniformVec2v(UObjHandle location, Vector2f... vectors) {
		float[] tmp = new float[vectors.length << 1];
		for (int i = 0, j = 0; i < vectors.length; i++, j += 2) {
			tmp[j] = vectors[i].x;
			tmp[j + 1] = vectors[i].y;
		}
		gl.glUniform2fv(location.getValue(this), vectors.length, tmp, 0);
	}

	@Override
	public void uniformVec3(UObjHandle location, Vector3f vector) {
		gl.glUniform3f(location.getValue(this), vector.x, vector.y, vector.z);
	}

	@Override
	public void uniformVec3v(UObjHandle location, Vector3f... vectors) {
		float[] tmp = new float[vectors.length * 3];
		for (int i = 0, j = 0; i < vectors.length; i++, j += 3) {
			tmp[j] = vectors[i].x;
			tmp[j + 1] = vectors[i].y;
			tmp[j + 2] = vectors[i].z;
		}
		gl.glUniform3fv(location.getValue(this), vectors.length, tmp, 0);
	}

	@Override
	public void uniformVec4(UObjHandle location, Vector4f vector) {
		gl.glUniform4f(location.getValue(this), vector.x, vector.y, vector.z, vector.w);
	}

	@Override
	public void uniformVec4v(UObjHandle location, Vector4f... vectors) {
		float[] tmp = new float[vectors.length << 2];
		for (int i = 0, j = 0; i < vectors.length; i++, j += 4) {
			tmp[j] = vectors[i].x;
			tmp[j + 1] = vectors[i].y;
			tmp[j + 2] = vectors[i].z;
			tmp[j + 3] = vectors[i].w;
		}
		gl.glUniform4fv(location.getValue(this), vectors.length, tmp, 0);
	}

	@Override
	public void renderBufferInit(UObjHandle rb) {
		gl.glGenRenderbuffers(1, glAllocTemp, 0);
		rb.initialize(this, glAllocTemp[0]);
	}

	@Override
	public void renderBufferStorage(UObjHandle rb, UTextureFormat format, int width, int height) {
		rbSetCurrent(rb);
		gl.glRenderbufferStorage(GL4.GL_RENDERBUFFER, api.getTextureFormatInternalFormat(format), width, height);
	}

	@Override
	public void framebufferInit(UObjHandle fb) {
		gl.glGenFramebuffers(1, glAllocTemp, 0);
		fb.initialize(this, glAllocTemp[0]);
	}

	@Override
	public void framebufferResetScreen() {
		fbSetCurrent(null);
	}

	@Override
	public void drawBufferTextureSet(UObjHandle fb, UObjHandle fbTexture, UObjHandle drawBufferHandle) {
		fbSetCurrent(fb);

		gl.glFramebufferTexture(GL4.GL_FRAMEBUFFER, drawBufferHandle.getValue(this), fbTexture.getValue(this), 0);
	}

	@Override
	public void drawBufferRenderbufferSet(UObjHandle fb, UObjHandle fbRenderbuffer, UObjHandle drawBufferHandle) {
		fbSetCurrent(fb);
		gl.glFramebufferRenderbuffer(GL4.GL_FRAMEBUFFER, drawBufferHandle.getValue(this), GL4.GL_RENDERBUFFER, fbRenderbuffer.getValue(this));
	}

	@Override
	public void drawBuffersDefine(UObjHandle fb, UObjHandle... drawBuffers) {
		fbSetCurrent(fb);
		int[] dbInts = new int[drawBuffers.length];
		for (int i = 0; i < dbInts.length; i++) {
			dbInts[i] = drawBuffers[i] == null ? GL4.GL_NONE : drawBuffers[i].getValue(this);
		}
		gl.glDrawBuffers(dbInts.length, dbInts, 0);
	}

	@Override
	public void framebufferBind(UObjHandle fb) {
		fbSetCurrent(fb);
	}

	@Override
	public void drawBufferInit(UObjHandle drawBuffer, UFramebufferAttachment attachment, int attachmentIndex) {
		drawBuffer.initialize(this, api.getFramebufferAttachment(attachment, attachmentIndex));
	}

	private abstract class StateManager {

		private int currentHandle = -1;

		public void setCurrent(int value) {
			currentHandle = value;
			bind(value);
		}

		public void setCurrent(UObjHandle handle) {
			if (!handle.isCurrent(GLRenderingBackend.this, currentHandle)) {
				setCurrent(handle.getValue(GLRenderingBackend.this));
			}
		}

		public void flush() {
			currentHandle = -1;
		}

		public abstract void bind(int handle);
	}

	private class TextureStateManager extends StateManager {

		private final int type;

		public TextureStateManager(int type) {
			this.type = type;
		}

		@Override
		public void bind(int handle) {
			gl.glEnable(type);
			gl.glBindTexture(type, handle);
		}

	}
}
