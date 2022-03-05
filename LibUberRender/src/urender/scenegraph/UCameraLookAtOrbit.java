package urender.scenegraph;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * A camera that follows a target and rotates around it.
 */
public class UCameraLookAtOrbit extends UCameraPerspectiveBase {

	/**
	 * Position of the camera's interest.
	 */
	public final Vector3f target = new Vector3f();
	/**
	 * Rotation around the target. This rotation will be applied in Yaw/Pitch/Roll order.
	 */
	public final Vector3f rotation = new Vector3f();
	/**
	 * Translation to be applied after target translation and orbit rotation.
	 */
	public final Vector3f postTranslation = new Vector3f();

	@Override
	public void mulViewMatrix(Matrix4f dest) {
		dest.translate(target);
		dest.rotateYXZ(rotation);
		dest.translate(postTranslation);
	}
}
