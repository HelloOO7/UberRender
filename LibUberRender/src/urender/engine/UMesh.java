package urender.engine;

import urender.engine.shader.UShaderProgram;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import urender.api.UBufferType;
import urender.api.UBufferUsageHint;
import urender.api.UDataType;
import urender.api.UObjHandle;
import urender.api.UPrimitiveType;
import urender.api.backend.RenderingBackend;

public class UMesh extends UGfxEngineObject {

	UPrimitiveType primitiveType;

	private UObjHandle __iboHandle = new UObjHandle();
	ByteBuffer indexBuffer;
	UDataType indexBufferFormat;

	private UObjHandle __vboHandle = new UObjHandle();
	ByteBuffer vertexBuffer;

	final List<UVertexAttribute> vertexAttributes = new ArrayList<>();

	public UPrimitiveType getPrimitiveType() {
		return primitiveType;
	}

	public int getOneVertexSize() {
		int size = 0;
		for (UVertexAttribute a : vertexAttributes) {
			size += a.format.sizeof * a.elementCount;
		}
		return size;
	}

	public int getVtxAttrCount() {
		return vertexAttributes.size();
	}

	public UVertexAttribute getVtxAttr(int index) {
		return vertexAttributes.get(index);
	}

	public UDataType getIBOFormat() {
		return indexBufferFormat;
	}

	public ByteBuffer getIBO() {
		return indexBuffer;
	}

	public ByteBuffer getVBO() {
		return vertexBuffer;
	}

	public int getVertexCount() {
		return vertexBuffer.capacity() / getOneVertexSize();
	}

	public int getIndexCount() {
		return indexBuffer.capacity() / indexBufferFormat.sizeof;
	}

	private static void setupBuffer(RenderingBackend core, UBufferType type, UObjHandle handle, ByteBuffer buf) {
		if (!handle.isInitialized(core)) {
			core.bufferInit(handle);
		}
		if (handle.getAndResetForceUpload(core)) {
			core.bufferUploadData(type, handle, UBufferUsageHint.STATIC, buf, buf.capacity());
		}
	}

	public void setup(UGfxRenderer rnd) {
		RenderingBackend core = rnd.getCore();

		setupBuffer(core, UBufferType.IBO, __iboHandle, indexBuffer);
		setupBuffer(core, UBufferType.VBO, __vboHandle, vertexBuffer);
	}

	public void draw(UGfxRenderer rnd, UShaderProgram program) {
		RenderingBackend core = rnd.getCore();

		int stride = getOneVertexSize();

		for (UVertexAttribute a : vertexAttributes) {
			UObjHandle index = program.getAttributeLocation(rnd, a.shaderAttrName);
			if (index.isInitialized(core)) {
				core.bufferAttribPointer(__vboHandle, index, a.elementCount, a.format, a.unsigned, a.normalized, stride, a.offset);
			}
		}

		core.buffersDrawIndexed(__vboHandle, primitiveType, __iboHandle, indexBufferFormat, getIndexCount());

		for (UVertexAttribute a : vertexAttributes) {
			UObjHandle index = program.getAttributeLocation(rnd, a.shaderAttrName);
			if (index.isInitialized(core)) {
				core.bufferAttribDisable(__vboHandle, index);
			}
		}
	}

	@Override
	public UGfxEngineObjectType getType() {
		return UGfxEngineObjectType.MESH;
	}
}
