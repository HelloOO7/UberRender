package urender.engine;

import urender.api.UTextureFormat;

/**
 * Texture resource builder.
 */
public abstract class UTextureBuilder extends UGfxObjectBuilder<UTexture> {

	protected String name;
	protected int width;
	protected int height;
	protected UTextureFormat format;
	protected UTextureSwizzleMask swizzleMask = new UTextureSwizzleMask();

	@Override
	protected UTexture getObject() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public UTextureBuilder setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Sets the texture image width.
	 *
	 * @param width
	 * @return this
	 */
	public UTextureBuilder setWidth(int width) {
		this.width = width;
		return this;
	}

	/**
	 * Sets the texture image height.
	 *
	 * @param height
	 * @return this
	 */
	public UTextureBuilder setHeight(int height) {
		this.height = height;
		return this;
	}

	/**
	 * Sets the texture's pixel format.
	 *
	 * @param format
	 * @return this
	 */
	public UTextureBuilder setFormat(UTextureFormat format) {
		this.format = format;
		return this;
	}

	/**
	 * Sets the texture channel swizzle mask.
	 *
	 * @param swizzleMask
	 * @return this
	 */
	public UTextureBuilder setSwizzleMask(UTextureSwizzleMask swizzleMask) {
		this.swizzleMask = swizzleMask;
		return this;
	}

	@Override
	public void reset() {
		name = null;
		width = 0;
		height = 0;
		format = null;
	}
}
