package urender.engine;

import urender.api.UObjHandle;
import urender.api.UTextureFormat;
import urender.api.UTextureType;
import urender.api.backend.RenderingBackend;

public abstract class UTexture extends UGfxEngineObject {

	protected final UObjHandle __handle = new UObjHandle();

	protected int width;
	protected int height;

	public final UTextureFormat format;

	public final UTextureSwizzleMask swizzleMask;

	public abstract void setup(RenderingBackend rnd);

	public abstract UTextureType getTextureType();

	protected UTexture(String name, int width, int height, UTextureFormat format, UTextureSwizzleMask swizzleMask) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.format = format;
		this.swizzleMask = swizzleMask;
	}

	protected UTexture(String name, int width, int height, UTextureFormat format) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.format = format;
		this.swizzleMask = new UTextureSwizzleMask();
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public void renameTo(String newName) {
		this.name = newName;
	}

	@Override
	public UGfxEngineObjectType getType() {
		return UGfxEngineObjectType.TEXTURE;
	}
}
