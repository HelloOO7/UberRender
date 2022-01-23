package urender.scenegraph;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import urender.common.math.Matrix4Unit;
import urender.engine.shader.UUniform;

public class UScene extends UGfxScenegraphObject {

	private final Matrix4Unit mtxMem = new Matrix4Unit(4);

	public UCamera camera = new UCameraViewpoint();

	private final USceneNode rootNode = new USceneNode();

	public void addChild(USceneNode child) {
		rootNode.parentRelation.getChildren().add(child);
	}

	public void addGlobalUniform(UUniform uniform) {
		rootNode.uniforms.add(uniform);
	}

	@Override
	public UGfxScenegraphObjectType getType() {
		return UGfxScenegraphObjectType.SCENE;
	}

	public URenderQueue calcRenderQueue() {
		URenderQueue queue = new URenderQueue();

		calcQueueNode(rootNode, null, queue);
		queue.sort();

		return queue;
	}

	private void calcQueueNode(USceneNode node, URenderQueue.URenderQueueNodeState parentState, URenderQueue queue) {
		URenderQueue.URenderQueueNodeState state = new URenderQueue.URenderQueueNodeState(node);
		queue.registDrawSources(state.drawSources);

		if (parentState == null) {
			state.modelMatrix = new Matrix4f();
			state.viewMatrix = new Matrix4f();
			state.projectionMatrix = new Matrix4f();

			camera.getProjectionMatrix(state.projectionMatrix);
			camera.mulViewMatrix(state.viewMatrix);
			state.viewMatrix.invert();
		} else {
			state.viewMatrix = parentState.viewMatrix;
			state.projectionMatrix = parentState.projectionMatrix;
			state.modelMatrix = new Matrix4f(parentState.modelMatrix);
			state.uniforms.addAll(parentState.uniforms);
		}

		state.uniforms.addAll(node.uniforms);

		int inh = node.parentRelation.getTransformInheritance();
		if ((inh & UTransformInheritance.IGNORE_CAMERA) != 0) {
			state.viewMatrix = new Matrix4f();
		}
		if ((inh & UTransformInheritance.INHERIT_SCALE) == 0) {
			state.modelMatrix.normalize3x3();
		}
		state.modelMatrix.scale(node.transform.getScale());

		if ((inh & UTransformInheritance.INHERIT_TRANSLATION) == 0) {
			state.modelMatrix.setTranslation(node.transform.getTranslation());
		} else {
			state.modelMatrix.translate(node.transform.getTranslation());
		}

		if ((inh & UTransformInheritance.INHERIT_ROTATION) == 0) {
			Vector3f scale = new Vector3f();
			Vector3f rotation = node.transform.getRotation();

			Matrix4f temp = mtxMem.malloc();
			state.modelMatrix.getScale(scale);
			temp.rotationZYX(rotation.z, rotation.y, rotation.x);
			temp.scale(scale);

			state.modelMatrix.set3x3(temp);
			mtxMem.free(temp);
		} else {
			state.modelMatrix.rotateZYX(node.transform.getRotation());
		}

		for (UModel model : node.models) {
			for (UModel.UMeshInstance meshInst : model.meshes) {
				queue.enqueue(new URenderQueue.URenderQueueMeshState(state, meshInst));
			}
		}

		for (USceneNode child : node.parentRelation.getChildren()) {
			calcQueueNode(child, state, queue);
		}
	}
}
