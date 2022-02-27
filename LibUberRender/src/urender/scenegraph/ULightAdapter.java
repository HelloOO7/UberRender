package urender.scenegraph;

import java.util.Collection;
import urender.engine.shader.UUniform;

public interface ULightAdapter {
	public void setLights(Collection<? extends ULight> lights);
	public Collection<UUniform> getLightUniforms();
}
