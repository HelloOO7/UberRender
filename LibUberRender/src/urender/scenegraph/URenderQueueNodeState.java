package urender.scenegraph;

import org.joml.Matrix4f;
import urender.engine.shader.UUniformList;

/**
 * Render queue state of a scenegraph node.
 */
public class URenderQueueNodeState {

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
