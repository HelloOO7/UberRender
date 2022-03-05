package urender.scenegraph;

import org.joml.Matrix4f;

/**
 * Base class for perspective projection cameras.
 */
public abstract class UCameraPerspectiveBase extends UCamera {

	/**
	 * Field of View.
	 */
	public float FOV = (float) Math.toRadians(45f);
	/**
	 * Projection aspect ratio.
	 */
	public float aspect = 16f / 9f;
	
	@Override
	public void getProjectionMatrix(Matrix4f dest) {
		dest.setPerspective(FOV, aspect, zNear, zFar);
	}
}
