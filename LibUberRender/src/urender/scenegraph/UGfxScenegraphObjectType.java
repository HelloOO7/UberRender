package urender.scenegraph;

/**
 * Enum type ID of UGfxScenegraphObject child classes.
 */
public enum UGfxScenegraphObjectType {
	/**
	 * 3D scene root.
	 */
	SCENE,
	/**
	 * Scene unit 3D model.
	 */
	MODEL,
	/**
	 * Camera-like transform.
	 */
	CAMERA,
	/**
	 * Light source.
	 */
	LIGHT
}
