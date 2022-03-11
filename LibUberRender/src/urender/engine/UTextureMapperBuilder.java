package urender.engine;

import urender.api.UTextureMagFilter;
import urender.api.UTextureMinFilter;
import urender.api.UTextureWrap;
import urender.common.IBuilder;

/**
 * Texture mapper builder.
 */
public class UTextureMapperBuilder implements IBuilder<UTextureMapper> {

	private UTextureMapper mapper = new UTextureMapper();

	/**
	 * Sets the local name of the texture bound to this texture mapper.
	 *
	 * @param name
	 * @return this
	 */
	public UTextureMapperBuilder setTextureName(String name) {
		mapper.textureName = name;
		return this;
	}

	/**
	 * Sets the name of the sampler uniform that the underlying texture should be mapped to.
	 *
	 * @param name
	 * @return this
	 */
	public UTextureMapperBuilder setShaderVariableName(String name) {
		mapper.shaderVariableName = name;
		return this;
	}

	/**
	 * Sets the horizontal texture wrap mode.
	 *
	 * @param wrap
	 * @return this
	 */
	public UTextureMapperBuilder setWrapU(UTextureWrap wrap) {
		mapper.wrapU = wrap;
		return this;
	}

	/**
	 * Sets the vertical texture wrap mode.
	 *
	 * @param wrap
	 * @return this
	 */
	public UTextureMapperBuilder setWrapV(UTextureWrap wrap) {
		mapper.wrapV = wrap;
		return this;
	}

	/**
	 * Sets the filtering mode for upscaling texels.
	 *
	 * @param magFilter
	 * @return this
	 */
	public UTextureMapperBuilder setMagFilter(UTextureMagFilter magFilter) {
		mapper.magFilter = magFilter;
		return this;
	}

	/**
	 * Sets the filtering mode for downscaling texels.
	 *
	 * @param minFilter
	 * @return this
	 */
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
