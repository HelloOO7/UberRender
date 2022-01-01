package urender.scenegraph;

public class UTransformInheritance {
	public static final int INHERIT_SCALE = (1 << 0);
	public static final int INHERIT_ROTATION = (1 << 1);
	public static final int INHERIT_TRANSLATION = (1 << 2);
	
	public static final int IGNORE_CAMERA = (1 << 3);
	
	public static final int INHERIT_NONE = 0;
	public static final int INHERIT_ALL = INHERIT_SCALE | INHERIT_ROTATION | INHERIT_TRANSLATION;
}
