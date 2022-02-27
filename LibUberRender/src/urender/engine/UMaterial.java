package urender.engine;

import urender.scenegraph.UGfxRenderer;
import java.util.ArrayList;
import java.util.List;
import urender.api.UObjHandle;
import urender.api.UTextureType;
import urender.api.backend.RenderingBackend;
import urender.engine.shader.UShaderProgram;
import urender.engine.shader.UUniformList;

public class UMaterial extends UGfxEngineObject {

	String shaderProgramName;

	UMaterialDrawLayer drawLayer = new UMaterialDrawLayer(UMaterialDrawLayer.ShadingMethod.DEFERRED, 0);

	public final UUniformList shaderParams = new UUniformList();

	final List<UTextureMapper> textureMappers = new ArrayList<>();

	UMaterial() {
	}

	public String getShaderProgramName() {
		return shaderProgramName;
	}

	public UMaterialDrawLayer getDrawLayer() {
		return drawLayer;
	}

	public void setDrawLayer(UMaterialDrawLayer drawLayer) {
		this.drawLayer = drawLayer;
	}

	public void bindShaderProgram(UShaderProgram program) {
		shaderProgramName = program == null ? null : program.getName();
	}

	public int getTextureMapperCount() {
		return textureMappers.size();
	}

	public UTextureMapper getTextureMapper(int index) {
		return textureMappers.get(index);
	}

	public List<UTextureMapper> getTextureMappers() {
		return textureMappers;
	}

	public void configureShader(UShaderProgram shader, RenderingBackend rnd, List<UTexture> textures) {
		shaderParams.setup(shader, rnd);

		int texUnitIdx = 0;
		for (UTextureMapper mapper : textureMappers) {
			if (mapper.textureName == null) {
				continue;
			}
			URenderTarget rt = null;
			UTexture tex = rt == null ? UGfxObject.find(textures, mapper.textureName) : null;

			if (tex != null || rt != null) {
				UObjHandle loc = shader.getUniformLocation(rnd, mapper.shaderVariableName);
				if (loc.isValid(rnd)) {
					UObjHandle texUnit = new UObjHandle();
					rnd.texUnitInit(texUnit, texUnitIdx);

					if (tex != null) {
						//Use texture
						rnd.texUnitSetTexture(texUnit, tex.getTextureType(), tex.__handle);
						rnd.texSetParams(tex.__handle, tex.getTextureType(), mapper.wrapU, mapper.wrapV, mapper.magFilter, mapper.minFilter);
					} /*else if (rt != null) {
						//Use RenderTexture
						if (rt.__textureHandle.isInitialized(rnd)) {
							//System.out.println("Binding RenderTexture " + rt.name + " to material " + name + " mapper " + mapper.shaderVariableName);
							rnd.texUnitSetTexture(texUnit, UTextureType.TEX2D, rt.__textureHandle);
							rnd.texSetParams(rt.__textureHandle, UTextureType.TEX2D, mapper.wrapU, mapper.wrapV, mapper.magFilter, mapper.minFilter);
						}
						else {
							System.err.println("RenderTarget RenderTexture not initialized! RT: " + rt.name);
						}
					}*/

					rnd.uniformSampler(loc, texUnit);
					texUnitIdx++;
				}
			} else {
				System.err.println("Could not find texture " + mapper.textureName);
			}
		}
	}

	@Override
	public UGfxEngineObjectType getType() {
		return UGfxEngineObjectType.MATERIAL;
	}

}
