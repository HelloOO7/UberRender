package urender.g3dio.ugfx.serializers;

import java.io.IOException;
import java.nio.ByteBuffer;
import urender.api.UDataType;
import urender.api.UPrimitiveType;
import urender.common.io.base.iface.DataInputEx;
import urender.common.io.base.iface.DataOutputEx;
import urender.engine.UMesh;
import urender.engine.UMeshBuilder;
import urender.engine.UVertexAttribute;
import urender.engine.UVertexAttributeBuilder;
import urender.g3dio.ugfx.UGfxDataInput;
import urender.g3dio.ugfx.adapters.IGfxResourceConsumer;

public class GfxMeshSerializer implements IGfxResourceSerializer<UMesh> {

	private static final UPrimitiveType[] PRIMITIVE_TYPE_LOOKUP = new UPrimitiveType[]{
		UPrimitiveType.TRIS,
		UPrimitiveType.LINES,
		UPrimitiveType.POINTS
	};

	private static final UDataType[] MESH_DATA_TYPE_LOOKUP = new UDataType[]{
		UDataType.FLOAT32,
		UDataType.FLOAT64,
		UDataType.FLOAT16,
		UDataType.INT8,
		UDataType.INT16,
		UDataType.INT32,};

	@Override
	public String getTagIdent() {
		return "MESH";
	}

	private ByteBuffer readRawBuffer(DataInputEx in) throws IOException {
		int size = in.readInt();
		byte[] array = in.readBytes(size);
		ByteBuffer b = ByteBuffer.allocateDirect(size); //We will read into an array and then transfer to a direct byte buffer, which is long-term faster
		b.put(ByteBuffer.wrap(array)); //DirectByteBuffer will transfer faster.
		return b;
	}
	
	private void writeRawBuffer(ByteBuffer b, DataOutputEx out) throws IOException {
		byte[] bytes;
		if (b.hasArray()) {
			bytes = b.array();
		}
		else {
			bytes = new byte[b.capacity()];
			b.rewind();
			b.get(bytes);
		}
		out.writeInt(bytes.length);
		out.write(bytes);
	}

	@Override
	public void deserialize(UGfxDataInput in, IGfxResourceConsumer consumer) throws IOException {
		UMeshBuilder bld = new UMeshBuilder();
		bld.setName(in.readString());
		bld.setPrimitiveType(PRIMITIVE_TYPE_LOOKUP[in.read()]);

		UDataType iboFormat = MESH_DATA_TYPE_LOOKUP[in.read()];

		int vtxAttrCount = in.read();
		UVertexAttributeBuilder attrBld = new UVertexAttributeBuilder();
		for (int i = 0; i < vtxAttrCount; i++) {
			attrBld.reset();

			bld.addVertexAttribute(
				attrBld
					.setShaderAttrName(in.readString())
					.setOffset(in.readUnsignedShort())
					.setElementCount(in.read())
					.setFormat(MESH_DATA_TYPE_LOOKUP[in.read()])
					.setTypeUnsigned(in.readBoolean())
					.setNormalized(in.readBoolean())
					.build()
			);
		}
		
		bld.setIBO(iboFormat, readRawBuffer(in)).setVBO(readRawBuffer(in));
		
		consumer.loadObject(bld.build());
	}

	@Override
	public void serialize(UMesh mesh, DataOutputEx out) throws IOException {
		out.writeString(mesh.getName());
		out.write(IGfxResourceSerializer.findEnumIndex(PRIMITIVE_TYPE_LOOKUP, mesh.getPrimitiveType()));
		
		out.write(IGfxResourceSerializer.findEnumIndex(MESH_DATA_TYPE_LOOKUP, mesh.getIBOFormat()));
		
		int vtxAttrCount = mesh.getVtxAttrCount();
		out.write(vtxAttrCount);
		for (int i = 0; i < vtxAttrCount; i++) {
			UVertexAttribute a = mesh.getVtxAttr(i);
			
			out.writeString(a.getShaderAttrName());
			out.writeShort(a.getOffset());
			out.write(a.getElementCount());
			out.write(IGfxResourceSerializer.findEnumIndex(MESH_DATA_TYPE_LOOKUP, a.getFormat()));
			out.writeBoolean(a.getTypeIsUnsigned());
			out.writeBoolean(a.isNormalized());
		}
		
		writeRawBuffer(mesh.getIBO(), out);
		writeRawBuffer(mesh.getVBO(), out);
	}

	@Override
	public boolean accepts(Object o) {
		return o instanceof UMesh;
	}
}
