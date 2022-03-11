package urender.g3dio.ugfx;

import java.io.IOException;
import java.nio.ByteBuffer;
import urender.common.io.base.iface.ReadableStream;
import urender.common.io.base.impl.ext.data.DataInStream;
import urender.g3dio.ugfx.loaders.IGfxResourceLoader;
import urender.g3dio.ugfx.serializers.IGfxEnumSerializerProvider;

/**
 * UGfx data input stream extension.
 */
public class UGfxDataInput extends DataInStream {

	private int version;
	private final IGfxResourceLoader loader;

	public UGfxDataInput(ReadableStream in, IGfxResourceLoader loader) {
		super(in);
		this.loader = loader;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public boolean versionOver(int version) {
		return this.version >= version;
	}

	@Override
	public String readString() throws IOException {
		String s = super.readString();
		if (s == null || s.isEmpty()) {
			return null;
		}
		return s;
	}

	public ByteBuffer readRawBuffer() throws IOException {
		return ByteBuffer.wrap(readBytes(readInt()));
	}

	public ByteBuffer readRawBufferDirect() throws IOException {
		int size = readInt();
		byte[] array = readBytes(size);
		ByteBuffer b = ByteBuffer.allocateDirect(size); //We will read into an array and then transfer to a direct byte buffer, which is long-term faster
		b.put(ByteBuffer.wrap(array)); //DirectByteBuffer will transfer faster.
		return b;
	}

	public <E extends Enum> E readEnum(Class<E> clazz) throws IOException {
		for (IGfxEnumSerializerProvider p : loader.getEnumSerializers()) {
			if (p.canSerialize(clazz)) {
				return p.deserialize(clazz, this);
			}
		}
		throw new RuntimeException("Non-serializable enum class: " + clazz);
	}
}
