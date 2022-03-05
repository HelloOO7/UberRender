package urender.scenegraph;

/**
 * Flags for inheriting parent node transform.
 */
public class UTransformInheritance {

	/**
	 * Inherit parent scaling.
	 */
	public static final int INHERIT_SCALE = (1 << 0);
	/**
	 * Inherit parent rotation.
	 */
	public static final int INHERIT_ROTATION = (1 << 1);
	/**
	 * Inherit parent translation.
	 */
	public static final int INHERIT_TRANSLATION = (1 << 2);

	/**
	 * Ignore camera transform.
	 */
	public static final int IGNORE_CAMERA = (1 << 3);

	/**
	 * Inherit nothing besides camera transform.
	 */
	public static final int INHERIT_NONE = 0;
	/**
	 * Inherit everything.
	 */
	public static final int INHERIT_ALL = INHERIT_SCALE | INHERIT_ROTATION | INHERIT_TRANSLATION;
}
