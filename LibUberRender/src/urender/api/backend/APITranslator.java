package urender.api.backend;

import urender.api.UBlendEquation;
import urender.api.UBlendFunction;
import urender.api.UBufferType;
import urender.api.UBufferUsageHint;
import urender.api.UDataType;
import urender.api.UFaceCulling;
import urender.api.UFramebufferAttachment;
import urender.api.UPrimitiveType;
import urender.api.UShaderType;
import urender.api.UTestFunction;
import urender.api.UTextureSwizzleChannel;
import urender.api.UTextureFaceAssignment;
import urender.api.UTextureFormat;
import urender.api.UTextureMagFilter;
import urender.api.UTextureMinFilter;
import urender.api.UTextureType;
import urender.api.UTextureWrap;

public interface APITranslator {
	public int getDataType(UDataType type);
	public int getPrimitiveType(UPrimitiveType type);
	public int getTextureType(UTextureType t);
	public int getTextureFaceAssignment(UTextureFaceAssignment asgn);
	public int getTextureFormatDataType(UTextureFormat format);
	public int getTextureFormatInternalFormat(UTextureFormat format);
	public int getTextureFormatExternalFormat(UTextureFormat format);
	public int getTextureSwizzleChannel(UTextureSwizzleChannel channel);
	public int getBufferTargetType(UBufferType bufType);
	public int getBufferUsage(UBufferUsageHint usage);
	public int getShaderType(UShaderType type);
	public int getTextureWrap(UTextureWrap wrap);
	public int getTextureMagFilter(UTextureMagFilter filter);
	public int getTextureMinFilter(UTextureMinFilter filter);
	public int getFramebufferAttachment(UFramebufferAttachment attachment, int index);
	public int getBlendEquation(UBlendEquation eq);
	public int getBlendFunc(UBlendFunction func);
	public int getFaceCulling(UFaceCulling faceCulling);
	public int getTestFunc(UTestFunction func);
}
