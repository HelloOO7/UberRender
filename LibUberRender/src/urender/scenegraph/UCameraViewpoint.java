package urender.scenegraph;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class UCameraViewpoint extends UCameraPerspectiveBase {

	public final Vector3f position = new Vector3f();
	public final Vector3f rotation = new Vector3f();
	
	@Override
	public void mulViewMatrix(Matrix4f dest) {
		dest.translate(position);
		dest.rotateZYX(rotation);
	}
}
