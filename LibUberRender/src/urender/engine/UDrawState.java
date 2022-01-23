package urender.engine;

import urender.engine.shader.UShaderProgram;
import urender.engine.shader.UUniformList;

public class UDrawState {
	public UUniformList currentUniformSet = new UUniformList();
	
	public UMaterial currentMaterial;
	public UShaderProgram currentShader;
}
