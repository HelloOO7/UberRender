package urender.engine;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import urender.api.UBufferType;
import urender.api.UBufferUsageHint;
import urender.api.UDataType;
import urender.api.UObjHandle;
import urender.api.UPrimitiveType;
import urender.api.backend.RenderingBackend;

public class UMesh extends UGfxObject {

	public UPrimitiveType primitiveType;

	public UObjHandle __iboHandle = new UObjHandle();
	public ByteBuffer indexBuffer;
	public UDataType indexBufferFormat;

	public UObjHandle __vboHandle = new UObjHandle();
	public ByteBuffer vertexBuffer;
	public final List<UVertexAttribute> vertexAttributes = new ArrayList<>();

	public int getOneVertexSize() {
		int size = 0;
		for (UVertexAttribute a : vertexAttributes) {
			size += a.format.sizeof * a.elementCount;
		}
		return size;
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

	public void draw(UGfxRenderer rnd) {
		RenderingBackend core = rnd.getCore();

		int stride = getOneVertexSize();

		int index = 0;
		for (UVertexAttribute a : vertexAttributes) {
			core.bufferAttribPointer(__vboHandle, index, a.elementCount, a.format, a.unsigned, a.normalized, stride - (a.elementCount * a.format.sizeof), a.offset);
			index++;
		}

		core.buffersDrawIndexed(__vboHandle, primitiveType, __iboHandle, indexBufferFormat, getIndexCount());
		
		index = 0;
		for (UVertexAttribute a : vertexAttributes) {
			core.bufferAttribDisable(__vboHandle, index);
			index++;
		}
	}
}
