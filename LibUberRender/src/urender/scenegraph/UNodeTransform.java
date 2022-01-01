package urender.scenegraph;

import org.joml.Vector3f;

public interface UNodeTransform {
	public Vector3f getTranslation();
	public Vector3f getRotation();
	public Vector3f getScale();
}
