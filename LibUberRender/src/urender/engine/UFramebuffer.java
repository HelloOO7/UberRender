package urender.engine;

import java.util.ArrayList;
import java.util.List;
import urender.api.UFramebufferAttachment;
import urender.api.UObjHandle;
import urender.api.backend.RenderingBackend;

public class UFramebuffer {
	private final List<URenderTarget> renderTargets = new ArrayList<>();
	
	private final UObjHandle __handle = new UObjHandle();
	
	public UFramebuffer(URenderTarget... renderTargets) {
		for (URenderTarget rt : renderTargets) {
			this.renderTargets.add(rt);
		}
	}
	
	public URenderTarget findRenderTarget(String name) {
		return UGfxObject.find(renderTargets, name);
	}
	
	public void setAllRenderTargetResolution(int width, int height) {
		for (URenderTarget rt : renderTargets) {
			rt.setResolution(width, height);
		}
	}
	
	public void setup(UGfxRenderer rnd) {
		RenderingBackend core = rnd.getCore();
		if (!__handle.isInitialized(core)) {
			core.framebufferInit(__handle);
		}
		core.framebufferBind(__handle);
		
		int maxDrawBuffer = 0;
		
		for (int i = 0; i < renderTargets.size(); i++) {
			URenderTarget rt = renderTargets.get(i);
			rt.setup(rnd);
			rt.bind(__handle, rnd);
			maxDrawBuffer = Math.max(maxDrawBuffer, rt.getDrawBufferIndex() + 1);
		}
		
		UObjHandle[] drawBuffers = new UObjHandle[maxDrawBuffer];
		for (URenderTarget rt : renderTargets) {
			if (rt.getAttachment() == UFramebufferAttachment.COLOR) {
				drawBuffers[rt.getDrawBufferIndex()] = rt.__drawBufferHandle;
			}
		}
		
		core.drawBuffersDefine(__handle, drawBuffers);
	}
}
