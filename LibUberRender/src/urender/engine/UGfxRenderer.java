package urender.engine;

import urender.api.backend.RenderingBackend;

public class UGfxRenderer {

	private final RenderingBackend backend;

	private UFramebuffer gbufferFramebuffer;
	private UFramebuffer forwardFramebuffer;

	private UMaterialDrawLayer.ShadingMethod nowShadingMethod = null;

	private final UDrawState drawState = new UDrawState();

	public UGfxRenderer(RenderingBackend backend, UFramebuffer gbufferFramebuffer, UFramebuffer forwardFramebuffer) {
		this.backend = backend;
		this.gbufferFramebuffer = gbufferFramebuffer;
		this.forwardFramebuffer = forwardFramebuffer;
	}

	public boolean isShadingMethodCurrent(UMaterialDrawLayer.ShadingMethod method) {
		return nowShadingMethod == null || method == nowShadingMethod;
	}

	public UDrawState getDrawState() {
		return drawState;
	}

	public void changeShadingMethod(UMaterialDrawLayer.ShadingMethod method) {
		if (method != nowShadingMethod) {
			if (method != null) {
				switch (method) {
					case DEFERRED:
						gbufferFramebuffer.setup(this);
						break;
					case FORWARD:
						forwardFramebuffer.setup(this);
						break;
				}
			}
			else {
				backend.framebufferResetScreen();
			}
			nowShadingMethod = method;
		}
	}

	public void setAllFramebufferResolution(int width, int height) {
		gbufferFramebuffer.setAllRenderTargetResolution(width, height);
		forwardFramebuffer.setAllRenderTargetResolution(width, height);
	}

	public void setScreenFramebuffer() {
		backend.framebufferResetScreen();
	}

	public URenderTarget findRenderTarget(String name) {
		URenderTarget rt = gbufferFramebuffer.findRenderTarget(name);
		if (rt == null) {
			rt = forwardFramebuffer.findRenderTarget(name);
		}
		return rt;
	}

	public RenderingBackend getCore() {
		return backend;
	}
}
