package urender.g3dio.ugfx.serializers;

import java.util.HashMap;
import java.util.Map;
import urender.engine.UMaterialDrawLayer;
import urender.engine.shader.UUniformType;

public class GfxEngineEnumSerializers extends AbstractGfxEnumSerializerProvider {

	private static final GfxEngineEnumSerializers INSTANCE = new GfxEngineEnumSerializers();

	private GfxEngineEnumSerializers() {

	}

	public static GfxEngineEnumSerializers getInstance() {
		return INSTANCE;
	}

	public static final AbstractGfxEnumSerializer<UUniformType> UNIFORM_TYPE = new AbstractGfxEnumSerializer<UUniformType>() {

		private final UUniformType[] UNIFORM_TYPE_LUT = new UUniformType[]{
			UUniformType.INT,
			UUniformType.FLOAT,
			UUniformType.VEC2,
			UUniformType.VEC3,
			UUniformType.VEC4,
			UUniformType.MATRIX3,
			UUniformType.MATRIX4
		};

		@Override
		protected UUniformType[] lut() {
			return UNIFORM_TYPE_LUT;
		}
	};

	public static final AbstractGfxEnumSerializer<UMaterialDrawLayer.ShadingMethod> SHADING_METHOD = new AbstractGfxEnumSerializer<UMaterialDrawLayer.ShadingMethod>() {

		private final UMaterialDrawLayer.ShadingMethod[] SHADING_METHOD_LOOKUP = new UMaterialDrawLayer.ShadingMethod[]{
			UMaterialDrawLayer.ShadingMethod.FORWARD,
			UMaterialDrawLayer.ShadingMethod.DEFERRED
		};

		@Override
		protected UMaterialDrawLayer.ShadingMethod[] lut() {
			return SHADING_METHOD_LOOKUP;
		}
	};
	private static final Map<Class<? extends Enum>, IGfxEnumSerializer> SERIALIZER_MAP = new HashMap<>();

	static {
		SERIALIZER_MAP.put(UUniformType.class, UNIFORM_TYPE);
		SERIALIZER_MAP.put(UMaterialDrawLayer.ShadingMethod.class, SHADING_METHOD);
	}

	@Override
	protected Map<Class<? extends Enum>, IGfxEnumSerializer> serializerMap() {
		return SERIALIZER_MAP;
	}

}
