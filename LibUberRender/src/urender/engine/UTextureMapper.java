package urender.engine;

import urender.api.UTextureMagFilter;
import urender.api.UTextureMinFilter;
import urender.api.UTextureWrap;

/**
 * Texture sampling parameter descriptor.
 */
public class UTextureMapper {

	String textureName;

	String shaderVariableName;

	UTextureWrap wrapU = UTextureWrap.REPEAT;
	UTextureWrap wrapV = UTextureWrap.REPEAT;
	UTextureMagFilter magFilter = UTextureMagFilter.LINEAR;
	UTextureMinFilter minFilter = UTextureMinFilter.LINEAR;

	/**
	 * Changes the texture shader bound to this texture mapper.
	 *
	 * @param tex
	 */
	public void setTexture(UTexture tex) {
		textureName = tex == null ? null : tex.getName();
	}

	/**
	 * Gets the local name of the texture bound to this texture mapper.
	 *
	 * @return
	 */
	public String getTextureName() {
		return textureName;
	}

	/**
	 * Gets the name of the sampler uniform that the underlying texture should be mapped to.
	 *
	 * @return
	 */
	public String getShaderVariableName() {
		return shaderVariableName;
	}

	/**
	 * Sets the name of the sampler uniform that the underlying texture should be mapped to.
	 *
	 * @param name
	 */
	public void setShaderVariableName(String name) {
		this.shaderVariableName = name;
	}

	/**
	 * Gets the horizontal texture wrap mode.
	 *
	 * @return
	 */
	public UTextureWrap getWrapU() {
		return wrapU;
	}

	/**
	 * Sets the horizontal texture wrap mode.
	 *
	 * @param wrapU
	 */
	public void setWrapU(UTextureWrap wrapU) {
		this.wrapU = wrapU;
	}

	/**
	 * Gets the vertical texture wrap mode.
	 *
	 * @return
	 */
	public UTextureWrap getWrapV() {
		return wrapV;
	}

	/**
	 * Sets the vertical texture wrap mode.
	 *
	 * @param wrapV
	 */
	public void setWrapV(UTextureWrap wrapV) {
		this.wrapV = wrapV;
	}

	/**
	 * Gets the filtering mode for upscaling texels.
	 *
	 * @return
	 */
	public UTextureMagFilter getMagFilter() {
		return magFilter;
	}

	/**
	 * Sets the filtering mode for upscaling texels.
	 *
	 * @param magFilter
	 */
	public void setMagFilter(UTextureMagFilter magFilter) {
		this.magFilter = magFilter;
	}

	/**
	 * Gets the filtering mode for downscaling texels.
	 *
	 * @return
	 */
	public UTextureMinFilter getMinFilter() {
		return minFilter;
	}

	/**
	 * Sets the filtering mode for downscaling texels.
	 *
	 * @param minFilter
	 */
	public void setMinFilter(UTextureMinFilter minFilter) {
		this.minFilter = minFilter;
	}
}
