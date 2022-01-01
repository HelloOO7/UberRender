package urender.scenegraph;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class UCameraLookAt extends UCameraPerspectiveBase {
	
	public final Vector3f eye = new Vector3f();
	public final Vector3f up = new Vector3f();
	public final Vector3f target = new Vector3f();
	
	@Override
	public void mulViewMatrix(Matrix4f dest) {
		dest.lookAt(eye, target, up);
	}
}
