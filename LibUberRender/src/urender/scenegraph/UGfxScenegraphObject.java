package urender.scenegraph;

import urender.engine.UGfxObject;

/**
 * Base class for URender engine objects.
 */
public abstract class UGfxScenegraphObject extends UGfxObject {

	/**
	 * Sets the local name of the object.
	 *
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the non-abstract type of the object.
	 *
	 * @return A UGfxScenegraphObjectType constant whose value guarantees safe type casting.
	 */
	public abstract UGfxScenegraphObjectType getType();
}
