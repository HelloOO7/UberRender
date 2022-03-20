package urender.engine;

import java.util.ArrayList;
import java.util.List;
import urender.api.UObjHandle;
import urender.api.backend.RenderingBackend;
import urender.engine.shader.UShaderProgram;
import urender.engine.shader.UUniformList;

/**
 * Material resource.
 */
public class UMaterial extends UGfxEngineObject {

	String shaderProgramName;

	UMaterialDrawLayer drawLayer = new UMaterialDrawLayer(UShadingMethod.DEFERRED, 0);

	public final UUniformList shaderParams = new UUniformList();

	final List<UTextureMapper> textureMappers = new ArrayList<>();

	UMaterial() {
	}

	/**
	 * Gets the name of the shader program to be used for rendering using this material.
	 *
	 * @return Local name of the shader program.
	 */
	public String getShaderProgramName() {
		return shaderProgramName;
	}

	/**
	 * Gets the layer that meshes that use this material should be drawn on.
	 *
	 * @return
	 */
	public UMaterialDrawLayer getDrawLayer() {
		return drawLayer;
	}

	/**
	 * Sets the layer that meshes that use this material should be drawn on.
	 *
	 * @param drawLayer
	 */
	public void setDrawLayer(UMaterialDrawLayer drawLayer) {
		this.drawLayer = drawLayer;
	}

	/**
	 * Changes the shader program bound to this material.
	 *
	 * @param program A shader program.
	 */
	public void bindShaderProgram(UShaderProgram program) {
		shaderProgramName = program == null ? null : program.getName();
	}

	/**
	 * Gets the number of texture mappers bound to this material.
	 *
	 * @return
	 */
	public int getTextureMapperCount() {
		return textureMappers.size();
	}

	/**
	 * Gets a texture mapper.
	 *
	 * @param index Index of the texture mapper.
	 * @return
	 */
	public UTextureMapper getTextureMapper(int index) {
		return textureMappers.get(index);
	}

	/**
	 * Gets the reference to this material's texture mapper list. Any changes made to it will be reflected in
	 * the material, so use with caution.
	 *
	 * @return
	 */
	public List<UTextureMapper> getTextureMappers() {
		return textureMappers;
	}

	/**
	 * Configures the shader program according to the material's shader parameters and texture mappers.
	 *
	 * @param shader The shader program to target.
	 * @param rnd Rendering backend core.
	 * @param textures List to search for required texture resources.
	 */
	public void configureShader(UShaderProgram shader, RenderingBackend rnd, List<UTexture> textures) {
		shader.use(rnd);
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
						rnd.texUnitSetTexture(texUnit, tex.getTextureType(), tex.__handle);
						rnd.texSetParams(tex.__handle, tex.getTextureType(), mapper.wrapU, mapper.wrapV, mapper.magFilter, mapper.minFilter);
					}

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
