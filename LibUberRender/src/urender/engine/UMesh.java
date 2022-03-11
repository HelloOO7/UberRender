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

/**
 * Mesh/geometry resource.
 */
public class UMesh extends UGfxEngineObject {

	UPrimitiveType primitiveType;

	private UObjHandle __iboHandle = new UObjHandle();
	ByteBuffer indexBuffer;
	UDataType indexBufferFormat;

	private UObjHandle __vboHandle = new UObjHandle();
	ByteBuffer vertexBuffer;

	final List<UVertexAttribute> vertexAttributes = new ArrayList<>();

	/**
	 * Get the primitive mode used for rendering this mesh.
	 *
	 * @return
	 */
	public UPrimitiveType getPrimitiveType() {
		return primitiveType;
	}

	/**
	 * Calculates the vertex stride.
	 *
	 * @return
	 */
	public int getOneVertexSize() {
		int size = 0;
		for (UVertexAttribute a : vertexAttributes) {
			size += a.format.sizeof * a.elementCount;
		}
		return size;
	}

	/**
	 * Gets the number of vertex attributes.
	 *
	 * @return
	 */
	public int getVtxAttrCount() {
		return vertexAttributes.size();
	}

	/**
	 * Gets a vertex attribute.
	 *
	 * @param index Index of the vertex attribute.
	 * @return
	 */
	public UVertexAttribute getVtxAttr(int index) {
		return vertexAttributes.get(index);
	}

	/**
	 * Gets the raw data format of the index buffer.
	 *
	 * @return
	 */
	public UDataType getIBOFormat() {
		return indexBufferFormat;
	}

	/**
	 * Gets the index buffer data.
	 *
	 * @return
	 */
	public ByteBuffer getIBO() {
		return indexBuffer;
	}

	/**
	 * Gets the vertex buffer data.
	 *
	 * @return
	 */
	public ByteBuffer getVBO() {
		return vertexBuffer;
	}

	/**
	 * Gets the number of vertices in the vertex buffer.
	 *
	 * @return
	 */
	public int getVertexCount() {
		return vertexBuffer.capacity() / getOneVertexSize();
	}

	/**
	 * Gets the number if facepoints in the index buffer.
	 *
	 * @return
	 */
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

	/**
	 * Readies the mesh's buffers for drawing.
	 *
	 * @param rnd Rendering backend core.
	 */
	public void setup(RenderingBackend rnd) {
		setupBuffer(rnd, UBufferType.IBO, __iboHandle, indexBuffer);
		setupBuffer(rnd, UBufferType.VBO, __vboHandle, vertexBuffer);
	}

	/**
	 * Draws the mesh using a shader program.
	 *
	 * @param rnd Rendering backend core.
	 * @param program The shader program to use.
	 */
	public void draw(RenderingBackend rnd, UShaderProgram program) {
		int stride = getOneVertexSize();

		for (UVertexAttribute a : vertexAttributes) {
			UObjHandle index = program.getAttributeLocation(rnd, a.shaderAttrName);
			if (index.isValid(rnd)) {
				rnd.bufferAttribPointer(__vboHandle, index, a.elementCount, a.format, a.normalized, stride, a.offset);
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
