package urender.engine;

import urender.common.IBuilder;

public abstract class UGfxObjectBuilder<O extends UGfxObject> implements IBuilder<O> {
	public UGfxObjectBuilder<O> setName(String name) {
		getObject().name = name;
		return this;
	}
	
	public String getName() {
		return getObject().getName();
	}
	
	protected abstract O getObject();
	
	public abstract void reset();
	
	public abstract O build();
}
