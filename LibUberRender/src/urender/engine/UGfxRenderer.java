package urender.engine;

import urender.api.backend.RenderingBackend;

public class UGfxRenderer {
	private final RenderingBackend backend;
	
	public UGfxRenderer(RenderingBackend backend) {
		this.backend = backend;
	}
	
	public RenderingBackend getCore() {
		return backend;
	}
}
