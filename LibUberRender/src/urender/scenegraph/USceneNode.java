package urender.scenegraph;

import java.util.ArrayList;
import java.util.List;
import urender.engine.UGfxObject;
import urender.engine.UGfxRenderer;
import urender.engine.UMaterial;
import urender.engine.UMesh;
import urender.engine.shader.UShader;
import urender.engine.shader.UShaderProgram;
import urender.engine.UTexture;
import urender.engine.shader.UUniformList;

public class USceneNode {
	public final UUniformList uniforms = new UUniformList();
	
	public final List<UModel> models = new ArrayList<>();
	public final List<UShaderProgram> programs = new ArrayList<>();
	
	public final List<UTexture> textures = new ArrayList<>();
	public final List<UMesh> meshes = new ArrayList<>();
	public final List<UMaterial> materials = new ArrayList<>();
	public final List<UShader> shaders = new ArrayList<>();
	
	public void setup(UGfxRenderer rnd) {
		for (UMesh m : meshes) {
			m.setup(rnd);
		}
		for (UTexture t : textures) {
			t.setup(rnd);
		}
		for (UShader shader : shaders) {
			shader.setup(rnd);
		}
		for (UShaderProgram program : programs) {
			program.setup(rnd, shaders);
		}
	}
	
	public void drawAllModels(UGfxRenderer rnd) {
		for (UModel mdl : models) {
			mdl.draw(rnd, meshes, materials, programs, uniforms, textures);
		}
	}
}
