package urender.scenegraph;

import org.joml.Matrix4f;

public abstract class UCameraPerspectiveBase extends UCamera {

	public float FOV = (float) Math.toRadians(45f);
	public float aspect = 16f / 9f;
	
	@Override
	public void getProjectionMatrix(Matrix4f dest) {
		dest.setPerspective(FOV, aspect, zNear, zFar);
	}
}
