package urender.g3dio.ugfx.loaders;

import urender.g3dio.ugfx.serializers.GfxAPIEnumSerializers;
import urender.g3dio.ugfx.serializers.GfxEngineEnumSerializers;
import urender.g3dio.ugfx.serializers.GfxMaterialSerializer;
import urender.g3dio.ugfx.serializers.GfxMeshSerializer;
import urender.g3dio.ugfx.serializers.GfxProgramSerializer;
import urender.g3dio.ugfx.serializers.GfxShaderSerializer;
import urender.g3dio.ugfx.serializers.GfxTextureSerializer;
import urender.g3dio.ugfx.serializers.IGfxEnumSerializerProvider;
import urender.g3dio.ugfx.serializers.IGfxResourceSerializer;

public class DefaultGfxResourceLoader implements IGfxResourceLoader {

	private static DefaultGfxResourceLoader SINGLETON;
	
	private static final IGfxResourceSerializer[] DEFAULT_RESOURCE_SERIALIZERS = new IGfxResourceSerializer[]{
		new GfxTextureSerializer(),
		new GfxMaterialSerializer(),
		new GfxShaderSerializer(),
		new GfxMeshSerializer(),
		new GfxProgramSerializer()
	};
	
	private static final IGfxEnumSerializerProvider[] DEFAULT_ENUM_SERIALIZER_SETS = new IGfxEnumSerializerProvider[] {
		GfxAPIEnumSerializers.getInstance(),
		GfxEngineEnumSerializers.getInstance()
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
	public IGfxResourceSerializer[] getResourceSerializers() {
		return DEFAULT_RESOURCE_SERIALIZERS;
	}

	@Override
	public IGfxEnumSerializerProvider[] getEnumSerializers() {
		return DEFAULT_ENUM_SERIALIZER_SETS;
	}
}
