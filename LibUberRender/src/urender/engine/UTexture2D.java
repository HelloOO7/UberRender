package urender.engine;

import java.nio.ByteBuffer;
import urender.api.UTextureFormat;
import urender.api.UTextureType;
import urender.api.backend.RenderingBackend;

public class UTexture2D extends UTexture {
	public final ByteBuffer data;
	
	public UTexture2D(String name, int width, int height, UTextureFormat format, ByteBuffer data) {
		super(name, width, height, format);
		this.data = data;
	}
	
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
