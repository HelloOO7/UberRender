package urender.engine;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import urender.api.UTextureFaceAssignment;
import urender.api.UTextureType;
import urender.api.backend.RenderingBackend;

public class UTexture2DCube extends UTexture {

	public List<UTexture3DFace> faces = new ArrayList<>();

	@Override
	public void setup(UGfxRenderer rnd) {
		RenderingBackend core = rnd.getCore();

		if (!__handle.isInitialized(core)) {
			core.texInit(__handle, UTextureType.TEX2D_CUBEMAP);
		}

		if (__handle.getAndResetForceUpload(core)) {
			for (UTexture3DFace face : faces) {
				core.texUploadData2D(__handle, width, height, format, face.assignment, face.data);
			}
		}
	}

	@Override
	public UTextureType getType() {
		return UTextureType.TEX2D_CUBEMAP;
	}

	public static class UTexture3DFace {

		public UTextureFaceAssignment assignment;
		public Buffer data;
	}
}
