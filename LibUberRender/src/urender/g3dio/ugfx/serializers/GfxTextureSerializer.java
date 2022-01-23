package urender.g3dio.ugfx.serializers;

import java.io.IOException;
import java.nio.ByteBuffer;
import urender.api.UTextureFormat;
import urender.api.UTextureType;
import urender.common.io.base.iface.DataInputEx;
import urender.common.io.base.iface.DataOutputEx;
import urender.engine.UTexture;
import urender.engine.UTexture2D;
import urender.engine.UTexture2DBuilder;
import urender.engine.UTextureBuilder;
import urender.g3dio.ugfx.UGfxDataInput;
import urender.g3dio.ugfx.adapters.IGfxResourceConsumer;

public class GfxTextureSerializer implements IGfxResourceSerializer<UTexture> {

	private static final UTextureType[] TEX_TYPE_LOOKUP = new UTextureType[]{UTextureType.TEX2D, UTextureType.TEX2D_CUBEMAP};
	private static final UTextureFormat[] TEX_FORMAT_LOOKUP = new UTextureFormat[]{
		UTextureFormat.R8,
		UTextureFormat.RG8,
		UTextureFormat.RGB8,
		UTextureFormat.RGBA8,
		UTextureFormat.FLOAT32
	};

	private static final ITextureSerializer[] TEX_SERIALIZERS = new ITextureSerializer[]{
		new Texture2DSerializer()
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
		UTextureType type = TEX_TYPE_LOOKUP[in.readUnsignedByte()];

		UTextureBuilder tex = decideSerializer(type).readTexture(in);

		tex
			.setName(in.readString())
			.setWidth(in.readUnsignedShort())
			.setHeight(in.readUnsignedShort())
			.setFormat(TEX_FORMAT_LOOKUP[in.read()]);

		consumer.loadObject(tex.build());
	}

	@Override
	public void serialize(UTexture tex, DataOutputEx out) throws IOException {
		out.write(IGfxResourceSerializer.findEnumIndex(TEX_TYPE_LOOKUP, tex.getTextureType()));
		
		decideSerializer(tex.getTextureType()).writeTexture(tex, out);
		
		out.writeString(tex.getName());
		out.writeShorts(tex.width, tex.height);
		out.write(IGfxResourceSerializer.findEnumIndex(TEX_FORMAT_LOOKUP, tex.format));
	}

	@Override
	public boolean accepts(Object o) {
		return o instanceof UTexture;
	}

	private static interface ITextureSerializer {

		public UTextureType getTexType();

		public UTextureBuilder readTexture(DataInputEx input) throws IOException;
		
		public void writeTexture(UTexture tex, DataOutputEx output) throws IOException;
	}

	private static class Texture2DSerializer implements ITextureSerializer {

		@Override
		public UTextureBuilder readTexture(DataInputEx input) throws IOException {
			UTexture2DBuilder tex = new UTexture2DBuilder();

			tex.setData(ByteBuffer.wrap(input.readBytes(input.readInt())));

			return tex;
		}

		@Override
		public UTextureType getTexType() {
			return UTextureType.TEX2D;
		}

		@Override
		public void writeTexture(UTexture tex, DataOutputEx output) throws IOException {
			ByteBuffer data = ((UTexture2D)tex).data;
			byte[] bytes = null;
			if (!data.hasArray()) {
				bytes = new byte[data.capacity()];
				data.rewind();
				data.get(bytes);
			}
			else {
				bytes = data.array();
			}
			output.writeInt(bytes.length);
			output.write(bytes);
		}
	}
}
