package urender.scenegraph;

import org.joml.Vector3f;

/**
 * Interface for scale/rotation/translation transform of scenegraph nodes.
 */
public interface UNodeTransform {

	/**
	 * Gets the translation component of the transform.
	 *
	 * @return
	 */
	public Vector3f getTranslation();

	/**
	 * Gets the rotation component of the transform.
	 *
	 * @return XYZ euler rotation in radians.
	 */
	public Vector3f getRotation();

	/**
	 * Gets the scale component of the transform.
	 *
	 * @return
	 */
	public Vector3f getScale();
}
