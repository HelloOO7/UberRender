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

	public UTexture2D(String name, int width, int height, UTextureFormat format, ByteBuffer data, UTextureSwizzleMask swizzleMask) {
		super(name, width, height, format, swizzleMask);
		this.data = data;
	}

	@Override
	public void setup(RenderingBackend rnd) {
		if (!__handle.isInitialized(rnd)) {
			rnd.texInit(__handle, UTextureType.TEX2D);
			__handle.forceUpload(rnd);
		}

		if (__handle.getAndResetForceUpload(rnd)) {
			swizzleMask.setup(rnd, this);
			rnd.texUploadData2D(__handle, width, height, format, null, data);
			//System.out.println("uploading texture " + getName() + " dim " + width + "x" + height + " format " + format + " swizzle " + swizzleMask);
		}
	}

	@Override
	public UTextureType getTextureType() {
		return UTextureType.TEX2D;
	}
}
