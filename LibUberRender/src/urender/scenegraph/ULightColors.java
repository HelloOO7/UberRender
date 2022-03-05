package urender.scenegraph;

import org.joml.Vector3f;

public class ULightColors {

	/**
	 * Ambient light color. Present in full scale wherever the light can reach.
	 */
	public Vector3f ambient = new Vector3f(1.0f);
	/**
	 * Diffuse light color. Calculated from the angle between the light and the fragment normal.
	 */
	public Vector3f diffuse = new Vector3f(1.0f);
	/**
	 * Specular light color. Calculated from the angle between the view direction and the reflection of the
	 * light along the fragment normal.
	 */
	public Vector3f specular = new Vector3f(0.0f);
}
