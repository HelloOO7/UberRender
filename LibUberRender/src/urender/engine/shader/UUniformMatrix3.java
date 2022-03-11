package urender.engine.shader;

import org.joml.Matrix3f;
import urender.api.UObjHandle;
import urender.api.backend.RenderingBackend;

/**
 * 3x3 32-bit floating point transform matrix.
 */
public class UUniformMatrix3 extends UUniform<Matrix3f> {

	public UUniformMatrix3(String name) {
		this(name, new Matrix3f());
	}

	public UUniformMatrix3(String name, Matrix3f... value) {
		super(name, value);
	}

	@Override
	protected void setDataImpl(UObjHandle loc, RenderingBackend rnd) {
		if (value.length > 1) {
			rnd.uniformMat3v(loc, value);
		} else {
			rnd.uniformMat3(loc, value[0]);
		}
	}

	@Override
	public UUniformType getUniformType() {
		return UUniformType.MATRIX3;
	}
}
