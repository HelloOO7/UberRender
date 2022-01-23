package urender.scenegraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import urender.engine.UDrawSources;
import urender.engine.UDrawState;
import urender.engine.UGfxRenderer;
import urender.engine.UMaterial;
import urender.engine.UMesh;
import urender.engine.shader.UShader;
import urender.engine.shader.UShaderProgram;
import urender.engine.UTexture;
import urender.engine.shader.UUniformList;

public class USceneNode {
	public UNodeTransform transform = new UDefaultNodeTransform();
	public UParentRelation parentRelation = new UDefaultParentRelation();
	
	public final UUniformList uniforms = new UUniformList();
	
	public final List<UModel> models = new ArrayList<>();
	public final List<UShaderProgram> programs = new ArrayList<>();
	
	public final List<UTexture> textures = new ArrayList<>();
	public final List<UMesh> meshes = new ArrayList<>();
	public final List<UMaterial> materials = new ArrayList<>();
	public final List<UShader> shaders = new ArrayList<>();
	
	public void drawHeadless(UGfxRenderer rnd) {
		URenderQueue.URenderQueueNodeState nodeState = new URenderQueue.URenderQueueNodeState(this);
		nodeState.drawSources.setup(rnd);
		List<URenderQueue.URenderQueueMeshState> meshStates = new ArrayList<>();
		for (UModel mdl : models) {
			for (UModel.UMeshInstance meshInst : mdl.meshes) {
				URenderQueue.URenderQueueMeshState meshState = new URenderQueue.URenderQueueMeshState(nodeState, meshInst);
				meshStates.add(meshState);
			}
		}
		Collections.sort(meshStates);
		for (URenderQueue.URenderQueueMeshState ms : meshStates) {
			ms.draw(rnd);
		}
	}
	
	public UDrawSources getDrawSources() {
		return new UDrawSources(meshes, materials, shaders, programs, uniforms, textures);
	}
}
