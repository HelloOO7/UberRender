package urender.engine;

import urender.engine.shader.UShaderProgram;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3f;
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
	
	private final UBoundingBox aabb = new UBoundingBox();

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

	public void getAABBCenter(Vector3f dest) {
		aabb.getCenter(dest);
	}
	
	public void getAABBMin(Vector3f dest) {
		dest.set(aabb.min);
	}
	
	public void getAABBMax(Vector3f dest) {
		dest.set(aabb.max);
	}
	
	private static void setupBuffer(RenderingBackend core, UBufferType type, UObjHandle handle, ByteBuffer buf) {
		if (!handle.isInitialized(core)) {
			core.bufferInit(handle);
		}
		if (handle.getAndResetForceUpload(core)) {
			core.bufferUploadData(type, handle, UBufferUsageHint.STATIC, buf, buf.capacity());
		}
	}

	public void setup(RenderingBackend rnd) {
		setupBuffer(rnd, UBufferType.IBO, __iboHandle, indexBuffer);
		setupBuffer(rnd, UBufferType.VBO, __vboHandle, vertexBuffer);
	}

	public void draw(RenderingBackend rnd, UShaderProgram program) {
		int stride = getOneVertexSize();

		for (UVertexAttribute a : vertexAttributes) {
			UObjHandle index = program.getAttributeLocation(rnd, a.shaderAttrName);
			if (index.isValid(rnd)) {
				rnd.bufferAttribPointer(__vboHandle, index, a.elementCount, a.format, a.unsigned, a.normalized, stride, a.offset);
			}
		}

		rnd.buffersDrawIndexed(__vboHandle, primitiveType, __iboHandle, indexBufferFormat, getIndexCount());

		for (UVertexAttribute a : vertexAttributes) {
			UObjHandle index = program.getAttributeLocation(rnd, a.shaderAttrName);
			if (index.isValid(rnd)) {
				rnd.bufferAttribDisable(__vboHandle, index);
			}
		}
	}

	@Override
	public UGfxEngineObjectType getType() {
		return UGfxEngineObjectType.MESH;
	}
}
