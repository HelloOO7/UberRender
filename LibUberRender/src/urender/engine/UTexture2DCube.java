package urender.engine;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import urender.api.UTextureFaceAssignment;
import urender.api.UTextureFormat;
import urender.api.UTextureType;
import urender.api.backend.RenderingBackend;

public class UTexture2DCube extends UTexture {

	public List<UTextureCubeFace> faces = new ArrayList<>();

	public UTexture2DCube(String name, int width, int height, UTextureFormat format, List<UTextureCubeFace> faces) {
		super(name, width, height, format);
		this.faces.addAll(faces);
	}

	@Override
	public void setup(UGfxRenderer rnd) {
		RenderingBackend core = rnd.getCore();

		if (!__handle.isInitialized(core)) {
			core.texInit(__handle, UTextureType.TEX2D_CUBEMAP);
		}

		if (__handle.getAndResetForceUpload(core)) {
			for (UTextureCubeFace face : faces) {
				core.texUploadData2D(__handle, width, height, format, face.assignment, face.data);
			}
		}
	}

	@Override
	public UTextureType getTextureType() {
		return UTextureType.TEX2D_CUBEMAP;
	}

	public static class UTextureCubeFace {

		public UTextureFaceAssignment assignment;
		public Buffer data;
		
		public UTextureCubeFace(UTextureFaceAssignment assignment, Buffer data) {
			this.assignment = assignment;
			this.data = data;
		}
	}
}
