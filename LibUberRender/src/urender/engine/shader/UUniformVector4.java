package urender.engine.shader;

import org.joml.Vector4f;
import urender.api.UObjHandle;
import urender.api.backend.RenderingBackend;

public class UUniformVector4 extends UUniform<Vector4f> {

	public UUniformVector4(String name) {
		this(name, new Vector4f());
	}

	public UUniformVector4(String name, Vector4f... value) {
		super(name, value);
	}

	@Override
	protected void setDataImpl(UObjHandle loc, RenderingBackend rnd) {
		if (value.length > 1) {
			rnd.uniformVec4v(loc, value);
		} else {
			rnd.uniformVec4(loc, value[0]);
		}
	}

	@Override
	public UUniformType getUniformType() {
		return UUniformType.VEC4;
	}
}
