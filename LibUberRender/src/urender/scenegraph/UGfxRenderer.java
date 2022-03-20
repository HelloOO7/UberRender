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

	/**
	 * Gets the framebuffer to be used for deferred rendering. This framebuffers should contain the G-Buffer's
	 * render targets.
	 *
	 * @return
	 */
	protected abstract UFramebuffer getGBufferFB();

	/**
	 * Gets the framebuffer to be used for forward rendering. This framebuffer is also used for blitting the
	 * composed G-Buffer.
	 *
	 * @return
	 */
	protected abstract UFramebuffer getForwardFB();

	/**
	 * Gets the class for handling the lighting model.
	 *
	 * @return
	 */
	protected abstract ULightAdapter getLightAdapter();

	/**
	 * Gets an arbitrary list of system uniforms to be used in all rendering contexts globally.
	 *
	 * @return
	 */
	protected abstract UUniformList getSystemUniforms();

	/**
	 * Implements a scene draw loop.
	 *
	 * @param scene The scene to draw.
	 */
	public abstract void drawScene(UScene scene);

	/**
	 * Blits the contents of the forward framebuffer to the screen framebuffer.
	 */
	public abstract void blitScreen();

	/**
	 * Deletes all framebuffers and render targets and terminates the renderer.
	 */
	public void shutdown() {
		getForwardFB().deleteRenderTargets(backend);
		getGBufferFB().deleteRenderTargets(backend);
		getForwardFB().delete(backend);
		getGBufferFB().delete(backend);
	}

	/**
	 * Sets up the draw state for a scene to be drawn.
	 *
	 * @param scene The scene that is about to be drawn.
	 */
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

	/**
	 * Gets a list of UTexture objects that represents this renderer's render targets.
	 *
	 * @return
	 */
	public List<UTexture> getRenderTextures() {
		List<UTexture> l = new ArrayList<>();
		l.addAll(getGBufferFB().getRenderTargets());
		l.addAll(getForwardFB().getRenderTargets());
		return l;
	}

	/**
	 * Checks against the currently set shading method.
	 *
	 * @param method The shading method to check.
	 * @return True if 'method' is the current shading method.
	 */
	public boolean isShadingMethodCurrent(UShadingMethod method) {
		return nowShadingMethod == null || method == nowShadingMethod;
	}

	/**
	 * Gets the renderer's draw state.
	 *
	 * @return
	 */
	public UDrawState getDrawState() {
		return drawState;
	}

	/**
	 * Enables alpha blending for all subsequent objects.
	 */
	public void enableAlphaBlend() {
		backend.renderStateBlendSet(true, UBlendEquation.ADD, UBlendFunction.SRC_ALPHA, UBlendFunction.ONE_MINUS_SRC_ALPHA);
	}

	/**
	 * Disables alpha blending for all subsequent objects.
	 */
	public void disableAlphaBlend() {
		backend.renderStateBlendSet(true, UBlendEquation.ADD, UBlendFunction.ONE, UBlendFunction.ZERO);
	}

	/**
	 * Changes the shading method used by the renderer and sets up framebuffers accordingly.
	 *
	 * @param method New shading method to set.
	 */
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

	/**
	 * Sets the resolution of all framebuffers and render targets to a fixed dimension.
	 *
	 * @param width Horizontal resolution.
	 * @param height Vertical resolution.
	 */
	public void setAllFramebufferResolution(int width, int height) {
		getGBufferFB().setAllRenderTargetResolution(width, height);
		getForwardFB().setAllRenderTargetResolution(width, height);
	}

	/**
	 * Binds the screen framebuffer.
	 */
	public void setScreenFramebuffer() {
		backend.framebufferResetScreen();
	}

	/**
	 * Finds a render target texture by name.
	 *
	 * @param name Name of the render target.
	 * @return A matching URenderTarget, or null if none was found.
	 */
	public URenderTarget findRenderTarget(String name) {
		URenderTarget rt = getGBufferFB().findRenderTarget(name);
		if (rt == null) {
			rt = getForwardFB().findRenderTarget(name);
		}
		return rt;
	}

	/**
	 * Gets the underlying rendering backend.
	 *
	 * @return
	 */
	public RenderingBackend getCore() {
		return backend;
	}
}
