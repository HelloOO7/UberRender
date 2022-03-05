package urender.scenegraph;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * A camera with a location and rotation.
 */
public class UCameraViewpoint extends UCameraPerspectiveBase {

	/**
	 * Location (eye position) of the camera.
	 */
	public final Vector3f position = new Vector3f();
	/**
	 * Rotation that represents the direction in which the camera is looking.
	 */
	public final Vector3f rotation = new Vector3f();

	@Override
	public void mulViewMatrix(Matrix4f dest) {
		dest.translate(position);
		dest.rotateZYX(rotation);
	}
}
