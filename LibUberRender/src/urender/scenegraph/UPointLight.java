package urender.scenegraph;

import org.joml.Vector3f;

public class UPointLight extends ULight {

	public Vector3f position = new Vector3f();
	
	public ULightColors colors = new ULightColors();
	
	public float constantAttn = 1f;
	public float linearAttn = 0.045f;
	public float quadraticAttn = 0.0075f;

	@Override
	public ULightType getLightType() {
		return ULightType.POINT;
	}
}
