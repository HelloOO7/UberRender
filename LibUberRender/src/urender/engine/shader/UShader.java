package urender.engine.shader;

import urender.api.UObjHandle;
import urender.api.UShaderType;
import urender.api.backend.RenderingBackend;
import urender.engine.UGfxEngineObject;
import urender.engine.UGfxEngineObjectType;
import urender.engine.UGfxRenderer;

public class UShader extends UGfxEngineObject {

	UObjHandle __shObj = new UObjHandle();

	UShaderType type;
	String shaderData;

	public UShader(String name, UShaderType type, String source) {
		this.name = name;
		this.type = type;
		this.shaderData = source;
	}

	public static UShader createVertexShader(String name, String source) {
		return new UShader(name, UShaderType.VERTEX, source);
	}

	public static UShader createFragmentShader(String name, String source) {
		return new UShader(name, UShaderType.FRAGMENT, source);
	}

	public UShaderType getShaderType() {
		return type;
	}

	public String getShaderData() {
		return shaderData;
	}

	public void setShaderData(String shaderData) {
		this.shaderData = shaderData;
	}

	public void setup(UGfxRenderer rnd) {
		RenderingBackend core = rnd.getCore();
		if (!__shObj.isInitialized(core)) {
			core.shaderInit(__shObj, type);
		}
		if (__shObj.getAndResetForceUpload(core)) {
			core.shaderCompileSource(__shObj, shaderData);
		}
	}

	@Override
	public UGfxEngineObjectType getType() {
		return UGfxEngineObjectType.SHADER;
	}
}
