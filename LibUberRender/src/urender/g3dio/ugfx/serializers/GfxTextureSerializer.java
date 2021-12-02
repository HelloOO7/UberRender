package urender.g3dio.ugfx.serializers;

import java.io.IOException;
import java.nio.ByteBuffer;
import urender.api.UTextureFormat;
import urender.api.UTextureType;
import urender.common.io.base.iface.DataInputEx;
import urender.common.io.base.iface.DataOutputEx;
import urender.engine.UTexture;
import urender.engine.UTexture2D;
import urender.g3dio.ugfx.adapters.IGfxResourceAdapter;

public class GfxTextureSerializer implements IGfxResourceSerializer<UTexture> {

	private static final UTextureType[] TEX_TYPE_LOOKUP = new UTextureType[]{UTextureType.TEX2D, UTextureType.TEX2D_CUBEMAP};
	private static final UTextureFormat[] TEX_FORMAT_LOOKUP = new UTextureFormat[]{
		UTextureFormat.R8,
		UTextureFormat.RG8,
		UTextureFormat.RGB8,
		UTextureFormat.RGBA8,
		UTextureFormat.FLOAT32
	};
	
	private static final ITextureSerializer[] TEX_SERIALIZERS = new ITextureSerializer[] {
		new Texture2DSerializer()
	};

	@Override
	public String getTagIdent() {
		return "IMAG";
	}

	@Override
	public void deserialize(DataInputEx in, IGfxResourceAdapter adapter) throws IOException {
		ITextureSerializer texSerializer = null;

		UTextureType type = TEX_TYPE_LOOKUP[in.readUnsignedByte()];

		for (ITextureSerializer s : TEX_SERIALIZERS) {
			if (s.getTexType() == type) {
				texSerializer = s;
				break;
			}
		}

		if (texSerializer == null) {
			throw new IOException("Could not initialize texture object of type " + type);
		} else {
			UTexture tex = texSerializer.readTexture(in);
			
			tex.name = in.readString();
			tex.width = in.readUnsignedShort();
			tex.height = in.readUnsignedShort();
			tex.format = TEX_FORMAT_LOOKUP[in.read()];
			
			adapter.loadObject(tex);
		}
	}

	@Override
	public void serialize(UTexture tex, DataOutputEx out) throws IOException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private static interface ITextureSerializer {
		public UTextureType getTexType();
		
		public UTexture readTexture(DataInputEx input) throws IOException;
	}
	
	private static class Texture2DSerializer implements ITextureSerializer {

		@Override
		public UTexture readTexture(DataInputEx input) throws IOException {
			UTexture2D tex = new UTexture2D();
			
			tex.data = ByteBuffer.wrap(input.readBytes(input.readInt()));
			
			return tex;
		}

		@Override
		public UTextureType getTexType() {
			return UTextureType.TEX2D;
		}
	}
}
