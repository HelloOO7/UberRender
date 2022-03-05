package urender.engine;

import urender.api.UDataType;

/**
 * Vertex buffer attribute description.
 */
public class UVertexAttribute {

	String shaderAttrName;

	boolean normalized;

	UDataType format;

	int offset;
	int elementCount;

	/**
	 * Gets the name of the shader program attribute to which this data should be bound.
	 *
	 * @return
	 */
	public String getShaderAttrName() {
		return shaderAttrName;
	}

	/**
	 * Checks whether a fixed point attribute is normalized.
	 *
	 * @return True if the attribute should be normalized, false if used as its fixed point value.
	 */
	public boolean isNormalized() {
		return normalized;
	}

	/**
	 * Gets the raw data format of the attribute.
	 *
	 * @return
	 */
	public UDataType getFormat() {
		return format;
	}

	/**
	 * Gets the offset of the attribute within a vertex's structure.
	 *
	 * @return
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Gets the component count of a vector attribute.
	 *
	 * @return
	 */
	public int getElementCount() {
		return elementCount;
	}

	/**
	 * Gets the total size of the attribute per vertex in bytes.
	 *
	 * @return
	 */
	public int getSize() {
		return format.sizeof * elementCount;
	}
}
