package urender.scenegraph;

import java.util.ArrayList;
import java.util.List;

public class UModel extends UGfxScenegraphObject {

	public final List<UMeshInstance> meshes = new ArrayList<>();

	@Override
	public UGfxScenegraphObjectType getType() {
		return UGfxScenegraphObjectType.MODEL;
	}

	public static class UMeshInstance {

		public String meshName;
		public String materialName;
	}
}
