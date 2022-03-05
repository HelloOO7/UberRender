package urender.g3dio.ugfx;

/**
 * Class for tracking UGfxResource version history.
 */
public class UGfxFormatRevisions {

	/**
	 * Current version of the UGfxResource writer.
	 */
	public static final int CURRENT = 4;

	/**
	 * Initial version.
	 */
	public static final int FOUNDATION = 1;
	/**
	 * Support for material shading layers and priorities.
	 */
	public static final int MATERIAL_DRAW_LAYERS = 2;
	/**
	 * Support for texture channel swizzling.
	 */
	public static final int TEXTURE_SWIZZLE_MASKS = 3;
	/**
	 * Update for new unsigned datatype API.
	 */
	public static final int SEPARATE_UNSIGNED_FORMATS = 4;
}
