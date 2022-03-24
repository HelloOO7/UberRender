package urender.scenegraph;

import java.util.ArrayList;
import java.util.List;
import urender.api.backend.RenderingBackend;
import urender.engine.UGfxObject;
import urender.engine.UMaterial;
import urender.engine.UMaterialDrawLayer;
import urender.engine.UMesh;
import urender.engine.UShadingMethod;
import urender.engine.UTexture;
import urender.engine.shader.UShaderProgram;

/**
 * Render queue state of a geometry mesh of a scenegraph node.
 */
public class URenderQueueMeshState implements Comparable<URenderQueueMeshState> {

	/**
	 * Render queue state of the parent node.
	 */
	public final URenderQueueNodeState nodeState;
	/**
	 * Mesh instance that this state targets.
	 */
	public final UModel.UMeshInstance meshInstance;
	private final UMesh mesh;
	final UMaterial mat;

	/**
	 * Creates a render queue node state for a mesh instance.
	 *
	 * @param nodeState State of the parent node.
	 * @param meshInstance The mesh instance to target.
	 */
	public URenderQueueMeshState(URenderQueueNodeState nodeState, UModel.UMeshInstance meshInstance) {
		this.nodeState = nodeState;
		this.meshInstance = meshInstance;
		mesh = UGfxObject.find(nodeState.drawSources.meshList, meshInstance.meshName);
		mat = UGfxObject.find(nodeState.drawSources.materialList, meshInstance.materialName);
	}

	/**
	 * Gets the render priority of the underlying material.
	 *
	 * @return
	 */
	public int getDrawPriority() {
		if (mat == null) {
			return -1;
		}
		return mat.getDrawLayer().priority;
	}

	/**
	 * Configures materials and shaders and draws the mesh.
	 * The mesh will not be drawn if its shading method is non-current.
	 *
	 * @param rnd Rendering engine.
	 */
	public void draw(UGfxRenderer rnd) {
		if (mesh != null && mat != null) {
			RenderingBackend core = rnd.getCore();
			if (rnd.isShadingMethodCurrent(mat.getDrawLayer().method)) {
				UDrawSources sources = nodeState.drawSources;
				UDrawState drawState = rnd.getDrawState();
				//System.out.println("Rendering mesh " + mesh.getName() + " using material " + mat.getName() + " and shader " + mat.getShaderProgramName() + " layer " + mat.getDrawLayer().priority);
				UShaderProgram shader = UGfxObject.find(sources.shaderProgramList, mat.getShaderProgramName());
				if (shader != null) {
					if (shader != drawState.currentShader || !drawState.currentUniformSet.valuesMatch(nodeState.uniforms)) {
						shader.use(core);
						nodeState.uniforms.setup(shader, core);
						drawState.commonUniforms.setup(shader, core);
						drawState.currentUniformSet = nodeState.uniforms;
					}
					if (mat != drawState.currentMaterial || shader != drawState.currentShader) {
						List<UTexture> textureTmp = new ArrayList<>();
						textureTmp.addAll(rnd.getRenderTextures()); //priority over normal textures
						textureTmp.addAll(sources.textureList);
						mat.configureShader(shader, core, textureTmp);
						drawState.currentMaterial = mat;
					}
					drawState.currentShader = shader;
					mesh.draw(core, shader);
				}
			}
		} else {
			if (mesh == null) {
				System.err.println("Could not find mesh " + meshInstance.meshName);
			} else if (mat == null && meshInstance.materialName != null) {
				System.err.println("Could not find material " + meshInstance.materialName);
			}
		}
	}
	private static final UMaterialDrawLayer DUMMY_DRAW_LAYER = new UMaterialDrawLayer(UShadingMethod.FORWARD, 0);

	@Override
	public int compareTo(URenderQueueMeshState o) {
		UMaterialDrawLayer thisLayer = mat == null ? DUMMY_DRAW_LAYER : mat.getDrawLayer();
		UMaterialDrawLayer otherLayer = o.mat == null ? DUMMY_DRAW_LAYER : o.mat.getDrawLayer();
		return thisLayer.compareTo(otherLayer);
	}

}
