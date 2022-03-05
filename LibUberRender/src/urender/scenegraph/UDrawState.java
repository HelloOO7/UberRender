package urender.scenegraph;

import urender.engine.UMaterial;
import urender.engine.shader.UShaderProgram;
import urender.engine.shader.UUniformList;

/**
 * High-level rendering engine state. For internal usage.
 */
public class UDrawState {

	public UUniformList currentUniformSet = new UUniformList();
	public UUniformList commonUniforms = new UUniformList();

	public UMaterial currentMaterial;
	public UShaderProgram currentShader;
}
