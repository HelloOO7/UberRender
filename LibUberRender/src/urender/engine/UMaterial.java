package urender.engine;

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

	public void configureShader(UShaderProgram shader, UGfxRenderer rnd, List<UTexture> textures) {
		RenderingBackend core = rnd.getCore();

		shaderParams.setup(shader, rnd);

		int texUnitIdx = 0;
		for (UTextureMapper mapper : textureMappers) {
			URenderTarget rt = rnd.findRenderTarget(mapper.textureName);
			UTexture tex = rt == null ? UGfxObject.find(textures, mapper.textureName) : null;

			if (tex != null || rt != null) {
				UObjHandle loc = shader.getUniformLocation(rnd, mapper.shaderVariableName);
				if (loc.isValid(core)) {
					UObjHandle texUnit = new UObjHandle();
					core.texUnitInit(texUnit, texUnitIdx);

					if (tex != null) {
						//Use texture
						core.texUnitSetTexture(texUnit, tex.getTextureType(), tex.__handle);
						core.texSetParams(tex.__handle, tex.getTextureType(), mapper.wrapU, mapper.wrapV, mapper.magFilter, mapper.minFilter);
					} else if (rt != null) {
						//Use RenderTexture
						if (rt.__textureHandle.isInitialized(core)) {
							//System.out.println("Binding RenderTexture " + rt.name + " to material " + name + " mapper " + mapper.shaderVariableName);
							core.texUnitSetTexture(texUnit, UTextureType.TEX2D, rt.__textureHandle);
							core.texSetParams(rt.__textureHandle, UTextureType.TEX2D, mapper.wrapU, mapper.wrapV, mapper.magFilter, mapper.minFilter);
						}
						else {
							System.err.println("RenderTarget RenderTexture not initialized! RT: " + rt.name);
						}
					}

					core.uniformSampler(loc, texUnit);
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
