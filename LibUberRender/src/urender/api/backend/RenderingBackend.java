package urender.api.backend;

import java.nio.Buffer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import urender.api.UBufferType;
import urender.api.UBufferUsageHint;
import urender.api.UDataType;
import urender.api.UObjHandle;
import urender.api.UPrimitiveType;
import urender.api.UShaderType;
import urender.api.UTextureFaceAssignment;
import urender.api.UTextureFormat;
import urender.api.UTextureMagFilter;
import urender.api.UTextureMinFilter;
import urender.api.UTextureType;
import urender.api.UTextureWrap;

public interface RenderingBackend {
	public Object getIdent();
	
	public void texInit(UObjHandle tex, UTextureType type);
	public void texUploadData2D(UObjHandle tex, int width, int height, UTextureFormat format, UTextureFaceAssignment faceAsgn, Buffer data);
	public void texSetParams(UObjHandle texture, UTextureType type, UTextureWrap wrapU, UTextureWrap wrapV, UTextureMagFilter magFilter, UTextureMinFilter minFilter);

	public void texUnitInit(UObjHandle texUnit, int unitIndex);
	public void texUnitSetTexture(UObjHandle texUnit, UTextureType type, UObjHandle texture);
	
	public void bufferInit(UObjHandle buffer);
	public void bufferUploadData(UBufferType target, UObjHandle buffer, UBufferUsageHint usage, Buffer data, int size);
	public void bufferAttribPointer(UObjHandle vbo, UObjHandle index, int size, UDataType type, boolean unsigned, boolean normalized, int stride, long offset);
	public void bufferAttribDisable(UObjHandle vbo, UObjHandle index);
	
	public void buffersDrawInline(UObjHandle vbo, UPrimitiveType primitiveType, int count);
	public void buffersDrawIndexed(UObjHandle vbo, UPrimitiveType primitiveType, UObjHandle ibo, UDataType iboFormat, int count);
	
	public void shaderInit(UObjHandle shader, UShaderType type);
	public void shaderCompileSource(UObjHandle shader, String source);
	
	public void programInit(UObjHandle program);
	public void programAttachShader(UObjHandle program, UObjHandle shader);
	public void programLink(UObjHandle program);
	public void programUse(UObjHandle program);
	
	public void uniformLocationInit(UObjHandle program, UObjHandle uniform, String name);
	public void uniformMat4(UObjHandle location, Matrix4f matrix);
	public void uniformMat3(UObjHandle location, Matrix3f matrix);
	public void uniformSampler(UObjHandle location, UObjHandle texUnit);
	
	public void attributeLocationInit(UObjHandle program, UObjHandle attribute, String name);
	
	public void flush();
}
