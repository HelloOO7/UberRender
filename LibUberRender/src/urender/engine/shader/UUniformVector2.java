package urender.engine.shader;

import org.joml.Vector2f;
import urender.api.UObjHandle;
import urender.api.backend.RenderingBackend;

/**
 * 2-component 32-bit floating point vector.
 */
public class UUniformVector2 extends UUniform<Vector2f> {

	public UUniformVector2(String name) {
		this(name, new Vector2f());
	}

	public UUniformVector2(String name, Vector2f... value) {
		super(name, value);
	}

	@Override
	protected void setDataImpl(UObjHandle loc, RenderingBackend rnd) {
		if (value.length > 1) {
			rnd.uniformVec2v(loc, value);
		} else {
			rnd.uniformVec2(loc, value[0]);
		}
	}

	@Override
	public UUniformType getUniformType() {
		return UUniformType.VEC2;
	}
}
