package urender.api;

public enum UDataType {
	FLOAT64(Double.BYTES),
	FLOAT32(Float.BYTES),
	FLOAT16(Float.BYTES >> 1),
	INT32(Integer.BYTES),
	INT16(Short.BYTES),
	INT8(Byte.BYTES);
	
	public final int sizeof;
	
	private UDataType(int sizeof) {
		this.sizeof = sizeof;
	}
}
