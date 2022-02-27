package urender.engine;

import org.joml.Vector3f;

public class UBoundingBox {

	public Vector3f min = new Vector3f(Float.MAX_VALUE);
	public Vector3f max = new Vector3f(-Float.MAX_VALUE);

	public void reset() {
		min.set(Float.MAX_VALUE);
		max.set(Float.MIN_VALUE);
	}

	public void update(Vector3f vec) {
		min.min(vec);
		max.max(vec);
	}

	public boolean contains(Vector3f point) {
		return point.x >= min.x && point.z >= min.z && point.x <= max.x && point.z <= max.z && point.y >= min.y && point.y <= max.y;
	}

	public void set(UBoundingBox aabb) {
		min.set(aabb.min);
		max.set(aabb.max);
	}

	public void minmax(UBoundingBox aabb) {
		min.min(aabb.min);
		max.max(aabb.max);
	}

	public void grow(float inc) {
		grow(inc, inc, inc);
	}

	public void grow(float incX, float incY, float incZ) {
		min.sub(incX, incY, incZ);
		max.add(incX, incY, incZ);
	}

	public void add(float x, float y, float z) {
		min.add(x, y, z);
		max.add(x, y, z);
	}

	public void div(float value) {
		min.div(value);
		max.div(value);
	}

	public void div(float x, float y, float z) {
		min.div(x, y, z);
		max.div(x, y, z);
	}

	public Vector3f getDimensions(Vector3f dest) {
		max.sub(min, dest);
		return dest;
	}

	public Vector3f getCenter(Vector3f center) {
		center.set(min);
		center.add(max);
		center.mul(0.5f);
		return center;
	}

	public boolean intersects(UBoundingBox b) {
		return (min.x <= b.max.x && max.x >= b.min.x)
			&& (min.y <= b.max.y && max.x >= b.min.y)
			&& (min.z <= b.max.z && max.z >= b.min.z);
	}

	@Override
	public String toString() {
		return "Min: " + min + " | Max: " + max;
	}
}
