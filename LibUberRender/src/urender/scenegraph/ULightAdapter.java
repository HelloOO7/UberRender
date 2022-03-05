package urender.scenegraph;

import java.util.Collection;
import urender.engine.shader.UUniform;

/**
 * Class for interfacing between an engine-specific lighting model and the scenegraph light configuration.
 */
public interface ULightAdapter {

	/**
	 * Provides scene lights to the adapter for further calculations.
	 *
	 * @param lights
	 */
	public void setLights(Collection<? extends ULight> lights);

	/**
	 * Gets all uniforms that should be provided to a shader for lighting calculations (including light source
	 * structures).
	 *
	 * @return
	 */
	public Collection<UUniform> getLightUniforms();
}
