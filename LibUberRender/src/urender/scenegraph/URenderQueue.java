package urender.scenegraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.joml.Matrix4f;
import urender.engine.UShadingMethod;
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
			if (s.mat != null) {
				if ((p = s.mat.getDrawLayer().priority) > max) {
					max = p;
				}
			}
		}
		return max;
	}
}
