package urender.scenegraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Default UParentRelation implementation with read/write fields for all properties.
 */
public class UDefaultParentRelation implements UParentRelation {

	/**
	 * Parent node.
	 */
	public USceneNode parent;
	/**
	 * Child nodes.
	 */
	public final List<USceneNode> children = new ArrayList<>();

	/**
	 * Not used yet.
	 */
	public String attachmentName = null;

	/**
	 * Inheritance flags.
	 */
	public int inheritance = UTransformInheritance.INHERIT_ALL;

	@Override
	public USceneNode getParent() {
		return parent;
	}

	@Override
	public List<USceneNode> getChildren() {
		return children;
	}

	@Override
	public String getAttachmentName() {
		return attachmentName;
	}

	@Override
	public int getTransformInheritance() {
		return inheritance;
	}

}
