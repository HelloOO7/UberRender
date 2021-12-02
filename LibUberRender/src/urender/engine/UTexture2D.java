package urender.engine;

import java.nio.ByteBuffer;
import urender.api.UTextureType;
import urender.api.backend.RenderingBackend;

public class UTexture2D extends UTexture {
	public ByteBuffer data;
	
	@Override
	public void setup(UGfxRenderer rnd) {
		RenderingBackend core = rnd.getCore();
		
		if (!__handle.isInitialized(core)) {
			core.texInit(__handle, UTextureType.TEX2D);
			__handle.forceUpload(core);
		}
		
		if (__handle.getAndResetForceUpload(core)) {
			core.texUploadData2D(__handle, width, height, format, null, data);
		}
	}

	@Override
	public UTextureType getTextureType() {
		return UTextureType.TEX2D;
	}
}
