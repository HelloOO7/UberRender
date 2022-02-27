package urender.g3dio.ugfx.loaders;

import urender.g3dio.ugfx.serializers.IGfxEnumSerializerProvider;
import urender.g3dio.ugfx.serializers.IGfxResourceSerializer;

public interface IGfxResourceLoader {
	public IGfxResourceSerializer[] getResourceSerializers();
	public IGfxEnumSerializerProvider[] getEnumSerializers();
}
