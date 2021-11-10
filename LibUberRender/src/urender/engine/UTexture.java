package urender.engine;

import urender.api.UObjHandle;
import urender.api.UTextureFormat;
import urender.api.UTextureType;

public abstract class UTexture extends UGfxObject {
	
	protected final UObjHandle __handle = new UObjHandle();
	
	public int width;
	public int height;
	
	public UTextureFormat format;

	public abstract void setup(UGfxRenderer rnd);
	
	public abstract UTextureType getType();
}
