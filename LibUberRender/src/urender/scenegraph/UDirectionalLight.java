package urender.scenegraph;

import org.joml.Vector3f;

public class UDirectionalLight extends ULight {

	public Vector3f direction = new Vector3f();
	
	public ULightColors colors = new ULightColors();

	@Override
	public ULightType getLightType() {
		return ULightType.DIRECTIONAL;
	}
}
