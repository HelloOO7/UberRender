package urender.engine;

import urender.api.UDataType;

public class UVertexAttributeBuilder {
	private UVertexAttribute attr = new UVertexAttribute();
	
	public UVertexAttributeBuilder setShaderAttrName(String name) {
		attr.shaderAttrName = name;
		return this;
	}
	
	public UVertexAttributeBuilder setTypeUnsigned(boolean unsigned) {
		attr.unsigned = unsigned;
		return this;
	}
	
	public UVertexAttributeBuilder setNormalized(boolean normalized) {
		attr.normalized = normalized;
		return this;
	}
	
	public UVertexAttributeBuilder setFormat(UDataType format) {
		attr.format = format;
		return this;
	}
	
	public UVertexAttributeBuilder setOffset(int offset) {
		attr.offset = offset;
		return this;
	}
	
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
