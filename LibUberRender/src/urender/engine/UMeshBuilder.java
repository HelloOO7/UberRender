package urender.engine;

import java.nio.ByteBuffer;
import urender.api.UDataType;
import urender.api.UPrimitiveType;

public class UMeshBuilder extends UGfxObjectBuilder<UMesh> {

	private UMesh mesh = new UMesh();

	/**
	 * Sets the primitive mode used for rendering the mesh.
	 *
	 * @param type
	 * @return
	 */
	public UMeshBuilder setPrimitiveType(UPrimitiveType type) {
		mesh.primitiveType = type;
		return this;
	}

	/**
	 * Sets the mesh's index buffer.
	 *
	 * @param format Format of the index buffer.
	 * @param buffer The index buffer.
	 * @return
	 */
	public UMeshBuilder setIBO(UDataType format, ByteBuffer buffer) {
		mesh.indexBufferFormat = format;
		mesh.indexBuffer = buffer;
		return this;
	}

	/**
	 * Sets the mesh's vertex buffer.
	 *
	 * @param buffer
	 * @return
	 */
	public UMeshBuilder setVBO(ByteBuffer buffer) {
		mesh.vertexBuffer = buffer;
		return this;
	}

	/**
	 * Adds a vertex attribute.
	 *
	 * @param attr
	 * @return
	 */
	public UMeshBuilder addVertexAttribute(UVertexAttribute attr) {
		mesh.vertexAttributes.add(attr);
		return this;
	}

	@Override
	public UMesh build() {
		return mesh;
	}

	@Override
	public void reset() {
		mesh = new UMesh();
	}

	@Override
	protected UMesh getObject() {
		return mesh;
	}
}
