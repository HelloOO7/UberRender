package urender.engine.shader;

import urender.api.UObjHandle;
import urender.engine.UGfxRenderer;

public abstract class UUniform {	
	public String name;
	
	public UUniform(String name) {
		this.name = name;
	}
	
	public abstract void setData(UObjHandle location, UGfxRenderer rnd);
}
