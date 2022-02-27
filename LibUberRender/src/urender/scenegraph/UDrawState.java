package urender.scenegraph;

import urender.engine.UMaterial;
import urender.engine.shader.UShaderProgram;
import urender.engine.shader.UUniformList;

public class UDrawState {
	public UUniformList currentUniformSet = new UUniformList();
	public UUniformList sceneUniformTemp = new UUniformList();
	
	public UMaterial currentMaterial;
	public UShaderProgram currentShader;
}
