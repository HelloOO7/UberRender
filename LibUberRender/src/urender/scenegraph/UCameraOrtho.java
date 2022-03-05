package urender.scenegraph;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Orthogonal projection camera.
 */
public class UCameraOrtho extends UCamera {

	/**
	 * Top coordinate of the projection.
	 */
	public float top;
	/**
	 * Bottom coordinate of the projection.
	 */
	public float bottom;
	/**
	 * Left coordinate of the projection.
	 */
	public float left;
	/**
	 * Right coordinate of the projection.
	 */
	public float right;

	/**
	 * Location (eye position) of the camera.
	 */
	public final Vector3f translation = new Vector3f();
	/**
	 * Rotation of the view.
	 */
	public final Vector3f rotation = new Vector3f();

	@Override
	public void mulViewMatrix(Matrix4f dest) {
		dest.translate(translation);
		dest.rotateZYX(rotation);
	}

	@Override
	public void getProjectionMatrix(Matrix4f dest) {
		dest.setOrtho(left, right, bottom, top, zNear, zFar);
	}
}
