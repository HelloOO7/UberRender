package urender.scenegraph;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * A camera that looks at a target.
 */
public class UCameraLookAt extends UCameraPerspectiveBase {

	/**
	 * Location (eye position) of the camera.
	 */
	public final Vector3f eye = new Vector3f();
	/**
	 * Up vector of the camera.
	 */
	public final Vector3f up = new Vector3f();
	/**
	 * The point to look at.
	 */
	public final Vector3f target = new Vector3f();

	@Override
	public void mulViewMatrix(Matrix4f dest) {
		dest.lookAt(eye, target, up);
	}
}
