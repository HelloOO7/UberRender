package urender.engine;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import urender.api.UTextureFaceAssignment;
import urender.api.UTextureFormat;
import urender.api.UTextureType;
import urender.api.backend.RenderingBackend;

/**
 * 2D Cubemap texture resource.
 */
public class UTexture2DCube extends UTexture {

	public List<UTextureCubeFace> faces = new ArrayList<>();

	/**
	 * Creates a cubemap texture resource.
	 *
	 * @param name Local name of the resource.
	 * @param width Width of the texture image.
	 * @param height Height of the texture image.
	 * @param format Pixel format of the data.
	 * @param faces List of texture face data.
	 */
	public UTexture2DCube(String name, int width, int height, UTextureFormat format, List<UTextureCubeFace> faces) {
		super(name, width, height, format);
		this.faces.addAll(faces);
	}

	@Override
	public void setup(RenderingBackend rnd) {
		if (!__handle.isInitialized(rnd)) {
			rnd.texInit(__handle, UTextureType.TEX2D_CUBEMAP);
		}

		if (__handle.getAndResetForceUpload(rnd)) {
			for (UTextureCubeFace face : faces) {
				rnd.texUploadData2D(__handle, width, height, format, face.assignment, face.data);
			}
		}

		swizzleMask.setup(rnd, this);
	}

	@Override
	public UTextureType getTextureType() {
		return UTextureType.TEX2D_CUBEMAP;
	}

	/**
	 * Face of a cubemap texture.
	 */
	public static class UTextureCubeFace {

		public UTextureFaceAssignment assignment;
		public ByteBuffer data;

		public UTextureCubeFace(UTextureFaceAssignment assignment, ByteBuffer data) {
			this.assignment = assignment;
			this.data = data;
		}
	}
}
