package urender.engine;

import java.util.ArrayList;
import java.util.List;
import urender.api.UObjHandle;
import urender.api.backend.RenderingBackend;
import urender.engine.shader.UShaderProgram;

public class UMaterial extends UGfxEngineObject {

	String shaderProgramName;

	final List<UTextureMapper> textureMappers = new ArrayList<>();

	UMaterial() {
	}
	
	public String getShaderProgramName() {
		return shaderProgramName;
	}
	
	public int getTextureMapperCount() {
		return textureMappers.size();
	}
	
	public UTextureMapper getTextureMapper(int index) {
		return textureMappers.get(index);
	}
	
	public Iterable<UTextureMapper> textureMappers() {
		return textureMappers;
	}

	public void configureShader(UShaderProgram shader, UGfxRenderer rnd, List<UTexture> textures) {
		RenderingBackend core = rnd.getCore();
		UObjHandle texUnit = new UObjHandle();

		int texUnitIdx = 0;
		for (UTextureMapper mapper : textureMappers) {
			core.texUnitInit(texUnit, texUnitIdx);

			UTexture tex = UGfxObject.find(textures, mapper.textureName);

			if (tex != null) {
				core.texUnitSetTexture(texUnit, tex.getTextureType(), tex.__handle);
				core.texSetParams(tex.__handle, tex.getTextureType(), mapper.wrapU, mapper.wrapV, mapper.magFilter, mapper.minFilter);
			}
			else {
				System.err.println("Could not find texture " + mapper.textureName);
			}

			core.uniformSampler(shader.getUniformLocation(rnd, mapper.shaderVariableName), texUnit);

			texUnit.reset();
			texUnitIdx++;
		}
	}

	@Override
	public UGfxEngineObjectType getType() {
		return UGfxEngineObjectType.MATERIAL;
	}

}
