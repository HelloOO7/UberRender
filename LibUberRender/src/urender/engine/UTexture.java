package urender.engine;

import urender.api.UObjHandle;
import urender.api.UTextureFormat;
import urender.api.UTextureType;

public abstract class UTexture extends UGfxEngineObject {
	
	protected final UObjHandle __handle = new UObjHandle();
	
	public final int width;
	public final int height;
	
	public final UTextureFormat format;

	public abstract void setup(UGfxRenderer rnd);
	
	public abstract UTextureType getTextureType();
	
	protected UTexture(String name, int width, int height, UTextureFormat format) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.format = format;
	}
	
	@Override
	public UGfxEngineObjectType getType() {
		return UGfxEngineObjectType.TEXTURE;
	}
}
