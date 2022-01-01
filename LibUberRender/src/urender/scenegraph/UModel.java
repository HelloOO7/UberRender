package urender.scenegraph;

import java.util.ArrayList;
import java.util.List;
import urender.engine.UGfxObject;
import urender.engine.UGfxRenderer;
import urender.engine.UMaterial;
import urender.engine.UMesh;
import urender.engine.UTexture;
import urender.engine.shader.UShaderProgram;
import urender.engine.shader.UUniformList;

public class UModel extends UGfxScenegraphObject {

	public final List<UMeshInstance> meshes = new ArrayList<>();

	public void draw(UGfxRenderer rnd, List<UMesh> meshList, List<UMaterial> materialList, List<UShaderProgram> shaderList, UUniformList uniforms, List<UTexture> textureList) {
		UShaderProgram lastShader = null;
		UMaterial lastMaterial = null;

		for (UModel.UMeshInstance inst : meshes) {
			UMesh m = UGfxObject.find(meshList, inst.meshName);
			UMaterial mat = UGfxObject.find(materialList, inst.materialName);
			if (m != null && mat != null) {
				//System.out.println("Rendering mesh " + m.getName() + " using material " + mat.getName() + " and shader " + mat.getShaderProgramName());
				UShaderProgram shader = UGfxObject.find(shaderList, mat.getShaderProgramName());

				if (shader != null) {
					if (shader != lastShader) {
						shader.use(rnd);

						uniforms.setup(shader, rnd);
					}
					if (mat != lastMaterial || shader != lastShader) {
						mat.configureShader(shader, rnd, textureList);
						lastMaterial = mat;
					}
					lastShader = shader;

					m.draw(rnd, shader);
				}
			} else {
				if (m == null) {
					System.err.println("Could not find mesh " + inst.meshName);
				} else if (mat == null) {
					System.err.println("Could not find material " + inst.materialName);
				}
			}
		}
	}

	@Override
	public UGfxScenegraphObjectType getType() {
		return UGfxScenegraphObjectType.MODEL;
	}

	public static class UMeshInstance {

		public String meshName;
		public String materialName;
	}
}
