package urender.engine;

import java.nio.ByteBuffer;
import urender.api.UDataType;
import urender.api.UPrimitiveType;

public class UMeshBuilder extends UGfxObjectBuilder<UMesh> {
	private UMesh mesh = new UMesh();
	
	public UMeshBuilder setPrimitiveType(UPrimitiveType type) {
		mesh.primitiveType = type;
		return this;
	}
	
	public UMeshBuilder setIBO(UDataType format, ByteBuffer buffer) {
		mesh.indexBufferFormat = format;
		mesh.indexBuffer = buffer;
		return this;
	}
	
	public UMeshBuilder setVBO(ByteBuffer buffer) {
		mesh.vertexBuffer = buffer;
		return this;
	}
	
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
