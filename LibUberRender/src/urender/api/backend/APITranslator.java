package urender.api.backend;

import urender.api.UBufferType;
import urender.api.UBufferUsageHint;
import urender.api.UDataType;
import urender.api.UPrimitiveType;
import urender.api.UShaderType;
import urender.api.UTextureFaceAssignment;
import urender.api.UTextureFormat;
import urender.api.UTextureType;

public interface APITranslator {
	public int getDataType(UDataType type, boolean unsigned);
	public int getPrimitiveType(UPrimitiveType type);
	public int getTextureType(UTextureType t);
	public int getTextureFaceAssignment(UTextureFaceAssignment asgn);
	public int getTextureFormatDataType(UTextureFormat format);
	public int getTextureFormatInternalFormat(UTextureFormat format);
	public int getTextureFormatExternalFormat(UTextureFormat format);
	public int getBufferTargetType(UBufferType bufType);
	public int getBufferUsage(UBufferUsageHint usage);
	public int getShaderType(UShaderType type);
}
