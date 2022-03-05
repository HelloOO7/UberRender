package urender.engine.shader;

/**
 * Data type of a shader program uniform.
 */
public enum UUniformType {
	/**
	 * 32-bit integer or boolean.
	 */
	INT,
	/**
	 * 32-bit floating point.
	 */
	FLOAT,
	/**
	 * 2-component 32-bit floating point vector.
	 */
	VEC2,
	/**
	 * 3-component 32-bit floating point vector.
	 */
	VEC3,
	/**
	 * 4-component 32-bit floating point vector.
	 */
	VEC4,
	/**
	 * 3x3 32-bit floating point transform matrix.
	 */
	MATRIX3,
	/**
	 * 4x4 32-bit floating point transform matrix.
	 */
	MATRIX4
}
