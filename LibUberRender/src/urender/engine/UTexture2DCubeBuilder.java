package urender.engine;

import java.util.ArrayList;
import java.util.List;

public class UTexture2DCubeBuilder extends UTextureBuilder {

	private List<UTexture2DCube.UTextureCubeFace> faces = new ArrayList<>();

	/**
	 * Adds a face to the cubemap.
	 *
	 * @param face
	 */
	public void addFace(UTexture2DCube.UTextureCubeFace face) {
		faces.add(face);
	}

	@Override
	public UTexture build() {
		return new UTexture2DCube(name, width, height, format, faces);
	}

}
