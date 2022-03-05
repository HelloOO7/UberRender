package urender.api;

/**
 * Defines the attachment of a texture or renderbuffer to a framebuffer.
 */
public enum UFramebufferAttachment {
	/**
	 * Color channel attachment.
	 */
	COLOR,
	/**
	 * Depth/Z-buffer attachment.
	 *
	 * Note that separate DEPTH formats tend to be problematic with a lot of GPUs. Use DEPTH_STENCIL
	 * where possible.
	 */
	DEPTH,
	/**
	 * Stencil buffer attachment.
	 * 
	 * Note that separate STENCIL formats tend to be problematic with a lot of GPUs. Use DEPTH_STENCIL
	 * where possible.
	 */
	STENCIL,
	/**
	 * Shared depth and stencil buffer attachment.
	 *
	 * This should be preferred over separate depth/stencil attachments where possible.
	 */
	DEPTH_STENCIL
}
