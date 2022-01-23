package urender.engine.shader;

import urender.api.UObjHandle;
import urender.engine.UGfxRenderer;

public class UUniformInt extends UUniform<Integer> {

	private int[] ints;

	public UUniformInt(String name) {
		this(name, 0);
	}

	public UUniformInt(String name, int... value) {
		super(name, (Integer[]) null);
		ints = value;
	}

	@Override
	protected Object array() {
		return ints;
	}

	@Override
	public void setData(UObjHandle loc, UGfxRenderer rnd) {
		if (value.length > 1) {
			rnd.getCore().uniformIntv(loc, ints);
		} else {
			rnd.getCore().uniformInt(loc, ints[0]);
		}
	}

	@Override
	public UUniformType getUniformType() {
		return UUniformType.INT;
	}
}
