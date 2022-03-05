package urender.api;

/**
 * Defines the mapped value of a texture swizzle.
 */
public enum UTextureSwizzleChannel {
	/**
	 * The swizzle should use the Red channel of the image.
	 */
	R,
	/**
	 * The swizzle should use the Green channel of the image.
	 */
	G,
	/**
	 * The swizzle should use the Blue channel of the image.
	 */
	B,
	/**
	 * The swizzle should use the Alpha channel of the image.
	 */
	A,
	/**
	 * The swizzle should always have a value of 0.0.
	 */
	ZERO,
	/**
	 * The swizzle should always have a value of 1.0.
	 */
	ONE
}
