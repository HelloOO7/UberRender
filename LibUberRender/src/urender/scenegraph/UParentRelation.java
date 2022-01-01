package urender.scenegraph;

import java.util.List;

public interface UParentRelation {
	public USceneNode getParent();
	public List<USceneNode> getChildren();
	
	public String getAttachmentName();
	public int getTransformInheritance();
}
