package urender.scenegraph;

import java.util.List;

/**
 * Interface for parent/child relations between scenegraph nodes.
 */
public interface UParentRelation {

	/**
	 * Gets the parent node.
	 *
	 * @return
	 */
	public USceneNode getParent();

	/**
	 * Gets a collection of all top-level children.
	 *
	 * @return
	 */
	public List<USceneNode> getChildren();

	/**
	 * Gets an arbitrary name of the attachment component of the parent that this relation is relative to.
	 *
	 * @return
	 */
	public String getAttachmentName();

	/**
	 * Gets the transform inheritance flags.
	 *
	 * @return Bitmask of transform inheritance capabilities.
	 */
	public int getTransformInheritance();
}
