package urender.engine;

import org.joml.Vector2f;
import urender.api.UTextureMagFilter;
import urender.api.UTextureMinFilter;
import urender.api.UTextureWrap;

public class UTextureMapper {

	String textureName;
	String meshUVSetName;

	String shaderVariableName;
	
	public final TextureTransform transform = new TextureTransform();

	UTextureWrap wrapU = UTextureWrap.REPEAT;
	UTextureWrap wrapV = UTextureWrap.REPEAT;
	UTextureMagFilter magFilter = UTextureMagFilter.LINEAR;
	UTextureMinFilter minFilter = UTextureMinFilter.LINEAR;
	
	public String getTextureName() {
		return textureName;
	}
	
	public String getMeshUVSetName() {
		return meshUVSetName;
	}
	
	public String getShaderVariableName() {
		return shaderVariableName;
	}
	
	public UTextureWrap getWrapU() {
		return wrapU;
	}
	
	public UTextureWrap getWrapV() {
		return wrapV;
	}
	
	public UTextureMagFilter getMagFilter() {
		return magFilter;
	}
	
	public UTextureMinFilter getMinFilter() {
		return minFilter;
	}

	public static class TextureTransform {

		public final Vector2f translation = new Vector2f();
		public float rotation;
		public final Vector2f scale = new Vector2f();
	}
}
