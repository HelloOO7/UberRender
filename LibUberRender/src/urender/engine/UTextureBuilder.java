package urender.engine;

import urender.api.UTextureFormat;
import urender.common.IBuilder;

public abstract class UTextureBuilder implements IBuilder<UTexture> {

	protected String name;
	protected int width;
	protected int height;
	protected UTextureFormat format;
	protected UTextureSwizzleMask swizzleMask = new UTextureSwizzleMask();
	
	public UTextureBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
	public UTextureBuilder setWidth(int width) {
		this.width = width;
		return this;
	}
	
	public UTextureBuilder setHeight(int height) {
		this.height = height;
		return this;
	}
	
	public UTextureBuilder setFormat(UTextureFormat format) {
		this.format = format;
		return this;
	}
	
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
