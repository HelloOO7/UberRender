package urender.api;

/**
 * Defines texture wrapping mode for coordinates outside of the 0.0 - 1.0 range.
 */
public enum UTextureWrap {
	/**
	 * Clamp to the color of the closest edge of the texture image.
	 */
	CLAMP_TO_EDGE,
	/**
	 * Clamp to the user-defined border color render state.
	 */
	CLAMP_TO_BORDER,
	/**
	 * Repeat the base texture image (effectively float modulo 1.0).
	 */
	REPEAT,
	/**
	 * Repeat the base texture image, while flipping coordinates with each repeat.
	 */
	MIRRORED_REPEAT
}
