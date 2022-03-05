package urender.scenegraph;

import org.joml.Vector3f;

/**
 * Default implementation of UNodeTransform with read/write fields for all properties.
 */
public class UDefaultNodeTransform implements UNodeTransform {

	/**
	 * Position vector.
	 */
	public final Vector3f translation = new Vector3f();
	/**
	 * Euler rotation in radians.
	 */
	public final Vector3f rotation = new Vector3f();
	/**
	 * Scaling vector.
	 */
	public final Vector3f scale = new Vector3f(1f);

	@Override
	public Vector3f getTranslation() {
		return translation;
	}

	@Override
	public Vector3f getRotation() {
		return rotation;
	}

	@Override
	public Vector3f getScale() {
		return scale;
	}

}
