package urender.engine;

import urender.api.UObjHandle;
import urender.api.UTextureFormat;

public abstract class UTexture extends UGfxObject {
	
	protected final UObjHandle __handle = new UObjHandle();
	
	public int width;
	public int height;
	
	public UTextureFormat format;

	protected abstract void setup(UGfxRenderer rnd);
}
