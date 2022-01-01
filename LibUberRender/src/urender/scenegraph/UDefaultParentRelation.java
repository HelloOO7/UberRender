package urender.scenegraph;

import java.util.ArrayList;
import java.util.List;

public class UDefaultParentRelation implements UParentRelation {

	public USceneNode parent;
	public final List<USceneNode> children = new ArrayList<>();
	
	public String attachmentName = null;
	
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
