package urender.g3dio.ugfx.serializers;

import java.io.IOException;
import urender.api.UTextureMagFilter;
import urender.api.UTextureMinFilter;
import urender.api.UTextureWrap;
import urender.common.io.base.iface.DataOutputEx;
import urender.engine.UMaterial;
import urender.engine.UMaterialBuilder;
import urender.engine.UMaterialDrawLayer;
import urender.engine.UTextureMapper;
import urender.engine.UTextureMapperBuilder;
import urender.g3dio.ugfx.UGfxDataInput;
import urender.g3dio.ugfx.UGfxDataOutput;
import urender.g3dio.ugfx.UGfxFormatRevisions;
import urender.g3dio.ugfx.adapters.IGfxResourceConsumer;

public class GfxMaterialSerializer implements IGfxResourceSerializer<UMaterial> {

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
			bld.setDrawLayer(new UMaterialDrawLayer(in.readEnum(UMaterialDrawLayer.ShadingMethod.class), in.readUnsignedShort()));
		}

		int mapperCount = in.read();
		UTextureMapperBuilder mapperBld = new UTextureMapperBuilder();
		for (int i = 0; i < mapperCount; i++) {
			mapperBld.reset();
			bld.addTextureMapper(
				mapperBld
					.setTextureName(in.readString())
					.setShaderVariableName(in.readString())
					.setMagFilter(in.readEnum(UTextureMagFilter.class))
					.setMinFilter(in.readEnum(UTextureMinFilter.class))
					.setWrapU(in.readEnum(UTextureWrap.class))
					.setWrapV(in.readEnum(UTextureWrap.class))
					.build()
			);
		}

		UMaterial mat = bld.build();

		GfxUniformListIO.readUniformList(mat.shaderParams, in);

		consumer.loadObject(mat);
	}

	@Override
	public void serialize(UMaterial mat, UGfxDataOutput out) throws IOException {
		out.writeString(mat.getName());
		out.writeString(mat.getShaderProgramName());
		out.writeEnum(mat.getDrawLayer().method);
		out.writeShort(mat.getDrawLayer().priority);

		out.write(mat.getTextureMapperCount());
		for (UTextureMapper mapper : mat.getTextureMappers()) {
			out.writeString(mapper.getTextureName());
			out.writeString(mapper.getShaderVariableName());
			
			out.writeEnum(mapper.getMagFilter());
			out.writeEnum(mapper.getMinFilter());
			out.writeEnum(mapper.getWrapU());
			out.writeEnum(mapper.getWrapV());
		}

		GfxUniformListIO.writeUniformList(mat.shaderParams, out);
	}

	@Override
	public boolean accepts(Object o) {
		return o instanceof UMaterial;
	}
}
