package urender.engine;

import urender.api.UObjHandle;
import urender.api.UTextureFormat;
import urender.api.UTextureType;
import urender.api.backend.RenderingBackend;

public abstract class UTexture extends UGfxEngineObject {

	protected final UObjHandle __handle = new UObjHandle();

	protected int width;
	protected int height;

	/**
	 * Raw data pixel format.
	 */
	public final UTextureFormat format;

	/**
	 * Texture channel swizzle mask.
	 */
	public final UTextureSwizzleMask swizzleMask;

	/**
	 * Readies the texture for use in samplers.
	 *
	 * @param rnd Rendering backend core.
	 */
	public abstract void setup(RenderingBackend rnd);

	/**
	 * Gets the non-abstract type of the texture.
	 *
	 * @return A UTextureType constant whose value guarantees safe type casting.
	 */
	public abstract UTextureType getTextureType();

	protected UTexture(String name, int width, int height, UTextureFormat format, UTextureSwizzleMask swizzleMask) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.format = format;
		this.swizzleMask = swizzleMask;
	}

	protected UTexture(String name, int width, int height, UTextureFormat format) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.format = format;
		this.swizzleMask = new UTextureSwizzleMask();
	}

	/**
	 * Gets the width of the texture image.
	 *
	 * @return
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets the height of the texture image.
	 *
	 * @return
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Changes the texture's local name.
	 *
	 * @param newName
	 */
	public void renameTo(String newName) {
		this.name = newName;
	}

	@Override
	public UGfxEngineObjectType getType() {
		return UGfxEngineObjectType.TEXTURE;
	}
}
