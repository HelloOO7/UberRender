package urender.engine;

import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import urender.api.UObjHandle;
import urender.api.UTextureMagFilter;
import urender.api.UTextureMinFilter;
import urender.api.UTextureWrap;
import urender.api.backend.RenderingBackend;
import urender.engine.shader.UShaderProgram;

public class UMaterial extends UGfxObject {

	public String shaderProgramName;

	public final List<UTextureMapper> textureMappers = new ArrayList<>();

	public void configureShader(UShaderProgram shader, UGfxRenderer rnd, List<UTexture> textures) {
		RenderingBackend core = rnd.getCore();
		UObjHandle texUnit = new UObjHandle();

		int index = 0;
		for (UTextureMapper mapper : textureMappers) {
			core.texUnitInit(texUnit, index);

			UTexture tex = UGfxObject.find(textures, mapper.textureName);

			if (tex != null) {
				core.texUnitSetTexture(texUnit, tex.getType(), tex.__handle);
				core.texSetParams(tex.__handle, tex.getType(), mapper.wrapU, mapper.wrapV, mapper.magFilter, mapper.minFilter);
			}
			else {
				System.err.println("Could not find texture " + mapper.textureName);
			}

			core.uniformSampler(shader.getUniformLocation(rnd, mapper.shaderVariableName), texUnit);

			texUnit.reset();
			index++;
		}
	}

	public static class UTextureMapper {

		public String textureName;
		public String meshUVSetName;

		public String shaderVariableName;

		public Vector2f translation;
		public float rotation;
		public Vector2f scale;
		
		public UTextureWrap wrapU = UTextureWrap.REPEAT;
		public UTextureWrap wrapV = UTextureWrap.REPEAT;
		public UTextureMagFilter magFilter = UTextureMagFilter.LINEAR;
		public UTextureMinFilter minFilter = UTextureMinFilter.LINEAR;
	}
}
