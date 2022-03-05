package urender.scenegraph;

import org.joml.Vector3f;

/**
 * Point light source.
 */
public class UPointLight extends ULight {

	/**
	 * Location of the light in the scene.
	 */
	public Vector3f position = new Vector3f();

	/**
	 * Lighting color factors.
	 */
	public ULightColors colors = new ULightColors();

	/**
	 * Constant attenuation factor.
	 */
	public float constantAttn = 1f;
	/**
	 * Linear attenuation factor.
	 */
	public float linearAttn = 0.045f;
	/**
	 * Quadratic attenuation factor.
	 */
	public float quadraticAttn = 0.0075f;

	@Override
	public ULightType getLightType() {
		return ULightType.POINT;
	}
}
