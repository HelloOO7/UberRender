
package urender.common.math;

import org.joml.Vector3f;

public class Vector3Unit extends MathUnit<Vector3f> {

	public Vector3Unit(int cacheSize) {
		super(cacheSize);
	}

	@Override
	protected Vector3f createMember() {
		return new Vector3f();
	}

}
