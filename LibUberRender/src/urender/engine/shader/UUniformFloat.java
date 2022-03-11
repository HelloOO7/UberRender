package urender.engine.shader;

import urender.api.UObjHandle;
import urender.api.backend.RenderingBackend;

/**
 * 32-bit float uniform value.
 */
public class UUniformFloat extends UUniform<Float> {

	public UUniformFloat(String name) {
		this(name, 0f);
	}

	public UUniformFloat(String name, float... value) {
		super(name, primitiveToBoxed(value));
	}
	
	private static Float[] primitiveToBoxed(float[] arr) {
		Float[] boxed = new Float[arr.length];
		for (int i = 0; i < arr.length; i++) {
			boxed[i] = arr[i];
		}
		return boxed;
	}

	@Override
	protected void setDataImpl(UObjHandle loc, RenderingBackend rnd) {
		if (value.length > 1) {
			float[] f = new float[value.length];
			for (int i = 0; i < value.length; i++) {
				f[i] = value[i];
			}
			rnd.uniformFloatv(loc, f);
		} else {
			rnd.uniformFloat(loc, value[0]);
		}
	}

	@Override
	public UUniformType getUniformType() {
		return UUniformType.FLOAT;
	}
}
