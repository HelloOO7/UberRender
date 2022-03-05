package urender.g3dio.ugfx.loaders;

import urender.g3dio.ugfx.serializers.IGfxEnumSerializerProvider;
import urender.g3dio.ugfx.serializers.IGfxResourceSerializer;

public interface IGfxResourceLoader {

	/**
	 * Gets an array of all de/serializers available to the resource IO.
	 *
	 * @return
	 */
	public IGfxResourceSerializer[] getResourceSerializers();

	/**
	 * Gets an array of all enum de/serializer available to resource de/serializers.
	 *
	 * @return
	 */
	public IGfxEnumSerializerProvider[] getEnumSerializers();
}
