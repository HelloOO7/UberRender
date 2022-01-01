package urender.scenegraph;

import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import urender.engine.shader.UUniformList;

public class URenderQueue {
	
	private List<URenderQueueNodeState> queue = new ArrayList<>();
	
	public void enqueue(URenderQueueNodeState state) {
		queue.add(state);
	}
	
	public Iterable<URenderQueueNodeState> queue() {
		return queue;
	}

	public static class URenderQueueNodeState {
		public Matrix4f viewMatrix;
		public Matrix4f projectionMatrix;
		
		public Matrix4f modelMatrix;
		
		public final USceneNode node;
		
		public final UUniformList uniforms = new UUniformList();
		
		public URenderQueueNodeState(USceneNode node) {
			this.node = node;
		}
	}
}
