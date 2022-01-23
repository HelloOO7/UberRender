package urender.engine;

import java.util.List;
import urender.engine.shader.UShader;
import urender.engine.shader.UShaderProgram;
import urender.engine.shader.UUniformList;

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

	public void setup(UGfxRenderer rnd) {
		for (UMesh m : meshList) {
			m.setup(rnd);
		}
		for (UTexture t : textureList) {
			t.setup(rnd);
		}
		for (UShader shader : shaderList) {
			shader.setup(rnd);
		}
		for (UShaderProgram program : shaderProgramList) {
			program.setup(rnd, shaderList);
		}
	}
}
