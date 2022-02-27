package urender.engine;

import urender.api.UTextureSwizzleChannel;
import urender.api.backend.RenderingBackend;

public class UTextureSwizzleMask {
	public final UTextureSwizzleChannel r;
	public final UTextureSwizzleChannel g;
	public final UTextureSwizzleChannel b;
	public final UTextureSwizzleChannel a;
	
	public UTextureSwizzleMask(UTextureSwizzleChannel r, UTextureSwizzleChannel g, UTextureSwizzleChannel b, UTextureSwizzleChannel a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public UTextureSwizzleMask() {
		this(UTextureSwizzleChannel.R, UTextureSwizzleChannel.G, UTextureSwizzleChannel.B, UTextureSwizzleChannel.A);
	}
	
	public void setup(RenderingBackend rnd, UTexture texture) {
		rnd.texSwizzleMask(texture.__handle, texture.getTextureType(), r, g, b, a);
	}
	
	@Override
	public String toString() {
		return r + "/" + g + "/" + b + "/" + a;
	}
}
