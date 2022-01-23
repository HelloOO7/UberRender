package urender.engine.shader;

import org.joml.Vector4f;
import urender.api.UObjHandle;
import urender.engine.UGfxRenderer;

public class UUniformVector4 extends UUniform<Vector4f> {

	public UUniformVector4(String name) {
		this(name, new Vector4f());
	}

	public UUniformVector4(String name, Vector4f... value) {
		super(name, value);
	}

	@Override
	public void setData(UObjHandle loc, UGfxRenderer rnd) {
		if (value.length > 1) {
			rnd.getCore().uniformVec4v(loc, value);
		} else {
			rnd.getCore().uniformVec4(loc, value[0]);
		}
	}

	@Override
	public UUniformType getUniformType() {
		return UUniformType.VEC4;
	}
}
