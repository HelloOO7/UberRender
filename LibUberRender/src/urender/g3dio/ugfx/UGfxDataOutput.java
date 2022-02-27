package urender.g3dio.ugfx;

import java.io.IOException;
import urender.common.io.base.iface.WriteableStream;
import urender.common.io.base.impl.ext.data.DataOutStream;
import urender.g3dio.ugfx.loaders.IGfxResourceLoader;
import urender.g3dio.ugfx.serializers.IGfxEnumSerializerProvider;

public class UGfxDataOutput extends DataOutStream {

	private final IGfxResourceLoader loader;

	public UGfxDataOutput(WriteableStream out, IGfxResourceLoader loader) {
		super(out);
		this.loader = loader;
	}
	
	@Override
	public void writeEnum(Enum value) throws IOException {
		Class clazz = value.getClass();
		for (IGfxEnumSerializerProvider p : loader.getEnumSerializers()) {
			if (p.canSerialize(clazz)) {
				p.serialize(value, this);
				return;
			}
		}
		throw new IOException("Non-serializable enum value: " + value);
	}
}
