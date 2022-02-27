package urender.scenegraph;

import java.util.ArrayList;
import java.util.List;
import urender.api.UBlendEquation;
import urender.api.UBlendFunction;
import urender.api.backend.RenderingBackend;
import urender.engine.UFramebuffer;
import urender.engine.UMaterialDrawLayer;
import urender.engine.URenderTarget;
import urender.engine.UTexture;
import urender.engine.shader.UUniformList;

public class UGfxRenderer {

	private final RenderingBackend backend;

	private ULightAdapter lightAdapter;

	private UFramebuffer gbufferFramebuffer;
	private UFramebuffer forwardFramebuffer;

	private UMaterialDrawLayer.ShadingMethod nowShadingMethod = null;

	private UDrawState drawState = new UDrawState();

	public UGfxRenderer(RenderingBackend backend, UFramebuffer gbufferFramebuffer, UFramebuffer forwardFramebuffer) {
		this.backend = backend;
		this.gbufferFramebuffer = gbufferFramebuffer;
		this.forwardFramebuffer = forwardFramebuffer;
	}

	public void beginScene(UScene scene) {
		if (lightAdapter != null) {
			lightAdapter.setLights(scene.lights);
		}
		drawState = new UDrawState();
		drawState.sceneUniformTemp.addAll(scene.getSceneUniforms());
		if (lightAdapter != null) {
			drawState.sceneUniformTemp.addAll(lightAdapter.getLightUniforms());
		}
	}

	public void bindLightAdapter(ULightAdapter lightAdapter) {
		this.lightAdapter = lightAdapter;
	}

	public List<UTexture> getRenderTextures() {
		List<UTexture> l = new ArrayList<>();
		l.addAll(gbufferFramebuffer.getRenderTargets());
		l.addAll(forwardFramebuffer.getRenderTargets());
		return l;
	}

	public boolean isShadingMethodCurrent(UMaterialDrawLayer.ShadingMethod method) {
		return nowShadingMethod == null || method == nowShadingMethod;
	}

	public UDrawState getDrawState() {
		return drawState;
	}

	public void enableAlphaBlend() {
		backend.renderStateBlendSet(true, UBlendEquation.ADD, UBlendFunction.SRC_ALPHA, UBlendFunction.ONE_MINUS_SRC_ALPHA);
	}

	public void disableAlphaBlend() {
		backend.renderStateBlendSet(true, UBlendEquation.ADD, UBlendFunction.ONE, UBlendFunction.ZERO);
	}

	public void changeShadingMethod(UMaterialDrawLayer.ShadingMethod method) {
		if (method != nowShadingMethod) {
			if (method != null) {
				switch (method) {
					case DEFERRED:
						gbufferFramebuffer.setup(backend);
						break;
					case FORWARD:
						forwardFramebuffer.setup(backend);
						break;
				}
			} else {
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
