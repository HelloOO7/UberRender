
package urender.common.math;

import org.joml.Matrix4f;

public class Matrix4Unit extends MathUnit<Matrix4f> {

	public Matrix4Unit(int cacheSize) {
		super(cacheSize);
	}

	@Override
	protected Matrix4f createMember() {
		return new Matrix4f();
	}

}
