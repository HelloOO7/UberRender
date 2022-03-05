package urender.scenegraph;

import org.joml.Vector3f;

/**
 * Point light that only illuminates a cone area.
 */
public class USpotLight extends UPointLight {

	/**
	 * Direction of the spotlight cone.
	 */
	public Vector3f spotDirection = new Vector3f();
	/**
	 * Cutoff angle of the spotlight in degrees, from 0 to 180.
	 */
	public float cutoffAngleDeg = 90f;

	@Override
	public ULightType getLightType() {
		return ULightType.SPOT;
	}
}
