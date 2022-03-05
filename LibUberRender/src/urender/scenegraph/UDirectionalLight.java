package urender.scenegraph;

import org.joml.Vector3f;

/**
 * Omnipresent directional light source.
 */
public class UDirectionalLight extends ULight {

	/**
	 * Direction vector of the light.
	 */
	public Vector3f direction = new Vector3f();

	/**
	 * Lighting color factors.
	 */
	public ULightColors colors = new ULightColors();

	@Override
	public ULightType getLightType() {
		return ULightType.DIRECTIONAL;
	}
}
