package urender.scenegraph;

/**
 * Light source base class.
 */
public abstract class ULight extends UGfxScenegraphObject {

	@Override
	public UGfxScenegraphObjectType getType() {
		return UGfxScenegraphObjectType.LIGHT;
	}

	/**
	 * Gets the non-abstract type of the light.
	 *
	 * @return A ULightType constant whose value guarantees safe type casting.
	 */
	public abstract ULightType getLightType();
}
