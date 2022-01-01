package urender.scenegraph;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class UCameraOrtho extends UCamera {

	public float top;
	public float bottom;
	public float left;
	public float right;
	
	public final Vector3f translation = new Vector3f();
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
