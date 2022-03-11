package urender.g3dio.ugfx;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
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
import urender.g3dio.ugfx.loaders.IGfxResourceLoader;
import urender.g3dio.ugfx.serializers.IGfxResourceSerializer;
import urender.g3dio.ugfx.adapters.IGfxResourceConsumer;
import urender.g3dio.ugfx.adapters.IGfxResourceProvider;

/**
 * URender Graphics Resource file format.
 *
 * Data is read and written using unaligned binary stream blocks.
 */
public class UGfxResource {

	/**
	 * Magic string for identifying the format's binary files.
	 */
	public static final String UGFX_SIGNATURE = "UGfxRsrc";
	/**
	 * Size of a four character resource block tag (4).
	 */
	public static final int UGFX_COMMON_TAGSTR_SIZE = 4;
	/**
	 * Terminator resource block tag.
	 */
	private static final String UGFX_STREAM_END_TAG = "TERM";

	/**
	 * Loads a resource from the program's classpath.
	 *
	 * @param path Classpath of the resource.
	 * @param loader The loader to use for the binary stream.
	 * @param consumer The consumer to load resources into.
	 */
	public static void loadResourceClasspath(String path, IGfxResourceLoader loader, IGfxResourceConsumer consumer) {
		try (ReadableStream in = new InputStreamReadable(UGfxResource.class.getClassLoader().getResourceAsStream(path))) {
			loadResource(in, loader, consumer);
		} catch (IOException ex) {
			Logger.getLogger(UGfxResource.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Loads a resource from a disk file.
	 *
	 * @param f Location of the resource in the file system.
	 * @param loader The loader to use for the binary stream.
	 * @param consumer The consumer to load resources into.
	 */
	public static void loadResourceFile(File f, IGfxResourceLoader loader, IGfxResourceConsumer consumer) {
		try (ReadableStream in = new InputStreamReadable(new BufferedInputStream(new FileInputStream(f)))) {
			loadResource(in, loader, consumer);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to read UGfxResource " + f.getAbsolutePath(), ex);
		}
	}

	/**
	 * Writes a resource to a disk file.
	 *
	 * @param f Location of the new resource in the file system.
	 * @param loader The loader to use for the binary stream.
	 * @param provider Provider to obtain resources from.
	 */
	public static void writeResourceFile(File f, IGfxResourceLoader loader, IGfxResourceProvider provider) {
		try (FileStream out = FileStream.create(f)) {
			out.setLength(0);
			writeResource(out, loader, provider);
		} catch (IOException ex) {
			Logger.getLogger(UGfxResource.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Reads a resource stream.
	 *
	 * @param stream Stream of valid resource data.
	 * @param loader The loader to use for the binary stream.
	 * @param consumer The consumer to load resources into.
	 * @throws IOException
	 */
	public static void loadResource(ReadableStream stream, IGfxResourceLoader loader, IGfxResourceConsumer consumer) throws IOException {
		UGfxDataInput in = new UGfxDataInput(stream, loader);

		GfxBinaryHeader header = new GfxBinaryHeader(in);

		header.verify();

		in.setVersion(header.revision);

		String tag;

		Map<String, IGfxResourceSerializer> serializers = new HashMap<>();

		for (IGfxResourceSerializer srl : loader.getResourceSerializers()) {
			serializers.put(srl.getTagIdent(), srl);
		}

		while (!((tag = in.readPaddedString(UGFX_COMMON_TAGSTR_SIZE)).equals(
			UGFX_STREAM_END_TAG
		))) {
			IGfxResourceSerializer srl = serializers.get(tag);
			if (srl == null) {
				throw new IOException("Non-deserializable resource tag: " + tag + " at " + Integer.toHexString(in.getPosition() - UGFX_COMMON_TAGSTR_SIZE));
			} else {
				srl.deserialize(in, consumer);
			}
		}
	}

	/**
	 * Writes a resource stream.
	 *
	 * @param stream Stream to write the data into.
	 * @param loader The loader to use for the binary stream.
	 * @param provider Provider to obtain resources from.
	 * @throws IOException
	 */
	public static void writeResource(WriteableStream stream, IGfxResourceLoader loader, IGfxResourceProvider provider) throws IOException {
		UGfxDataOutput out = new UGfxDataOutput(stream, loader);

		new GfxBinaryHeader().write(out);

		Object obj;

		IGfxResourceSerializer[] serializers = loader.getResourceSerializers();

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

			if (revision > UGfxFormatRevisions.CURRENT) {
				throw new IOException("UGfxResource binary file version " + revision + " too new.");
			}
		}
	}
}
