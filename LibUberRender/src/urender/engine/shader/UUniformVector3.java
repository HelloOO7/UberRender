package urender.engine.shader;

import org.joml.Vector3f;
import urender.api.UObjHandle;
import urender.engine.UGfxRenderer;

public class UUniformVector3 extends UUniform<Vector3f> {

	public UUniformVector3(String name) {
		this(name, new Vector3f());
	}

	public UUniformVector3(String name, Vector3f... value) {
		super(name, value);
	}

	@Override
	public void setData(UObjHandle loc, UGfxRenderer rnd) {
		if (value.length > 1) {
			rnd.getCore().uniformVec3v(loc, value);
		} else {
			rnd.getCore().uniformVec3(loc, value[0]);
		}
	}

	@Override
	public UUniformType getUniformType() {
		return UUniformType.VEC3;
	}
}
