package urender.scenegraph;

import org.joml.Vector3f;

public class USpotLight extends UPointLight {

	public Vector3f spotDirection = new Vector3f();
	public float cutoffAngleDeg = 90f;

	@Override
	public ULightType getLightType() {
		return ULightType.SPOT;
	}
}
