package urender.engine;

import urender.api.backend.RenderingBackend;

public class UGfxRenderer {
	private final RenderingBackend backend;
	
	private UFramebuffer framebuffer;
	private UFramebuffer renderTargetSource;
	
	public UGfxRenderer(RenderingBackend backend) {
		this.backend = backend;
	}
	
	public void setFramebuffer(UFramebuffer fb) {
		this.framebuffer = fb;
		if (fb != null) {
			fb.setup(this);
		}
		else {
			backend.framebufferResetScreen();
		}
	}
	
	public void setRenderSourceFramebuffer(UFramebuffer fb) {
		this.renderTargetSource = fb;
	}
	
	public UFramebuffer getRenderSourceFramebuffer() {
		return renderTargetSource;
	}
	
	public void setScreenFramebuffer() {
		setFramebuffer(null);
	}
	
	public RenderingBackend getCore() {
		return backend;
	}
}
