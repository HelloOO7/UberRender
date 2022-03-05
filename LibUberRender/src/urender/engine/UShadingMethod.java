package urender.engine;

/**
 * Shader paradigm type.
 */
public enum UShadingMethod {
	/**
	 * Forward shading that works directly on vertex attributes.
	 */
	FORWARD,
	/**
	 * Deferred shading that uses G-buffer render targets.
	 */
	DEFERRED
}
