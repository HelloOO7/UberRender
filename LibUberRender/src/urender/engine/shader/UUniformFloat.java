package urender.engine.shader;

import urender.api.UObjHandle;
import urender.engine.UGfxRenderer;

public class UUniformFloat extends UUniform<Float> {

	private float[] floats;
	
	public UUniformFloat(String name) {
		this(name, 0f);
	}

	public UUniformFloat(String name, float... value) {
		super(name, (Float[])null);
		floats = value;
	}
	
	@Override
	protected Object array() {
		return floats;
	}

	@Override
	public void setData(UObjHandle loc, UGfxRenderer rnd) {
		if (floats.length > 1) {
			rnd.getCore().uniformFloatv(loc, floats);
		} else {
			rnd.getCore().uniformFloat(loc, floats[0]);
		}
	}

	@Override
	public UUniformType getUniformType() {
		return UUniformType.FLOAT;
	}
}
