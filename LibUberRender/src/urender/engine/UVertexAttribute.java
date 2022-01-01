package urender.engine;

import urender.api.UDataType;

public class UVertexAttribute {
	String shaderAttrName;
	
	boolean unsigned;
	boolean normalized;
	
	UDataType format;
	
	int offset;
	int elementCount;
	
	public String getShaderAttrName() {
		return shaderAttrName;
	}
	
	public boolean getTypeIsUnsigned() {
		return unsigned;
	}
	
	public boolean isNormalized() {
		return normalized;
	}
	
	public UDataType getFormat() {
		return format;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public int getElementCount() {
		return elementCount;
	}
	
	public int getSize() {
		return format.sizeof * elementCount;
	}
}
