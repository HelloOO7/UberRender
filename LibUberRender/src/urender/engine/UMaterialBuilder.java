package urender.engine;

public class UMaterialBuilder extends UGfxObjectBuilder<UMaterial> {

	private UMaterial material = new UMaterial();

	/**
	 * Sets the name of the shader program to be used for rendering using this material.
	 *
	 * @param name Local name of the shader program.
	 * @return this
	 */
	public UMaterialBuilder setShaderProgramName(String name) {
		material.shaderProgramName = name;
		return this;
	}

	/**
	 * Sets the layer that meshes that use this material should be drawn on.
	 *
	 * @param layer
	 * @return
	 */
	public UMaterialBuilder setDrawLayer(UMaterialDrawLayer layer) {
		material.drawLayer = layer;
		return this;
	}

	/**
	 * Adds a texture mapper to the material.
	 *
	 * @param mapper
	 * @return
	 */
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
