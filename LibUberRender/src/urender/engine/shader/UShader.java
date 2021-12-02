package urender.engine.shader;

import urender.api.UObjHandle;
import urender.api.UShaderType;
import urender.api.backend.RenderingBackend;
import urender.engine.UGfxObject;
import urender.engine.UGfxObjectType;
import urender.engine.UGfxRenderer;

public class UShader extends UGfxObject {
	UObjHandle __shObj = new UObjHandle();
	
	public UShaderType type;
	public String shaderData;
	
	public void setup(UGfxRenderer rnd) {
		RenderingBackend core = rnd.getCore();
		core.shaderInit(__shObj, type);
		core.shaderCompileSource(__shObj, shaderData);
	}

	@Override
	public UGfxObjectType getType() {
		return UGfxObjectType.SHADER;
	}
}
