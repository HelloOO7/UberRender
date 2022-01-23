package urender.scenegraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.joml.Matrix4f;
import urender.engine.UDrawSources;
import urender.engine.UDrawState;
import urender.engine.UGfxObject;
import urender.engine.UGfxRenderer;
import urender.engine.UMaterial;
import urender.engine.UMaterialDrawLayer;
import urender.engine.UMesh;
import urender.engine.shader.UShaderProgram;
import urender.engine.shader.UUniformList;

public class URenderQueue {

	List<UDrawSources> drawSourcesAll = new ArrayList<>();
	List<URenderQueueMeshState> queue = new ArrayList<>();

	public void registDrawSources(UDrawSources sources) {
		drawSourcesAll.add(sources);
	}

	public void enqueue(URenderQueueMeshState state) {
		queue.add(state);
	}

	public void sort() {
		Collections.sort(queue);
	}

	public Iterable<UDrawSources> drawSources() {
		return drawSourcesAll;
	}

	public Iterable<URenderQueueMeshState> queue() {
		return queue;
	}

	public static class URenderQueueNodeState {

		public Matrix4f viewMatrix;
		public Matrix4f projectionMatrix;

		public Matrix4f modelMatrix;

		public final UDrawSources drawSources;

		public final UUniformList uniforms = new UUniformList();

		public URenderQueueNodeState(USceneNode node) {
			this.drawSources = node.getDrawSources();
		}
	}

	public static class URenderQueueMeshState implements Comparable<URenderQueueMeshState> {

		public final URenderQueueNodeState nodeState;

		public final UModel.UMeshInstance meshInstance;

		private final UMesh mesh;
		private final UMaterial mat;

		public URenderQueueMeshState(URenderQueueNodeState nodeState, UModel.UMeshInstance meshInstance) {
			this.nodeState = nodeState;
			this.meshInstance = meshInstance;
			mesh = UGfxObject.find(nodeState.drawSources.meshList, meshInstance.meshName);
			mat = UGfxObject.find(nodeState.drawSources.materialList, meshInstance.materialName);
		}

		public void draw(UGfxRenderer rnd) {
			if (mesh != null && mat != null) {
				if (rnd.isShadingMethodCurrent(mat.getDrawLayer().method)) {
					UDrawSources sources = nodeState.drawSources;
					UDrawState drawState = rnd.getDrawState();
					//System.out.println("Rendering mesh " + mesh.getName() + " using material " + mat.getName() + " and shader " + mat.getShaderProgramName());
					UShaderProgram shader = UGfxObject.find(sources.shaderProgramList, mat.getShaderProgramName());

					if (shader != null) {
						if (shader != drawState.currentShader || !drawState.currentUniformSet.valuesMatch(nodeState.uniforms)) {
							shader.use(rnd);
							
							nodeState.uniforms.setup(shader, rnd);

							drawState.currentUniformSet = nodeState.uniforms;
						}
						if (mat != drawState.currentMaterial || shader != drawState.currentShader) {
							mat.configureShader(shader, rnd, sources.textureList);
							drawState.currentMaterial = mat;
						}
						drawState.currentShader = shader;

						mesh.draw(rnd, shader);
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

		private static final UMaterialDrawLayer DUMMY_DRAW_LAYER = new UMaterialDrawLayer(UMaterialDrawLayer.ShadingMethod.FORWARD, 0);

		@Override
		public int compareTo(URenderQueueMeshState o) {
			UMaterialDrawLayer thisLayer = mat == null ? DUMMY_DRAW_LAYER : mat.getDrawLayer();
			UMaterialDrawLayer otherLayer = o.mat == null ? DUMMY_DRAW_LAYER : o.mat.getDrawLayer();
			return thisLayer.compareTo(otherLayer);
		}
	}
}
