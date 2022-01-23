package urender.g3dio.ugfx;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import urender.common.io.InvalidMagicException;
import urender.common.io.base.iface.DataInputEx;
import urender.common.io.base.iface.DataOutputEx;
import urender.common.io.base.iface.ReadableStream;
import urender.common.io.base.iface.WriteableStream;
import urender.common.io.base.impl.InputStreamReadable;
import urender.common.io.base.impl.access.FileStream;
import urender.common.io.base.impl.ext.data.DataInStream;
import urender.common.io.base.impl.ext.data.DataOutStream;
import urender.g3dio.ugfx.loaders.IGfxResourceLoader;
import urender.g3dio.ugfx.serializers.IGfxResourceSerializer;
import urender.g3dio.ugfx.adapters.IGfxResourceConsumer;
import urender.g3dio.ugfx.adapters.IGfxResourceProvider;

public class UGfxResource {

	public static final String UGFX_SIGNATURE = "UGfxRsrc";
	public static final int UGFX_COMMON_TAGSTR_SIZE = 4;

	private static final String UGFX_STREAM_END_TAG = "TERM";

	public static void loadResourceClasspath(String path, IGfxResourceLoader loader, IGfxResourceConsumer consumer) {
		try (ReadableStream in = new InputStreamReadable(UGfxResource.class.getClassLoader().getResourceAsStream(path))) {
			loadResource(in, loader, consumer);
		} catch (IOException ex) {
			Logger.getLogger(UGfxResource.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public static void loadResourceFile(File f, IGfxResourceLoader loader, IGfxResourceConsumer consumer) {
		try (ReadableStream in = FileStream.create(f)) {
			loadResource(in, loader, consumer);
		} catch (IOException ex) {
			Logger.getLogger(UGfxResource.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public static void writeResourceFile(File f, IGfxResourceLoader loader, IGfxResourceProvider provider) {
		try (FileStream out = FileStream.create(f)) {
			out.setLength(0);
			writeResource(out, loader, provider);
		} catch (IOException ex) {
			Logger.getLogger(UGfxResource.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static void loadResource(ReadableStream stream, IGfxResourceLoader loader, IGfxResourceConsumer consumer) throws IOException {
		DataInStream in = new DataInStream(stream);

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
			} else {
				srl.deserialize(in, consumer);
			}
		}
	}

	public static void writeResource(WriteableStream stream, IGfxResourceLoader loader, IGfxResourceProvider provider) throws IOException {
		DataOutStream out = new DataOutStream(stream);

		new GfxBinaryHeader().write(out);

		Object obj;

		IGfxResourceSerializer[] serializers = loader.getSerializers();

		while ((obj = provider.nextObject()) != null) {
			IGfxResourceSerializer srl = null;
			for (IGfxResourceSerializer s : serializers) {
				if (s.accepts(obj)) {
					srl = s;
					break;
				}
			}
			if (srl == null) {
				throw new IOException("Non-serializable resource: " + obj);
			} else {
				out.writePaddedString(srl.getTagIdent(), UGFX_COMMON_TAGSTR_SIZE);
				srl.serialize(obj, out);
			}
		}

		out.writePaddedString(UGFX_STREAM_END_TAG, UGFX_COMMON_TAGSTR_SIZE);
	}

	private static class GfxBinaryHeader {

		public final String signature;
		public final ByteOrder byteOrder;
		public final int revision;

		public GfxBinaryHeader() {
			signature = UGFX_SIGNATURE;
			byteOrder = ByteOrder.nativeOrder();
			revision = UGfxFormatRevisions.CURRENT;
		}

		public GfxBinaryHeader(DataInputEx in) throws IOException {
			signature = in.readPaddedString(UGFX_SIGNATURE.length());
			byteOrder = in.readBoolean() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
			revision = in.readInt();
		}

		public void write(DataOutputEx out) throws IOException {
			out.writeStringUnterminated(signature);
			out.writeBoolean(byteOrder == ByteOrder.BIG_ENDIAN);
			out.order(byteOrder);
			out.writeInt(revision);
		}

		public void verify() throws IOException {
			if (!Objects.equals(signature, UGFX_SIGNATURE)) {
				throw new InvalidMagicException("UGfxResource binary data signature mismatched.");
			}

			if (revision < UGfxFormatRevisions.CURRENT) {
				throw new IOException("UGfxResource binary file version " + revision + " out of date.");
			} else if (revision > UGfxFormatRevisions.CURRENT) {
				throw new IOException("UGfxResource binary file version " + revision + " too new.");
			}
		}
	}
}
