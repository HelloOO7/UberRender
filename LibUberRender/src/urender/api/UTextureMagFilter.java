package urender.api;

/**
 * Defines filtering/interpolation mode when upscaling texels.
 */
public enum UTextureMagFilter {
	/**
	 * Use the color of the nearest pixel.
	 */
	NEAREST_NEIGHBOR,
	/**
	 * Linearly interpolate between neigboring pixels.
	 */
	LINEAR
}
