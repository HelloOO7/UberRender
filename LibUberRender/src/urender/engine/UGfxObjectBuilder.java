package urender.engine;

import urender.common.IBuilder;

/**
 * Common base class for UGfxObject builder patterns.
 *
 * @param <O> Type of the UGfxObject class.
 */
public abstract class UGfxObjectBuilder<O extends UGfxObject> implements IBuilder<O> {

	/**
	 * Sets the local name of the resource.
	 *
	 * @param name
	 * @return this
	 */
	public UGfxObjectBuilder<O> setName(String name) {
		getObject().name = name;
		return this;
	}

	/**
	 * Gets the currently set local name of the resource.
	 *
	 * @return
	 */
	public String getName() {
		return getObject().getName();
	}

	protected abstract O getObject();
}
