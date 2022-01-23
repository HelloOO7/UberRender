package urender.engine;

import urender.api.UTextureMagFilter;
import urender.api.UTextureMinFilter;
import urender.api.UTextureWrap;
import urender.common.IBuilder;

public class UTextureMapperBuilder implements IBuilder<UTextureMapper> {
	private UTextureMapper mapper = new UTextureMapper();
	
	public UTextureMapperBuilder setTextureName(String name) {
		mapper.textureName = name;
		return this;
	}
	
	public UTextureMapperBuilder setShaderVariableName(String name) {
		mapper.shaderVariableName = name;
		return this;
	}
	
	public UTextureMapperBuilder setWrapU(UTextureWrap wrap) {
		mapper.wrapU = wrap;
		return this;
	}
	
	public UTextureMapperBuilder setWrapV(UTextureWrap wrap) {
		mapper.wrapV = wrap;
		return this;
	}
	
	public UTextureMapperBuilder setMagFilter(UTextureMagFilter magFilter) {
		mapper.magFilter = magFilter;
		return this;
	}
	
	public UTextureMapperBuilder setMinFilter(UTextureMinFilter minFilter) {
		mapper.minFilter = minFilter;
		return this;
	}
	
	@Override
	public UTextureMapper build() {
		return mapper;
	}

	@Override
	public void reset() {
		mapper = new UTextureMapper();
	}
}
