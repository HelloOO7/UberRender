package urender.scenegraph;

public abstract class ULight extends UGfxScenegraphObject {

	@Override
	public UGfxScenegraphObjectType getType() {
		return UGfxScenegraphObjectType.LIGHT;
	}

	public abstract ULightType getLightType();
}
