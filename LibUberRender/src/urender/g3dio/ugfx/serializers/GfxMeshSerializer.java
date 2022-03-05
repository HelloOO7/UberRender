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
import urender.g3dio.ugfx.UGfxDataOutput;
import urender.g3dio.ugfx.UGfxFormatRevisions;
import urender.g3dio.ugfx.adapters.IGfxResourceConsumer;

public class GfxMeshSerializer implements IGfxResourceSerializer<UMesh> {

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
		} else {
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
		bld.setPrimitiveType(in.readEnum(UPrimitiveType.class));

		UDataType iboFormat = in.readEnum(UDataType.class);
		if (!in.versionOver(UGfxFormatRevisions.SEPARATE_UNSIGNED_FORMATS)) {
			iboFormat = makeTypeUnsigned(iboFormat);
		}

		int vtxAttrCount = in.read();
		UVertexAttributeBuilder attrBld = new UVertexAttributeBuilder();
		for (int i = 0; i < vtxAttrCount; i++) {
			attrBld.reset();

			attrBld
				.setShaderAttrName(in.readString())
				.setOffset(in.readUnsignedShort())
				.setElementCount(in.read());

			UDataType format = in.readEnum(UDataType.class);

			if (!in.versionOver(UGfxFormatRevisions.SEPARATE_UNSIGNED_FORMATS)) {
				boolean unsigned = in.readBoolean();

				if (unsigned) {
					format = makeTypeUnsigned(format);
				}
			}

			attrBld.setFormat(format);
			attrBld.setNormalized(in.readBoolean());

			bld.addVertexAttribute(attrBld.build());
		}

		bld.setIBO(iboFormat, readRawBuffer(in)).setVBO(readRawBuffer(in));

		consumer.loadObject(bld.build());
	}

	private static UDataType makeTypeUnsigned(UDataType type) {
		switch (type) {
			case INT16:
				type = UDataType.UINT16;
				break;
			case INT32:
				type = UDataType.UINT32;
				break;
			case INT8:
				type = UDataType.UINT8;
				break;
		}
		return type;
	}

	@Override
	public void serialize(UMesh mesh, UGfxDataOutput out) throws IOException {
		out.writeString(mesh.getName());
		out.writeEnum(mesh.getPrimitiveType());

		out.writeEnum(mesh.getIBOFormat());

		int vtxAttrCount = mesh.getVtxAttrCount();
		out.write(vtxAttrCount);
		for (int i = 0; i < vtxAttrCount; i++) {
			UVertexAttribute a = mesh.getVtxAttr(i);

			out.writeString(a.getShaderAttrName());
			out.writeShort(a.getOffset());
			out.write(a.getElementCount());
			out.writeEnum(a.getFormat());
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
