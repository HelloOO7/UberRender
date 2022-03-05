package urender.scenegraph;

import java.util.Comparator;

/**
 * Interface used for sorting objects in a render queue.
 */
public interface URenderQueueSorter extends Comparator<URenderQueue.URenderQueueMeshState> {
	
}
