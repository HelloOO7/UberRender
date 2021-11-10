package urender.engine;

import urender.api.UDataType;

public class UVertexAttribute {
	public String shaderAttrName;
	
	public boolean unsigned;
	public boolean normalized;
	
	public UDataType format;
	
	public int offset;
	public int elementCount;
	
	public int getSize() {
		return format.sizeof * elementCount;
	}
}
