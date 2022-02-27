package urender.engine.shader;

import urender.api.UObjHandle;
import urender.api.backend.RenderingBackend;

public class UUniformInt extends UUniform<Integer> {

	public UUniformInt(String name) {
		this(name, 0);
	}

	public UUniformInt(String name, boolean value) {
		this(name, value ? 1 : 0);
	}

	public UUniformInt(String name, int... value) {
		super(name, primitiveToBoxed(value));
	}

	private static Integer[] primitiveToBoxed(int[] arr) {
		Integer[] boxed = new Integer[arr.length];
		for (int i = 0; i < arr.length; i++) {
			boxed[i] = arr[i];
		}
		return boxed;
	}

	@Override
	protected void setDataImpl(UObjHandle loc, RenderingBackend rnd) {
		if (value.length > 1) {
			int[] ints = new int[value.length];
			for (int i = 0; i < value.length; i++) {
				ints[i] = value[i];
			}
			rnd.uniformIntv(loc, ints);
		} else {
			rnd.uniformInt(loc, value[0]);
		}
	}

	@Override
	public UUniformType getUniformType() {
		return UUniformType.INT;
	}
}
