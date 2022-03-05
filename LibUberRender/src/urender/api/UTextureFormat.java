package urender.api;

/**
 * High-level texture image data format types.
 */
public enum UTextureFormat {
	/**
	 * Color/alpha texture.
	 *
	 * BPP: 32 BPC: 24
	 */
	RGBA8,
	/**
	 * Color texture.
	 *
	 * BPP:24 BPC:8
	 */
	RGB8,
	/**
	 * Two-channel texture.
	 *
	 * BPP: 16 BPC: 8
	 */
	RG8,
	/**
	 * Single-channel texture.
	 *
	 * BPP: 8 BPC: 8
	 */
	R8,

	/**
	 * Single-channel half-precision floating point texture.
	 *
	 * BPP: 16 BPC: 16
	 */
	R16F,
	/**
	 * Single-channel unsigned integer texture.
	 *
	 * BPP: 16 BPC: 16
	 */
	R16UI,

	/**
	 * Single-channel floating point texture.
	 *
	 * BPP: 32 BPC: 32
	 */
	FLOAT32,
	/**
	 * Half-precision floating point color/alpha texture.
	 *
	 * BPP: 64 BPC: 16
	 */
	RGBA16F,

	/**
	 * Depth texture.
	 *
	 * BPP: 24 BPC: 24
	 */
	DEPTH_COMPONENT24,
	/**
	 * Stencil texture.
	 *
	 * BPP: 8 BPC: 8
	 */
	STENCIL_INDEX8,
	/**
	 * Depth/stencil texture.
	 *
	 * BPP: 32 BPC: 24 depth bits, 8 stencil bits
	 */
	DEPTH24_STENCIL8
}
