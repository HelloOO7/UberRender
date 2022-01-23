package urender.g3dio.ugfx.serializers;

import java.io.IOException;
import urender.api.UTextureMagFilter;
import urender.api.UTextureMinFilter;
import urender.api.UTextureWrap;
import urender.common.io.base.iface.DataInputEx;
import urender.common.io.base.iface.DataOutputEx;
import urender.engine.UMaterial;
import urender.engine.UMaterialBuilder;
import urender.engine.UMaterialDrawLayer;
import urender.engine.UTextureMapper;
import urender.engine.UTextureMapperBuilder;
import urender.g3dio.ugfx.UGfxDataInput;
import urender.g3dio.ugfx.UGfxFormatRevisions;
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

	private static final UMaterialDrawLayer.ShadingMethod[] SHADING_METHOD_LOOKUP = new UMaterialDrawLayer.ShadingMethod[]{
		UMaterialDrawLayer.ShadingMethod.FORWARD,
		UMaterialDrawLayer.ShadingMethod.DEFERRED
	};

	@Override
	public String getTagIdent() {
		return "MATL";
	}

	@Override
	public void deserialize(UGfxDataInput in, IGfxResourceConsumer consumer) throws IOException {
		UMaterialBuilder bld = new UMaterialBuilder();

		bld.setName(in.readString());
		bld.setShaderProgramName(in.readString());
		if (in.versionOver(UGfxFormatRevisions.MATERIAL_DRAW_LAYERS)) {
			bld.setDrawLayer(new UMaterialDrawLayer(SHADING_METHOD_LOOKUP[in.read()], in.readUnsignedShort()));
		}

		int mapperCount = in.read();
		UTextureMapperBuilder mapperBld = new UTextureMapperBuilder();
		for (int i = 0; i < mapperCount; i++) {
			mapperBld.reset();
			bld.addTextureMapper(
				mapperBld
					.setTextureName(in.readString())
					.setShaderVariableName(in.readString())
					.setMagFilter(MAG_FILTER_LOOKUP[in.read()])
					.setMinFilter(MIN_FILTER_LOOKUP[in.read()])
					.setWrapU(WRAP_LOOKUP[in.read()])
					.setWrapV(WRAP_LOOKUP[in.read()])
					.build()
			);
		}

		UMaterial mat = bld.build();

		GfxUniformListIO.readUniformList(mat.shaderParams, in);

		consumer.loadObject(mat);
	}

	@Override
	public void serialize(UMaterial mat, DataOutputEx out) throws IOException {
		out.writeString(mat.getName());
		out.writeString(mat.getShaderProgramName());
		out.write(IGfxResourceSerializer.findEnumIndex(SHADING_METHOD_LOOKUP, mat.getDrawLayer().method));
		out.writeShort(mat.getDrawLayer().priority);

		out.write(mat.getTextureMapperCount());
		for (UTextureMapper mapper : mat.getTextureMappers()) {
			out.writeString(mapper.getTextureName());
			out.writeString(mapper.getShaderVariableName());

			out.write(IGfxResourceSerializer.findEnumIndex(MAG_FILTER_LOOKUP, mapper.getMagFilter()));
			out.write(IGfxResourceSerializer.findEnumIndex(MIN_FILTER_LOOKUP, mapper.getMinFilter()));
			out.write(IGfxResourceSerializer.findEnumIndex(WRAP_LOOKUP, mapper.getWrapU()));
			out.write(IGfxResourceSerializer.findEnumIndex(WRAP_LOOKUP, mapper.getWrapV()));
		}

		GfxUniformListIO.writeUniformList(mat.shaderParams, out);
	}

	@Override
	public boolean accepts(Object o) {
		return o instanceof UMaterial;
	}
}
