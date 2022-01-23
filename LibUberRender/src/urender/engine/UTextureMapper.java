package urender.engine;

import org.joml.Vector2f;
import urender.api.UTextureMagFilter;
import urender.api.UTextureMinFilter;
import urender.api.UTextureWrap;

public class UTextureMapper {

	String textureName;

	String shaderVariableName;
	
	UTextureWrap wrapU = UTextureWrap.REPEAT;
	UTextureWrap wrapV = UTextureWrap.REPEAT;
	UTextureMagFilter magFilter = UTextureMagFilter.LINEAR;
	UTextureMinFilter minFilter = UTextureMinFilter.LINEAR;
	
	public void setTexture(UTexture tex) {
		textureName = tex == null ? null : tex.getName();
	}
	
	public String getTextureName() {
		return textureName;
	}
	
	public String getShaderVariableName() {
		return shaderVariableName;
	}
	
	public void setShaderVariableName(String str) {
		this.shaderVariableName = str;
	}
	
	public UTextureWrap getWrapU() {
		return wrapU;
	}
	
	public void setWrapU(UTextureWrap wrapU) {
		this.wrapU = wrapU;
	}
	
	public UTextureWrap getWrapV() {
		return wrapV;
	}
	
	public void setWrapV(UTextureWrap wrapV) {
		this.wrapV = wrapV;
	}
	
	public UTextureMagFilter getMagFilter() {
		return magFilter;
	}
	
	public void setMagFilter(UTextureMagFilter magFilter) {
		this.magFilter = magFilter;
	}
	
	public UTextureMinFilter getMinFilter() {
		return minFilter;
	}
	
	public void setMinFilter(UTextureMinFilter minFilter) {
		this.minFilter = minFilter;
	}

	public static class TextureTransform {

		public final Vector2f translation = new Vector2f();
		public float rotation;
		public final Vector2f scale = new Vector2f();
	}
}
