package urender.scenegraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import urender.engine.UMaterial;
import urender.engine.UMesh;
import urender.engine.shader.UShader;
import urender.engine.shader.UShaderProgram;
import urender.engine.UTexture;
import urender.engine.shader.UUniformList;

/**
 * Scenegraph node.
 */
public class USceneNode {

	/**
	 * Transform of the scene node relative to the parent.
	 */
	public UNodeTransform transform = new UDefaultNodeTransform();
	/**
	 * Relation to a parent scenegraph node.
	 */
	public UParentRelation parentRelation = new UDefaultParentRelation();
	/**
	 * List of shader parameters global to this node and all its children.
	 */
	public final UUniformList uniforms = new UUniformList();

	/**
	 * Local model resources.
	 */
	public final List<UModel> models = new ArrayList<>();
	/**
	 * Local shader programs.
	 */
	public final List<UShaderProgram> programs = new ArrayList<>();

	/**
	 * Local texture resources.
	 */
	public final List<UTexture> textures = new ArrayList<>();
	/**
	 * Local mesh resources.
	 */
	public final List<UMesh> meshes = new ArrayList<>();
	/**
	 * Local material resources.
	 */
	public final List<UMaterial> materials = new ArrayList<>();
	/**
	 * Local shader resources.
	 */
	public final List<UShader> shaders = new ArrayList<>();

	/**
	 * Renders this node with a dummy render queue.
	 *
	 * @param rnd Rendering engine.
	 */
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

	/**
	 * Creates a draw source array from this node's local resources.
	 *
	 * @return
	 */
	public UDrawSources getDrawSources() {
		return new UDrawSources(meshes, materials, shaders, programs, uniforms, textures);
	}
}
