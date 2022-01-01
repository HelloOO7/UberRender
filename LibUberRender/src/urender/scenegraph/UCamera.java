package urender.scenegraph;

import org.joml.Matrix4f;

public abstract class UCamera extends UGfxScenegraphObject {
	public float zNear = 1f;
	public float zFar = 5000f;
	
	public abstract void getProjectionMatrix(Matrix4f dest);
	public abstract void mulViewMatrix(Matrix4f dest);
	
	@Override
	public UGfxScenegraphObjectType getType() {
		return UGfxScenegraphObjectType.CAMERA;
	}
}
