package urender.engine.shader;

import org.joml.Matrix4f;
import urender.api.UObjHandle;
import urender.engine.UGfxRenderer;

public class UUniformMatrix4 extends UUniform<Matrix4f> {

	public UUniformMatrix4(String name) {
		this(name, new Matrix4f());
	}

	public UUniformMatrix4(String name, Matrix4f... value) {
		super(name, value);
	}

	@Override
	public void setData(UObjHandle loc, UGfxRenderer rnd) {
		if (value.length > 1) {
			rnd.getCore().uniformMat4v(loc, value);
		}
		else {
			rnd.getCore().uniformMat4(loc, value[0]);
		}
	}

	@Override
	public UUniformType getUniformType() {
		return UUniformType.MATRIX4;
	}
}
