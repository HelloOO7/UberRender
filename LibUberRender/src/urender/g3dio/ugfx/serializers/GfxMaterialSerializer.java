package urender.g3dio.ugfx.serializers;

import java.io.IOException;
import urender.api.UTextureMagFilter;
import urender.api.UTextureMinFilter;
import urender.api.UTextureWrap;
import urender.common.io.base.iface.DataInputEx;
import urender.common.io.base.iface.DataOutputEx;
import urender.engine.UMaterial;
import urender.engine.UMaterialBuilder;
import urender.engine.UTextureMapper;
import urender.engine.UTextureMapperBuilder;
import urender.g3dio.ugfx.adapters.IGfxResourceConsumer;

public class GfxMaterialSerializer implements IGfxResourceSerializer<UMaterial> {

	private static final UTextureMagFilter[] MAG_FILTER_LOOKUP = new UTextureMagFilter[]{
		UTextureMagFilter.LINEAR,
		UTextureMagFilter.NEAREST_NEIGHBOR
	};
	
	private static final UTextureMinFilter[] MIN_FILTER_LOOKUP = new UTextureMinFilter[]{
		UTextureMinFilter.LINEAR,
		UTextureMinFilter.NEAREST_NEIGHBOR,
		UTextureMinFilter.LINEAR_MIPMAP_LINEAR,
		UTextureMinFilter.LINEAR_MIPMAP_NEAREST,
		UTextureMinFilter.NEAREST_MIPMAP_LINEAR,
		UTextureMinFilter.LINEAR_MIPMAP_NEAREST
	};
	
	private static final UTextureWrap[] WRAP_LOOKUP = new UTextureWrap[]{
		UTextureWrap.REPEAT,
		UTextureWrap.MIRRORED_REPEAT,
		UTextureWrap.CLAMP_TO_EDGE,
		UTextureWrap.CLAMP_TO_BORDER
	};

	@Override
	public String getTagIdent() {
		return "MATL";
	}

	@Override
	public void deserialize(DataInputEx in, IGfxResourceConsumer consumer) throws IOException {
		UMaterialBuilder bld = new UMaterialBuilder();

		bld.setName(in.readString());
		bld.setShaderProgramName(in.readString());

		int mapperCount = in.read();
		UTextureMapperBuilder mapperBld = new UTextureMapperBuilder();
		for (int i = 0; i < mapperCount; i++) {
			mapperBld.reset();
			bld.addTextureMapper(
				mapperBld
					.setTextureName(in.readString())
					.setMeshUVSetName(in.readString())
					.setShaderVariableName(in.readString())
					
					.setTransformTranslation(in.readFloat(), in.readFloat())
					.setTransformScale(in.readFloat(), in.readFloat())
					.setTransformRotation(in.readFloat())
					
					.setMagFilter(MAG_FILTER_LOOKUP[in.read()])
					.setMinFilter(MIN_FILTER_LOOKUP[in.read()])
					.setWrapU(WRAP_LOOKUP[in.read()])
					.setWrapV(WRAP_LOOKUP[in.read()])
					.build()
			);
		}

		consumer.loadObject(bld.build());
	}

	@Override
	public void serialize(UMaterial mat, DataOutputEx out) throws IOException {
		out.writeString(mat.getName());
		out.writeString(mat.getShaderProgramName());
		
		out.write(mat.getTextureMapperCount());
		for (UTextureMapper mapper : mat.textureMappers()) {
			out.writeString(mapper.getTextureName());
			out.writeString(mapper.getMeshUVSetName());
			out.writeString(mapper.getShaderVariableName());
			
			out.writeFloats(mapper.transform.translation.x, mapper.transform.translation.y);
			out.writeFloats(mapper.transform.scale.x, mapper.transform.scale.y);
			out.writeFloat(mapper.transform.rotation);
			
			out.write(IGfxResourceSerializer.findEnumIndex(MAG_FILTER_LOOKUP, mapper.getMagFilter()));
			out.write(IGfxResourceSerializer.findEnumIndex(MIN_FILTER_LOOKUP, mapper.getMinFilter()));
			out.write(IGfxResourceSerializer.findEnumIndex(WRAP_LOOKUP, mapper.getWrapU()));
			out.write(IGfxResourceSerializer.findEnumIndex(WRAP_LOOKUP, mapper.getWrapV()));
		}
	}

	@Override
	public boolean accepts(Object o) {
		return o instanceof UMaterial;
	}
}
