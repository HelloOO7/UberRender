package urender.api;

/**
 * Function for evaluating render state tests, f.e. the depth or alpha test.
 */
public enum UTestFunction {
	/**
	 * The test should never pass.
	 */
	NEVER,
	/**
	 * Pass if the new value is less than the reference value.
	 */
	LESS,
	/**
	 * Pass if the new value is equal to the reference value.
	 */
	EQUAL,
	/**
	 * Pass if the new value is less than or equal to the reference value.
	 */
	LEQUAL,
	/**
	 * Pass if the new value is greater than the reference value.
	 */
	GREATER,
	/**
	 * Pass if the new value is not equal to the reference value.
	 */
	NOTEQUAL,
	/**
	 * Pass if the new value is greater than or equal to the reference value.
	 */
	GEQUAL,
	/**
	 * Pass regardless of the value.
	 */
	ALWAYS
}
