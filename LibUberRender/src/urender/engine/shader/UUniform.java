package urender.engine.shader;

import urender.engine.UGfxRenderer;

public abstract class UUniform {	
	public String name;
	
	public UUniform(String name) {
		this.name = name;
	}
	
	public abstract void setData(UShaderProgram program, UGfxRenderer rnd);
}
