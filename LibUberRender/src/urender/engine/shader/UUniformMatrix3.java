package urender.engine.shader;

import org.joml.Matrix3f;
import urender.api.UObjHandle;
import urender.engine.UGfxRenderer;

public class UUniformMatrix3 extends UUniform<Matrix3f> {

	public UUniformMatrix3(String name) {
		this(name, new Matrix3f());
	}

	public UUniformMatrix3(String name, Matrix3f... value) {
		super(name, value);
	}

	@Override
	public void setData(UObjHandle loc, UGfxRenderer rnd) {
		if (value.length > 1) {
			rnd.getCore().uniformMat3v(loc, value);
		} else {
			rnd.getCore().uniformMat3(loc, value[0]);
		}
	}

	@Override
	public UUniformType getUniformType() {
		return UUniformType.MATRIX3;
	}
}
