package urender.api;

/**
 * Defines filtering/interpolation mode when downscaling texels.
 */
public enum UTextureMinFilter {
	/**
	 * Use the nearest pixel's color.
	 */
	NEAREST_NEIGHBOR,
	/**
	 * Use the nearest pixel's color of the nearest mipmap.
	 */
	NEAREST_MIPMAP_NEAREST,
	/**
	 * Use the interpolated color of the nearest pixels on neighboring mipmaps.
	 */
	NEAREST_MIPMAP_LINEAR,
	/**
	 * Linearly interpolate neighboring pixels.
	 */
	LINEAR,
	/**
	 * Linearly interpolate neighboring pixels of the nearest mipmap.
	 */
	LINEAR_MIPMAP_NEAREST,
	/**
	 * Use the interpolated color of linearly interpolated pixels on neighboring mipmaps.
	 */
	LINEAR_MIPMAP_LINEAR
}
