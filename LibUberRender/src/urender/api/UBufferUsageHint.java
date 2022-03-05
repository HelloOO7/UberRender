package urender.api;

/**
 * Definition of driver hints that let the GPU perform optimization based on the reupload frequency of a buffer.
 * 
 * This can have many effects defined by the GPU hardware and driver implementation.
 */
public enum UBufferUsageHint {
	/**
	 * The buffer will only be uploaded once and kept in VRAM without changes.
	 */
	STATIC,
	/**
	 * The buffer might be partially modified after being uploaded.
	 */
	DYNAMIC,
	/**
	 * The buffer will be reuploaded with high frequency and used at most a couple of times.
	 */
	STREAM
}
