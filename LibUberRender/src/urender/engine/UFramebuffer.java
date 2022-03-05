package urender.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import urender.api.UFramebufferAttachment;
import urender.api.UObjHandle;
import urender.api.backend.RenderingBackend;

/**
 * GPU framebuffer container class.
 */
public class UFramebuffer {

	private final List<URenderTarget> renderTargets = new ArrayList<>();

	private final UObjHandle __handle = new UObjHandle();

	/**
	 * Creates a framebuffer with a set of render targets.
	 *
	 * @param renderTargets
	 */
	public UFramebuffer(URenderTarget... renderTargets) {
		for (URenderTarget rt : renderTargets) {
			this.renderTargets.add(rt);
		}
	}

	/**
	 * Gets the list of render targets used in this framebuffer. All operations on the list will be reflected
	 * in the framebuffer.
	 *
	 * @return
	 */
	public List<? extends UTexture> getRenderTargets() {
		return renderTargets;
	}

	/**
	 * Finds a render target by name.
	 *
	 * @param name Local name of the render target to search for.
	 * @return The matched render target, or null if none was found.
	 */
	public URenderTarget findRenderTarget(String name) {
		return UGfxObject.find(renderTargets, name);
	}

	/**
	 * Sets the resolution of all render targets.
	 *
	 * @param width New width of the render targets.
	 * @param height New height of the render targets.
	 */
	public void setAllRenderTargetResolution(int width, int height) {
		for (URenderTarget rt : renderTargets) {
			rt.setResolution(width, height);
		}
	}

	private List<URenderTarget> lastBoundRenderTargets = new ArrayList<>();
	private UObjHandle[] lastDrawBuffers = null;

	/**
	 * Readies the framebuffer for being drawn into.
	 *
	 * @param rnd Rendering backend core.
	 */
	public void setup(RenderingBackend rnd) {
		if (!__handle.isInitialized(rnd)) {
			rnd.framebufferInit(__handle);
		}
		rnd.framebufferBind(__handle);

		int maxDrawBuffer = 0;

		for (int i = 0; i < renderTargets.size(); i++) {
			URenderTarget rt = renderTargets.get(i);
			rt.setup(rnd);

			if (!lastBoundRenderTargets.contains(rt)) {
				rt.bind(__handle, rnd);
			}

			maxDrawBuffer = Math.max(maxDrawBuffer, rt.getDrawBufferIndex() + 1);
		}
		lastBoundRenderTargets.clear();
		lastBoundRenderTargets.addAll(renderTargets);

		UObjHandle[] drawBuffers = new UObjHandle[maxDrawBuffer];
		for (URenderTarget rt : renderTargets) {
			if (rt.getAttachment() == UFramebufferAttachment.COLOR) {
				drawBuffers[rt.getDrawBufferIndex()] = rt.__drawBufferHandle;
			}
		}

		if (!Arrays.equals(lastDrawBuffers, drawBuffers)) {
			rnd.drawBuffersDefine(__handle, drawBuffers);
			lastDrawBuffers = drawBuffers;
		}
	}
}
