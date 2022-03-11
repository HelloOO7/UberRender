package urender.engine;

import java.nio.ByteBuffer;
import urender.api.UTextureFormat;
import urender.api.UTextureType;
import urender.api.backend.RenderingBackend;

/**
 * Single-face 2D texture resource.
 */
public class UTexture2D extends UTexture {

	public final ByteBuffer data;

	/**
	 * Creates a 2D texture resource.
	 *
	 * @param name Local name of the resource.
	 * @param width Width of the texture image.
	 * @param height Height of the texture image.
	 * @param format Pixel format of the data.
	 * @param data Raw texture data, aligned to 4-bytes.
	 */
	public UTexture2D(String name, int width, int height, UTextureFormat format, ByteBuffer data) {
		super(name, width, height, format);
		this.data = data;
	}

	/**
	 * Creates a 2D texture resource.
	 *
	 * @param name Local name of the resource.
	 * @param width Width of the texture image.
	 * @param height Height of the texture image.
	 * @param format Pixel format of the data.
	 * @param data Raw texture data, aligned to 4-bytes.
	 * @param swizzleMask Texture channel swizzle mask
	 */
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
