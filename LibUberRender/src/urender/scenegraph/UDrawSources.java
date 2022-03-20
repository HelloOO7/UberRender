package urender.scenegraph;

import java.util.List;
import urender.api.backend.RenderingBackend;
import urender.engine.UMaterial;
import urender.engine.UMesh;
import urender.engine.UTexture;
import urender.engine.shader.UShader;
import urender.engine.shader.UShaderProgram;
import urender.engine.shader.UUniformList;

/**
 * List of graphics resources available to a context. For internal usage.
 */
public class UDrawSources {

	public final List<UMesh> meshList;
	public final List<UMaterial> materialList;
	public final List<UShader> shaderList;
	public final List<UShaderProgram> shaderProgramList;
	public final List<UTexture> textureList;

	public UDrawSources(
		List<UMesh> meshList,
		List<UMaterial> materialList,
		List<UShader> shaderList,
		List<UShaderProgram> shaderProgramList,
		UUniformList uniforms,
		List<UTexture> textureList
	) {
		this.meshList = meshList;
		this.materialList = materialList;
		this.shaderList = shaderList;
		this.shaderProgramList = shaderProgramList;
		this.textureList = textureList;
	}

	/**
	 * Readies all underlying resources for rendering.
	 * @param rnd
	 */
	public void setup(UGfxRenderer rnd) {
		RenderingBackend core = rnd.getCore();
		for (UMesh m : meshList) {
			m.setup(core);
		}
		for (UTexture t : textureList) {
			t.setup(core);
		}
		for (UShader shader : shaderList) {
			shader.setup(core);
		}
		for (UShaderProgram program : shaderProgramList) {
			program.setup(core, shaderList);
		}
	}
	
	/**
	 * Deletes all underlying resources from video memory.
	 * @param rnd
	 */
	public void delete(UGfxRenderer rnd) {
		RenderingBackend core = rnd.getCore();
		
		UMesh.deleteAll(core, meshList);
		UTexture.deleteAll(core, textureList);
		UShader.deleteAll(core, shaderList);
		UShaderProgram.deleteAll(core, shaderProgramList);
	}
}
