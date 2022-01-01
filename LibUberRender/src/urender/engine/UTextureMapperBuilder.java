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
	
	public UTextureMapperBuilder setMeshUVSetName(String name) {
		mapper.meshUVSetName = name;
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
	
	public UTextureMapperBuilder setTransformTranslation(float x, float y) {
		mapper.transform.translation.set(x, y);
		return this;
	}
	
	public UTextureMapperBuilder setTransformScale(float x, float y) {
		mapper.transform.scale.set(x, y);
		return this;
	}
	
	public UTextureMapperBuilder setTransformRotation(float alpha) {
		mapper.transform.rotation = alpha;
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
