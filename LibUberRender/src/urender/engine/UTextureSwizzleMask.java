package urender.engine;

import urender.api.UTextureSwizzleChannel;
import urender.api.backend.RenderingBackend;

/**
 * Texture channel swizzle mask container.
 */
public class UTextureSwizzleMask {

	public final UTextureSwizzleChannel r;
	public final UTextureSwizzleChannel g;
	public final UTextureSwizzleChannel b;
	public final UTextureSwizzleChannel a;

	/**
	 * Creates a texture channel swizzle mask.
	 *
	 * @param r Red channel swizzle.
	 * @param g Green channel swizzle.
	 * @param b Blue channel swizzle.
	 * @param a Alpha channel swizzle.
	 */
	public UTextureSwizzleMask(UTextureSwizzleChannel r, UTextureSwizzleChannel g, UTextureSwizzleChannel b, UTextureSwizzleChannel a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	/**
	 * Creates a texture channel swizzle mask with the RGBA channels mapped to their own values.
	 */
	public UTextureSwizzleMask() {
		this(UTextureSwizzleChannel.R, UTextureSwizzleChannel.G, UTextureSwizzleChannel.B, UTextureSwizzleChannel.A);
	}

	/**
	 * Sets the swizzle mask to a texture.
	 *
	 * @param rnd Rendering backend core.
	 * @param texture The texture to swizzle up.s
	 */
	public void setup(RenderingBackend rnd, UTexture texture) {
		rnd.texSwizzleMask(texture.__handle, texture.getTextureType(), r, g, b, a);
	}

	@Override
	public String toString() {
		return r + "/" + g + "/" + b + "/" + a;
	}
}
