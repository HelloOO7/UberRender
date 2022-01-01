package urender.scenegraph;

import org.joml.Vector3f;

public class UDefaultNodeTransform implements UNodeTransform {

	public final Vector3f translation = new Vector3f();
	public final Vector3f rotation = new Vector3f();
	public final Vector3f scale = new Vector3f(1f);
	
	@Override
	public Vector3f getTranslation() {
		return translation;
	}

	@Override
	public Vector3f getRotation() {
		return rotation;
	}

	@Override
	public Vector3f getScale() {
		return scale;
	}

}
