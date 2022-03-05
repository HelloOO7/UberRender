package urender.api;

/**
 * Mode of discarding primitives based on their face normal.
 *
 * The face normal is usually calculated by the GPU and requires vertices to be in counterclockwise order when
 * "looked at" from the unculled side to work properly.
 */
public enum UFaceCulling {
	/**
	 * Both the front and back faces of a primitive should be rendered.
	 */
	NONE,
	/**
	 * The front faces of primitives should be discarded.
	 */
	FRONT,
	/**
	 * The baces faces of primitives should be discarded.
	 */
	BACK,
	/**
	 * All faces of primitives should be discarded. This does not affect primitives that do not have faces,
	 * i.e. lines and points.
	 */
	FRONT_AND_BACK
}
