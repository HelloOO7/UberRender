package urender.engine;

public class UMaterialBuilder extends UGfxObjectBuilder<UMaterial> {
	private UMaterial material = new UMaterial();
	
	public UMaterialBuilder setShaderProgramName(String name) {
		material.shaderProgramName = name;
		return this;
	}
	
	public UMaterialBuilder setDrawLayer(UMaterialDrawLayer layer) {
		material.drawLayer = layer;
		return this;
	}
	
	public UMaterialBuilder addTextureMapper(UTextureMapper mapper) {
		material.textureMappers.add(mapper);
		return this;
	}
	
	@Override
	public UMaterial build() {
		return material;
	}

	@Override
	public void reset() {
		material = new UMaterial();
	}

	@Override
	protected UMaterial getObject() {
		return material;
	}
}
