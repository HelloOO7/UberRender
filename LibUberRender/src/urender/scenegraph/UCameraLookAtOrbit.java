package urender.scenegraph;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class UCameraLookAtOrbit extends UCameraPerspectiveBase {
	
	public final Vector3f target = new Vector3f();
	public final Vector3f rotation = new Vector3f();
	public final Vector3f postTranslation = new Vector3f();
	
	@Override
	public void mulViewMatrix(Matrix4f dest) {
		dest.translate(target);
		dest.rotateYXZ(rotation);
		dest.translate(postTranslation);
	}
}
