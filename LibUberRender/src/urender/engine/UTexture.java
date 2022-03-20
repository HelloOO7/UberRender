package urender.engine;

import java.util.ArrayList;
import java.util.List;
import urender.api.UObjHandle;
import urender.api.UTextureFormat;
import urender.api.UTextureType;
import urender.api.backend.RenderingBackend;

/**
 * Texture resource base class.
 */
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

	public void delete(RenderingBackend rnd) {
		if (__handle.isValid(rnd)) {
			rnd.texDelete(__handle);
		}
	}
	
	public static void deleteAll(RenderingBackend rnd, Iterable<UTexture> textures) {
		List<UObjHandle> handles = new ArrayList<>();
		for (UTexture tex : textures) {
			if (tex.__handle.isValid(rnd)) {
				handles.add(tex.__handle);
			}
		}
		rnd.texDelete(handles.toArray(new UObjHandle[handles.size()]));
	}

	@Override
	public UGfxEngineObjectType getType() {
		return UGfxEngineObjectType.TEXTURE;
	}
}
