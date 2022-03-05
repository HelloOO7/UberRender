package urender.engine;

import urender.api.UDataType;

public class UVertexAttributeBuilder {

	private UVertexAttribute attr = new UVertexAttribute();

	/**
	 * Sets the name of the shader program attribute to which this data should be bound.
	 *
	 * @param name
	 * @return this
	 */
	public UVertexAttributeBuilder setShaderAttrName(String name) {
		attr.shaderAttrName = name;
		return this;
	}

	/**
	 * Sets whether a fixed point attribute is normalized.
	 *
	 * @param normalized
	 * @return this
	 */
	public UVertexAttributeBuilder setNormalized(boolean normalized) {
		attr.normalized = normalized;
		return this;
	}

	/**
	 * Sets the raw data format of the attribute.
	 *
	 * @param format
	 * @return this
	 */
	public UVertexAttributeBuilder setFormat(UDataType format) {
		attr.format = format;
		return this;
	}

	/**
	 * Sets the offset of the attribute within a vertex's structure.
	 *
	 * @param offset
	 * @return this
	 */
	public UVertexAttributeBuilder setOffset(int offset) {
		attr.offset = offset;
		return this;
	}

	/**
	 * Sets the component count of a vector attribute.
	 *
	 * @param elementCount
	 * @return this
	 */
	public UVertexAttributeBuilder setElementCount(int elementCount) {
		attr.elementCount = elementCount;
		return this;
	}

	public UVertexAttribute build() {
		return attr;
	}

	public void reset() {
		attr = new UVertexAttribute();
	}
}
