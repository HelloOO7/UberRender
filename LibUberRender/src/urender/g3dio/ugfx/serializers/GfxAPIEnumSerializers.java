package urender.g3dio.ugfx.serializers;

import java.util.HashMap;
import java.util.Map;
import urender.api.UDataType;
import urender.api.UPrimitiveType;
import urender.api.UShaderType;
import urender.api.UTextureFormat;
import urender.api.UTextureMagFilter;
import urender.api.UTextureMinFilter;
import urender.api.UTextureSwizzleChannel;
import urender.api.UTextureType;
import urender.api.UTextureWrap;

/**
 * UGfx enum serializers for urender.api enum constants.
 */
public class GfxAPIEnumSerializers extends AbstractGfxEnumSerializerProvider {

	private static final GfxAPIEnumSerializers INSTANCE = new GfxAPIEnumSerializers();
	
	private GfxAPIEnumSerializers() {
		
	}
	
	public static GfxAPIEnumSerializers getInstance() {
		return INSTANCE;
	}
	
	public static final AbstractGfxEnumSerializer<UTextureType> TEX_TYPE = new AbstractGfxEnumSerializer<UTextureType>() {

		private final UTextureType[] TEX_TYPE_LOOKUP = new UTextureType[]{UTextureType.TEX2D, UTextureType.TEX2D_CUBEMAP};

		@Override
		protected UTextureType[] lut() {
			return TEX_TYPE_LOOKUP;
		}
	};

	public static final AbstractGfxEnumSerializer<UTextureFormat> TEX_FORMAT = new AbstractGfxEnumSerializer<UTextureFormat>() {

		private final UTextureFormat[] TEX_FORMAT_LOOKUP = new UTextureFormat[]{
			UTextureFormat.R8,
			UTextureFormat.RG8,
			UTextureFormat.RGB8,
			UTextureFormat.RGBA8,
			UTextureFormat.FLOAT32,
			UTextureFormat.DEPTH24_STENCIL8,
			UTextureFormat.DEPTH_COMPONENT24,
			UTextureFormat.STENCIL_INDEX8,
			UTextureFormat.R16F,
			UTextureFormat.R16UI,
			UTextureFormat.RGBA16F
		};

		@Override
		protected UTextureFormat[] lut() {
			return TEX_FORMAT_LOOKUP;
		}
	};
	
	public static final AbstractGfxEnumSerializer<UTextureSwizzleChannel> TEX_SWIZZLE_CHANNEL = new AbstractGfxEnumSerializer<UTextureSwizzleChannel>() {

		private final UTextureSwizzleChannel[] TEX_SWIZZLE_LOOKUP = new UTextureSwizzleChannel[]{
			UTextureSwizzleChannel.R,
			UTextureSwizzleChannel.G,
			UTextureSwizzleChannel.B,
			UTextureSwizzleChannel.A,
			UTextureSwizzleChannel.ZERO,
			UTextureSwizzleChannel.ONE
		};

		@Override
		protected UTextureSwizzleChannel[] lut() {
			return TEX_SWIZZLE_LOOKUP;
		}
	};

	public static final AbstractGfxEnumSerializer<UShaderType> SHADER_TYPE = new AbstractGfxEnumSerializer<UShaderType>() {

		private final UShaderType[] SHA_TYPE_LOOKUP = new UShaderType[]{UShaderType.VERTEX, UShaderType.FRAGMENT};

		@Override
		protected UShaderType[] lut() {
			return SHA_TYPE_LOOKUP;
		}
	};

	public static final AbstractGfxEnumSerializer<UPrimitiveType> PRIMITIVE_TYPE = new AbstractGfxEnumSerializer<UPrimitiveType>() {

		private final UPrimitiveType[] PRIMITIVE_TYPE_LOOKUP = new UPrimitiveType[]{
			UPrimitiveType.TRIS,
			UPrimitiveType.LINES,
			UPrimitiveType.POINTS
		};

		@Override
		protected UPrimitiveType[] lut() {
			return PRIMITIVE_TYPE_LOOKUP;
		}
	};

	public static final AbstractGfxEnumSerializer<UDataType> DATA_TYPE = new AbstractGfxEnumSerializer<UDataType>() {
		private final UDataType[] DATA_TYPE_LOOKUP = new UDataType[]{
			UDataType.FLOAT32,
			UDataType.FLOAT64,
			UDataType.FLOAT16,
			UDataType.INT8,
			UDataType.INT16,
			UDataType.INT32,
			UDataType.UINT8,
			UDataType.UINT16,
			UDataType.UINT32
		};

		@Override
		protected UDataType[] lut() {
			return DATA_TYPE_LOOKUP;
		}
	};

	public static final AbstractGfxEnumSerializer<UTextureMagFilter> TEX_MAG_FILTER = new AbstractGfxEnumSerializer<UTextureMagFilter>() {
		private final UTextureMagFilter[] MAG_FILTER_LOOKUP = new UTextureMagFilter[]{
			UTextureMagFilter.LINEAR,
			UTextureMagFilter.NEAREST_NEIGHBOR
		};

		@Override
		protected UTextureMagFilter[] lut() {
			return MAG_FILTER_LOOKUP;
		}
	};

	public static final AbstractGfxEnumSerializer<UTextureMinFilter> TEX_MIN_FILTER = new AbstractGfxEnumSerializer<UTextureMinFilter>() {
		private final UTextureMinFilter[] MIN_FILTER_LOOKUP = new UTextureMinFilter[]{
			UTextureMinFilter.LINEAR,
			UTextureMinFilter.NEAREST_NEIGHBOR,
			UTextureMinFilter.LINEAR_MIPMAP_LINEAR,
			UTextureMinFilter.LINEAR_MIPMAP_NEAREST,
			UTextureMinFilter.NEAREST_MIPMAP_LINEAR,
			UTextureMinFilter.LINEAR_MIPMAP_NEAREST
		};

		@Override
		protected UTextureMinFilter[] lut() {
			return MIN_FILTER_LOOKUP;
		}
	};

	public static final AbstractGfxEnumSerializer<UTextureWrap> TEX_WRAP = new AbstractGfxEnumSerializer<UTextureWrap>() {
		private final UTextureWrap[] WRAP_LOOKUP = new UTextureWrap[]{
			UTextureWrap.REPEAT,
			UTextureWrap.MIRRORED_REPEAT,
			UTextureWrap.CLAMP_TO_EDGE,
			UTextureWrap.CLAMP_TO_BORDER
		};

		@Override
		protected UTextureWrap[] lut() {
			return WRAP_LOOKUP;
		}
	};

	private static final Map<Class<? extends Enum>, IGfxEnumSerializer> SERIALIZER_MAP = new HashMap<>();

	static {
		SERIALIZER_MAP.put(UTextureType.class, TEX_TYPE);
		SERIALIZER_MAP.put(UTextureWrap.class, TEX_WRAP);
		SERIALIZER_MAP.put(UTextureMagFilter.class, TEX_MAG_FILTER);
		SERIALIZER_MAP.put(UTextureMinFilter.class, TEX_MIN_FILTER);
		SERIALIZER_MAP.put(UTextureFormat.class, TEX_FORMAT);
		SERIALIZER_MAP.put(UTextureSwizzleChannel.class, TEX_SWIZZLE_CHANNEL);
		SERIALIZER_MAP.put(UDataType.class, DATA_TYPE);
		SERIALIZER_MAP.put(UShaderType.class, SHADER_TYPE);
		SERIALIZER_MAP.put(UPrimitiveType.class, PRIMITIVE_TYPE);
	}

	@Override
	protected Map<Class<? extends Enum>, IGfxEnumSerializer> serializerMap() {
		return SERIALIZER_MAP;
	}

}
