package urender.api;

public enum UFramebufferAttachment {
	COLOR,
	DEPTH, //warning: Separate Depth/stencil formats tend to be problematic with a lot of GPUs. Use DEPTH_STENCIL where possible
	STENCIL,
	DEPTH_STENCIL
}
