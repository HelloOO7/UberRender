package urender.g3dio.ugfx.loaders;

import urender.g3dio.ugfx.serializers.GfxMaterialSerializer;
import urender.g3dio.ugfx.serializers.GfxMeshSerializer;
import urender.g3dio.ugfx.serializers.GfxProgramSerializer;
import urender.g3dio.ugfx.serializers.GfxShaderSerializer;
import urender.g3dio.ugfx.serializers.GfxTextureSerializer;
import urender.g3dio.ugfx.serializers.IGfxResourceSerializer;

public class DefaultGfxResourceLoader implements IGfxResourceLoader {

	private static DefaultGfxResourceLoader SINGLETON;
	
	private static final IGfxResourceSerializer[] DEFAULT_SERIALIZERS = new IGfxResourceSerializer[]{
		new GfxTextureSerializer(),
		new GfxMaterialSerializer(),
		new GfxShaderSerializer(),
		new GfxMeshSerializer(),
		new GfxProgramSerializer()
	};
	
	private DefaultGfxResourceLoader() {
		
	}
	
	public static DefaultGfxResourceLoader getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new DefaultGfxResourceLoader();
		}
		
		return SINGLETON;
	}

	@Override
	public IGfxResourceSerializer[] getSerializers() {
		return DEFAULT_SERIALIZERS;
	}
}
