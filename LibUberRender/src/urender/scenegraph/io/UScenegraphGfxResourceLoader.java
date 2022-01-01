package urender.scenegraph.io;

import urender.g3dio.ugfx.loaders.IGfxResourceLoader;
import urender.g3dio.ugfx.serializers.*;
import urender.scenegraph.io.serializers.GfxModelSerializer;

public class UScenegraphGfxResourceLoader implements IGfxResourceLoader {

	private static UScenegraphGfxResourceLoader SINGLETON;

	private static final IGfxResourceSerializer[] DEFAULT_SERIALIZERS = new IGfxResourceSerializer[]{
		//COMMON
		new GfxTextureSerializer(),
		new GfxMaterialSerializer(),
		new GfxShaderSerializer(),
		new GfxMeshSerializer(),
		new GfxProgramSerializer(),
		
		//SCENEGRAPH
		new GfxModelSerializer()
	};

	private UScenegraphGfxResourceLoader() {

	}

	public static UScenegraphGfxResourceLoader getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new UScenegraphGfxResourceLoader();
		}

		return SINGLETON;
	}

	@Override
	public IGfxResourceSerializer[] getSerializers() {
		return DEFAULT_SERIALIZERS;
	}

}
