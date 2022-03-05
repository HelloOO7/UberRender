package urender.scenegraph;

import java.util.ArrayList;
import java.util.List;
import urender.api.UBlendEquation;
import urender.api.UBlendFunction;
import urender.api.backend.RenderingBackend;
import urender.engine.UShadingMethod;
import urender.engine.UFramebuffer;
import urender.engine.URenderTarget;
import urender.engine.UTexture;
import urender.engine.shader.UUniformList;

/**
 * URender SceneGraph rendering engine base class.
 */
public abstract class UGfxRenderer {

	protected final RenderingBackend backend;

	private UShadingMethod nowShadingMethod = null;

	private UDrawState drawState = new UDrawState();

	public UGfxRenderer(RenderingBackend backend) {
		this.backend = backend;
	}
	
	protected abstract UFramebuffer getGBufferFB();
	protected abstract UFramebuffer getForwardFB();
	
	protected abstract ULightAdapter getLightAdapter();
	protected abstract UUniformList getSystemUniforms();
	
	public abstract void drawScene(UScene scene);
	public abstract void blitScreen();

	protected void beginScene(UScene scene) {
		ULightAdapter lightAdapter = getLightAdapter();
		if (lightAdapter != null) {
			lightAdapter.setLights(scene.lights);
		}
		drawState = new UDrawState();
		drawState.commonUniforms.addAll(getSystemUniforms());
		drawState.commonUniforms.addAll(scene.getSceneUniforms());
		if (lightAdapter != null) {
			drawState.commonUniforms.addAll(lightAdapter.getLightUniforms());
		}
	}

	public List<UTexture> getRenderTextures() {
		List<UTexture> l = new ArrayList<>();
		l.addAll(getGBufferFB().getRenderTargets());
		l.addAll(getForwardFB().getRenderTargets());
		return l;
	}

	public boolean isShadingMethodCurrent(UShadingMethod method) {
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

	public void changeShadingMethod(UShadingMethod method) {
		if (method != nowShadingMethod) {
			if (method != null) {
				switch (method) {
					case DEFERRED:
						getGBufferFB().setup(backend);
						break;
					case FORWARD:
						getForwardFB().setup(backend);
						break;
				}
			} else {
				backend.framebufferResetScreen();
			}
			nowShadingMethod = method;
		}
	}

	public void setAllFramebufferResolution(int width, int height) {
		getGBufferFB().setAllRenderTargetResolution(width, height);
		getForwardFB().setAllRenderTargetResolution(width, height);
	}

	public void setScreenFramebuffer() {
		backend.framebufferResetScreen();
	}

	public URenderTarget findRenderTarget(String name) {
		URenderTarget rt = getGBufferFB().findRenderTarget(name);
		if (rt == null) {
			rt = getForwardFB().findRenderTarget(name);
		}
		return rt;
	}

	public RenderingBackend getCore() {
		return backend;
	}
}
