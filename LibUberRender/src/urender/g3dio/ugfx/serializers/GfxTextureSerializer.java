package urender.g3dio.ugfx.serializers;

import java.io.IOException;
import java.nio.ByteBuffer;
import urender.api.UTextureFaceAssignment;
import urender.api.UTextureFormat;
import urender.api.UTextureSwizzleChannel;
import urender.api.UTextureType;
import urender.common.io.base.iface.DataInputEx;
import urender.common.io.base.iface.DataOutputEx;
import urender.engine.UTexture;
import urender.engine.UTexture2D;
import urender.engine.UTexture2DBuilder;
import urender.engine.UTexture2DCube;
import urender.engine.UTexture2DCubeBuilder;
import urender.engine.UTextureBuilder;
import urender.engine.UTextureSwizzleMask;
import urender.g3dio.ugfx.UGfxDataInput;
import urender.g3dio.ugfx.UGfxDataOutput;
import urender.g3dio.ugfx.UGfxFormatRevisions;
import urender.g3dio.ugfx.adapters.IGfxResourceConsumer;

public class GfxTextureSerializer implements IGfxResourceSerializer<UTexture> {

	private static final ITextureSerializer[] TEX_SERIALIZERS = new ITextureSerializer[]{
		new Texture2DSerializer(),
		new Texture2DCubeSerializer()
	};

	@Override
	public String getTagIdent() {
		return "IMAG";
	}

	private ITextureSerializer decideSerializer(UTextureType type) throws IOException {
		for (ITextureSerializer s : TEX_SERIALIZERS) {
			if (s.getTexType() == type) {
				return s;
			}
		}
		throw new IOException("Could not de/serialize texture object of type " + type);
	}

	@Override
	public void deserialize(UGfxDataInput in, IGfxResourceConsumer consumer) throws IOException {
		UTextureType type = in.readEnum(UTextureType.class);

		UTextureBuilder tex = decideSerializer(type).readTexture(in);

		tex
			.setName(in.readString())
			.setWidth(in.readUnsignedShort())
			.setHeight(in.readUnsignedShort())
			.setFormat(in.readEnum(UTextureFormat.class));

		if (in.versionOver(UGfxFormatRevisions.TEXTURE_SWIZZLE_MASKS)) {
			tex.setSwizzleMask(new UTextureSwizzleMask(
				in.readEnum(UTextureSwizzleChannel.class),
				in.readEnum(UTextureSwizzleChannel.class),
				in.readEnum(UTextureSwizzleChannel.class),
				in.readEnum(UTextureSwizzleChannel.class)
			));
		}

		consumer.loadObject(tex.build());
	}

	@Override
	public void serialize(UTexture tex, UGfxDataOutput out) throws IOException {
		out.writeEnum(tex.getTextureType());

		decideSerializer(tex.getTextureType()).writeTexture(tex, out);

		out.writeString(tex.getName());
		out.writeShorts(tex.getWidth(), tex.getHeight());
		out.writeEnum(tex.format);

		out.writeEnum(tex.swizzleMask.r);
		out.writeEnum(tex.swizzleMask.g);
		out.writeEnum(tex.swizzleMask.b);
		out.writeEnum(tex.swizzleMask.a);
	}

	@Override
	public boolean accepts(Object o) {
		return o instanceof UTexture;
	}

	private static interface ITextureSerializer {

		public UTextureType getTexType();

		public UTextureBuilder readTexture(UGfxDataInput input) throws IOException;

		public void writeTexture(UTexture tex, UGfxDataOutput output) throws IOException;
	}

	private static class Texture2DSerializer implements ITextureSerializer {

		@Override
		public UTextureBuilder readTexture(UGfxDataInput input) throws IOException {
			UTexture2DBuilder tex = new UTexture2DBuilder();

			tex.setData(input.readRawBuffer());

			return tex;
		}

		@Override
		public UTextureType getTexType() {
			return UTextureType.TEX2D;
		}

		@Override
		public void writeTexture(UTexture tex, UGfxDataOutput output) throws IOException {
			ByteBuffer data = ((UTexture2D) tex).data;
			output.writeRawBuffer(data);
		}
	}

	private static class Texture2DCubeSerializer implements ITextureSerializer {

		@Override
		public UTextureBuilder readTexture(UGfxDataInput input) throws IOException {
			UTexture2DCubeBuilder tex = new UTexture2DCubeBuilder();

			int faceCount = input.read();
			for (int i = 0; i < faceCount; i++) {
				tex.addFace(
					new UTexture2DCube.UTextureCubeFace(
						input.readEnum(UTextureFaceAssignment.class),
						input.readRawBuffer()
					)
				);
			}

			return tex;
		}

		@Override
		public UTextureType getTexType() {
			return UTextureType.TEX2D_CUBEMAP;
		}

		@Override
		public void writeTexture(UTexture tex, UGfxDataOutput output) throws IOException {
			UTexture2DCube cube = (UTexture2DCube) tex;

			output.write(cube.faces.size());
			for (UTexture2DCube.UTextureCubeFace face : cube.faces) {
				output.writeEnum(face.assignment);
				output.writeRawBuffer(face.data);
			}
		}
	}
}
