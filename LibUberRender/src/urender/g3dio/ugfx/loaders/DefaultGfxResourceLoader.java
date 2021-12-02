package urender.g3dio.ugfx.loaders;

import urender.g3dio.ugfx.serializers.GfxTextureSerializer;
import urender.g3dio.ugfx.serializers.IGfxResourceSerializer;

public class DefaultGfxResourceLoader implements IGfxResourceLoader {

	private static final IGfxResourceSerializer[] DEFAULT_SERIALIZERS = new IGfxResourceSerializer[]{
		new GfxTextureSerializer()
	};

	@Override
	public IGfxResourceSerializer[] getSerializers() {
		return DEFAULT_SERIALIZERS;
	}

}
