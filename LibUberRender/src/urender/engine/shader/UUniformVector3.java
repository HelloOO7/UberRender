package urender.engine.shader;

import org.joml.Vector3f;
import urender.api.UObjHandle;
import urender.api.backend.RenderingBackend;

public class UUniformVector3 extends UUniform<Vector3f> {

	public UUniformVector3(String name) {
		this(name, new Vector3f());
	}

	public UUniformVector3(String name, Vector3f... value) {
		super(name, value);
	}

	@Override
	protected void setDataImpl(UObjHandle loc, RenderingBackend rnd) {
		if (value.length > 1) {
			rnd.uniformVec3v(loc, value);
		} else {
			rnd.uniformVec3(loc, value[0]);
		}
	}

	@Override
	public UUniformType getUniformType() {
		return UUniformType.VEC3;
	}
}
