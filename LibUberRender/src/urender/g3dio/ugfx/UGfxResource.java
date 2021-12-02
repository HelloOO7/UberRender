package urender.g3dio.ugfx;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import urender.common.io.InvalidMagicException;
import urender.common.io.base.iface.DataInputEx;
import urender.common.io.base.iface.IOStream;
import urender.common.io.base.impl.ext.data.DataIOStream;
import urender.g3dio.ugfx.adapters.IGfxResourceAdapter;
import urender.g3dio.ugfx.loaders.IGfxResourceLoader;
import urender.g3dio.ugfx.serializers.IGfxResourceSerializer;

public class UGfxResource {

	public static final String UGFX_SIGNATURE = "UGfxRsrc";
	public static final int UGFX_COMMON_TAGSTR_SIZE = 4;

	private static final String UGFX_STREAM_END_TAG = "TERM";

	public static void loadResource(IOStream ios, IGfxResourceLoader loader, IGfxResourceAdapter adapter) throws IOException {
		DataIOStream in = new DataIOStream(ios);

		GfxBinaryHeader header = new GfxBinaryHeader(in);

		header.verify();

		String tag;
		
		Map<String, IGfxResourceSerializer> serializers = new HashMap<>();
		
		for (IGfxResourceSerializer srl : loader.getSerializers()) {
			serializers.put(srl.getTagIdent(), srl);
		}

		while (!((tag = in.readPaddedString(UGFX_COMMON_TAGSTR_SIZE)).equals(
			UGFX_STREAM_END_TAG
		))) {
			IGfxResourceSerializer srl = serializers.get(tag);
			if (srl == null) {
				throw new IOException("Non-deserializable resource tag: " + tag);
			}
			else {
				srl.deserialize(in, adapter);
			}
		}
	}

	private static class GfxBinaryHeader {

		public final String signature;
		public final ByteOrder byteOrder;
		public final int revision;
		public int fileSize;

		public GfxBinaryHeader() {
			signature = UGFX_SIGNATURE;
			byteOrder = ByteOrder.nativeOrder();
			revision = UGfxFormatRevisions.CURRENT;
		}

		public GfxBinaryHeader(DataInputEx in) throws IOException {
			signature = in.readPaddedString(UGFX_SIGNATURE.length());
			byteOrder = in.readBoolean() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
			revision = in.readInt();
			fileSize = in.readInt();
		}

		public void verify() throws IOException {
			if (!Objects.equals(signature, UGFX_SIGNATURE)) {
				throw new InvalidMagicException("UGfxResource binary data signature mismatched.");
			}

			if (revision < UGfxFormatRevisions.CURRENT) {
				throw new IOException("UGfxResource binary file version " + revision + " out of date.");
			} else if (revision < UGfxFormatRevisions.CURRENT) {
				throw new IOException("UGfxResource binary file version " + revision + " too new.");
			}
		}
	}
}
