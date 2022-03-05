package urender.scenegraph;

import java.util.ArrayList;
import java.util.List;

/**
 * A set of meshes drawable as a scene actor.
 */
public class UModel extends UGfxScenegraphObject {

	/**
	 * List of meshes that should be drawn as part of this model.
	 */
	public final List<UMeshInstance> meshes = new ArrayList<>();

	@Override
	public UGfxScenegraphObjectType getType() {
		return UGfxScenegraphObjectType.MODEL;
	}

	/**
	 * Geometry and material coupled together into a drawable mesh.
	 */
	public static class UMeshInstance {

		/**
		 * Local name of the geometry mesh resource.
		 */
		public String meshName;
		/**
		 * Local name of the material resource.
		 */
		public String materialName;
	}
}
