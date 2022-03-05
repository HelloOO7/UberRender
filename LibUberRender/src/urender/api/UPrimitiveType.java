package urender.api;

/**
 * Primitive rendering mode of a vertex buffer.
 */
public enum UPrimitiveType {
	/**
	 * Render each 3 vertices as a triangle face.
	 */
	TRIS,
	/**
	 * Render each 2 vertices as a line.
	 */
	LINES,
	/**
	 * Draw a point at the locations of each vertex.
	 */
	POINTS
}
