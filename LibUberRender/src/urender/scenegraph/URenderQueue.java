package urender.scenegraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.joml.Matrix4f;
import urender.api.backend.RenderingBackend;
import urender.engine.UShadingMethod;
import urender.engine.UGfxObject;
import urender.engine.UMaterial;
import urender.engine.UMaterialDrawLayer;
import urender.engine.UMesh;
import urender.engine.UTexture;
import urender.engine.shader.UShaderProgram;
import urender.engine.shader.UUniformList;

/**
 * Calculated and sorted render object queue of a scenegraph tree.
 */
public class URenderQueue {

	List<UDrawSources> drawSourcesAll = new ArrayList<>();
	List<URenderQueueMeshState> queue = new ArrayList<>();

	/**
	 * Registers a list of draw sources for setup.
	 *
	 * @param sources
	 */
	public void registDrawSources(UDrawSources sources) {
		drawSourcesAll.add(sources);
	}

	/**
	 * Adds a calculated object state to the render queue.
	 *
	 * @param state
	 */
	public void enqueue(URenderQueueMeshState state) {
		queue.add(state);
	}

	/**
	 * Sorts the render queue by shading layer/priority.
	 */
	public void sort() {
		Collections.sort(queue);
	}

	/**
	 * Sorts the render queue using a user-defined algorithm.
	 *
	 * @param sorter
	 */
	public void sort(URenderQueueSorter sorter) {
		queue.sort(sorter);
	}

	/**
	 * Gets an iterable of all registered draw sources.
	 *
	 * @return
	 */
	public Iterable<UDrawSources> drawSources() {
		return drawSourcesAll;
	}

	/**
	 * Gets an iterable of all queued render objects.
	 *
	 * @return
	 */
	public Iterable<URenderQueueMeshState> queue() {
		return queue;
	}

	/**
	 * Deduces the maximum used render priority of a shading layer.
	 *
	 * @param sm
	 * @return
	 */
	public int getMaxRenderPriority(UShadingMethod sm) {
		int max = -1;
		for (URenderQueueMeshState s : queue) {
			int p;
			if ((p = s.mat.getDrawLayer().priority) > max) {
				max = p;
			}
		}
		return max;
	}

	/**
	 * Render queue state of a scenegraph node.
	 */
	public static class URenderQueueNodeState {

		/**
		 * View/camera matrix.
		 */
		public Matrix4f viewMatrix;
		/**
		 * Projection matrix.
		 */
		public Matrix4f projectionMatrix;
		/**
		 * Model/world matrix.
		 */
		public Matrix4f modelMatrix;

		/**
		 * List of all graphics resources available to the node.
		 */
		public final UDrawSources drawSources;

		/**
		 * List of all global shader parameters available to the node.
		 */
		public final UUniformList uniforms = new UUniformList();

		/**
		 * Creates a render queue node state based off a scene node.
		 *
		 * @param node
		 */
		public URenderQueueNodeState(USceneNode node) {
			this.drawSources = node.getDrawSources();
			uniforms.addAll(node.uniforms);
		}
	}

	/**
	 * Render queue state of a geometry mesh of a scenegraph node.
	 */
	public static class URenderQueueMeshState implements Comparable<URenderQueueMeshState> {

		/**
		 * Render queue state of the parent node.
		 */
		public final URenderQueueNodeState nodeState;
		/**
		 * Mesh instance that this state targets.
		 */
		public final UModel.UMeshInstance meshInstance;

		private final UMesh mesh;
		private final UMaterial mat;

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
				} else if (mat == null) {
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
}
