package urender.engine;

import org.joml.Vector3f;

public class UBoundingBox {

	public Vector3f min = new Vector3f(Float.MAX_VALUE);
	public Vector3f max = new Vector3f(-Float.MAX_VALUE);

	/**
	 * Resets the bounding box to maximum/minimum data type values.
	 */
	public void reset() {
		min.set(Float.MAX_VALUE);
		max.set(Float.MIN_VALUE);
	}

	/**
	 * Updates the bounding box from a point.
	 *
	 * @param vec
	 */
	public void update(Vector3f vec) {
		min.min(vec);
		max.max(vec);
	}

	/**
	 * Checks if a point lies within the axis aligned bounding box.
	 *
	 * @param point Coordinates of the point.
	 * @return True if the points is inside the bounding box.
	 */
	public boolean contains(Vector3f point) {
		return point.x >= min.x && point.z >= min.z && point.x <= max.x && point.z <= max.z && point.y >= min.y && point.y <= max.y;
	}

	/**
	 * Sets the bounding box from another.
	 *
	 * @param aabb
	 */
	public void set(UBoundingBox aabb) {
		min.set(aabb.min);
		max.set(aabb.max);
	}

	/**
	 * Updates the bounding box from a child bounding box.
	 *
	 * @param aabb
	 */
	public void minmax(UBoundingBox aabb) {
		min.min(aabb.min);
		max.max(aabb.max);
	}

	/**
	 * Grows the bounding box.
	 *
	 * @param inc Amount to grow by on all axes.
	 */
	public void grow(float inc) {
		grow(inc, inc, inc);
	}

	/**
	 * Grows the bounding box.
	 *
	 * @param incX Amount to grow by on the X axis.
	 * @param incY Amount to grow by on the Y axis.
	 * @param incZ Amount to grow by on the Y axis.
	 */
	public void grow(float incX, float incY, float incZ) {
		min.sub(incX, incY, incZ);
		max.add(incX, incY, incZ);
	}

	/**
	 * Translates the bounding box.
	 *
	 * @param x Amount to translate along the X axis.
	 * @param y Amount to translate along the Y axis.
	 * @param z Amount to translate along the Z axis.
	 */
	public void add(float x, float y, float z) {
		min.add(x, y, z);
		max.add(x, y, z);
	}

	/**
	 * Divides the bounding box.
	 *
	 * @param value Factor to divide along all axes.
	 */
	public void div(float value) {
		min.div(value);
		max.div(value);
	}

	/**
	 * Divides the bounding box.
	 *
	 * @param x Factor to divide along the X axis.
	 * @param y Factor to divide along the Y axis.
	 * @param z Factor to divide along the Z axis.
	 */
	public void div(float x, float y, float z) {
		min.div(x, y, z);
		max.div(x, y, z);
	}

	/**
	 * Calculates the dimensions of the bounding box.
	 *
	 * @param dest
	 * @return
	 */
	public Vector3f getDimensions(Vector3f dest) {
		max.sub(min, dest);
		return dest;
	}

	/**
	 * Calculates the center of the bounding box.
	 *
	 * @param center
	 * @return
	 */
	public Vector3f getCenter(Vector3f center) {
		center.set(min);
		center.add(max);
		center.mul(0.5f);
		return center;
	}

	/**
	 * Checks if the axis aligned bounding box intersects another.
	 *
	 * @param b The bounding box to check against.
	 * @return
	 */
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
