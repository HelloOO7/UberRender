package urender.api;

/**
 * Generic raw data type.
 */
public enum UDataType {
	/**
	 * 64-bit floating point.
	 *
	 * C type: double
	 */
	FLOAT64(Double.BYTES),
	/**
	 * 32-bit floating point.
	 *
	 * C type: float
	 */
	FLOAT32(Float.BYTES),
	/**
	 * 16-bit floating point.
	 *
	 * C type: half/binary16
	 */
	FLOAT16(Float.BYTES >> 1),
	/**
	 * 32-bit integer (signed).
	 *
	 * C type: int32_t
	 */
	INT32(Integer.BYTES),
	/**
	 * 16-bit integer (signed).
	 *
	 * C type: int16_t
	 */
	INT16(Short.BYTES),
	/**
	 * 8-bit integer (signed).
	 *
	 * C type: int8_t
	 */
	INT8(Byte.BYTES),
	/**
	 * 32-bit integer (unsigned).
	 *
	 * C type: uint32_t
	 */
	UINT32(Integer.BYTES),
	/**
	 * 16-bit integer (unsigned).
	 *
	 * C type: uint16_t
	 */
	UINT16(Short.BYTES),
	/**
	 * 8-bit integer (unsigned).
	 *
	 * C type: uint8_t
	 */
	UINT8(Byte.BYTES);

	/**
	 * Size of an element of this data type in bytes.
	 */
	public final int sizeof;

	private UDataType(int sizeof) {
		this.sizeof = sizeof;
	}
}
