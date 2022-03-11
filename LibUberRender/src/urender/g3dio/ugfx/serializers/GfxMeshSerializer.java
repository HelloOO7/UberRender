package urender.g3dio.ugfx.serializers;

import java.io.IOException;
import urender.api.UDataType;
import urender.api.UPrimitiveType;
import urender.engine.UMesh;
import urender.engine.UMeshBuilder;
import urender.engine.UVertexAttribute;
import urender.engine.UVertexAttributeBuilder;
import urender.g3dio.ugfx.UGfxDataInput;
import urender.g3dio.ugfx.UGfxDataOutput;
import urender.g3dio.ugfx.UGfxFormatRevisions;
import urender.g3dio.ugfx.adapters.IGfxResourceConsumer;

/**
 * UGfx Mesh resource serializer.
 */
public class GfxMeshSerializer implements IGfxResourceSerializer<UMesh> {

	@Override
	public String getTagIdent() {
		return "MESH";
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

		bld.setIBO(iboFormat, in.readRawBufferDirect()).setVBO(in.readRawBufferDirect());

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

		out.writeRawBuffer(mesh.getIBO());
		out.writeRawBuffer(mesh.getVBO());
	}

	@Override
	public boolean accepts(Object o) {
		return o instanceof UMesh;
	}
}
