package urender.engine.shader;

import org.joml.Vector2f;
import urender.api.UObjHandle;
import urender.engine.UGfxRenderer;

public class UUniformVector2 extends UUniform<Vector2f> {

	public UUniformVector2(String name) {
		this(name, new Vector2f());
	}

	public UUniformVector2(String name, Vector2f... value) {
		super(name, value);
	}

	@Override
	public void setData(UObjHandle loc, UGfxRenderer rnd) {
		if (value.length > 1) {
			rnd.getCore().uniformVec2v(loc, value);
		} else {
			rnd.getCore().uniformVec2(loc, value[0]);
		}
	}

	@Override
	public UUniformType getUniformType() {
		return UUniformType.VEC2;
	}
}
