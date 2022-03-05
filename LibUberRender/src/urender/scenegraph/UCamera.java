package urender.scenegraph;

import org.joml.Matrix4f;

public abstract class UCamera extends UGfxScenegraphObject {
	/**
	 * Near clip plane Z.
	 */
	public float zNear = 1f;
	/**
	 * Far clip plane Z.
	 */
	public float zFar = 5000f;
	
	/**
	 * Calculates the projection matrix.
	 * @param dest 
	 */
	public abstract void getProjectionMatrix(Matrix4f dest);
	/**
	 * Multiplies 'dest' with a calculated view matrix.
	 * @param dest 
	 */
	public abstract void mulViewMatrix(Matrix4f dest);
	
	@Override
	public UGfxScenegraphObjectType getType() {
		return UGfxScenegraphObjectType.CAMERA;
	}
}
