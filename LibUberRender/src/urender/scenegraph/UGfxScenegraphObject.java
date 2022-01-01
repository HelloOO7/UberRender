
package urender.scenegraph;

import urender.engine.UGfxObject;

public abstract class UGfxScenegraphObject extends UGfxObject {
	public void setName(String name) {
		this.name = name;
	}
	
	public abstract UGfxScenegraphObjectType getType();
}
