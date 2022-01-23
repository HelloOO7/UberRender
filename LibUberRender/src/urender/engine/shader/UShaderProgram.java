package urender.engine.shader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import urender.api.UObjHandle;
import urender.api.UShaderType;
import urender.api.backend.RenderingBackend;
import urender.engine.UGfxEngineObject;
import urender.engine.UGfxEngineObjectType;
import urender.engine.UGfxObject;
import urender.engine.UGfxRenderer;

public class UShaderProgram extends UGfxEngineObject {

	private UObjHandle __program = new UObjHandle();

	private Map<String, UObjHandle> uniforms = new HashMap<>();
	private Map<String, UObjHandle> attributes = new HashMap<>();

	String vertexShaderName;
	String fragmentShaderName;

	public UShaderProgram(String name, String vshName, String fshName) {
		this.name = name;
		this.vertexShaderName = vshName;
		this.fragmentShaderName = fshName;
	}

	public void setVsh(UShader vsh) {
		if (vsh.getShaderType() != UShaderType.VERTEX) {
			throw new IllegalArgumentException("Shader is not a vertex shader!");
		}
		vertexShaderName = vsh.getName();
	}

	public void setFsh(UShader fsh) {
		if (fsh.getShaderType() != UShaderType.FRAGMENT) {
			throw new IllegalArgumentException("Shader is not a fragment shader!");
		}
		fragmentShaderName = fsh.getName();
	}

	public String getVshName() {
		return vertexShaderName;
	}

	public String getFshName() {
		return fragmentShaderName;
	}

	public UObjHandle getUniformLocation(UGfxRenderer rnd, String name) {
		UObjHandle uniform = uniforms.get(name);
		if (uniform == null) {
			uniform = new UObjHandle();
			uniforms.put(name, uniform);
		}
		if (!uniform.isInitialized(rnd.getCore())) {
			rnd.getCore().uniformLocationInit(__program, uniform, name);
		}
		return uniform;
	}

	public UObjHandle getAttributeLocation(UGfxRenderer rnd, String name) {
		UObjHandle attribute = attributes.get(name);
		if (attribute == null) {
			attribute = new UObjHandle();
			attributes.put(name, attribute);
		}
		if (!attribute.isInitialized(rnd.getCore())) {
			rnd.getCore().attributeLocationInit(__program, attribute, name);
		}
		return attribute;
	}

	public void setup(UGfxRenderer rnd, List<UShader> shaders) {
		RenderingBackend core = rnd.getCore();
		if (!__program.isInitialized(core)) {
			core.programInit(__program);
		}
		if (__program.getAndResetForceUpload(core)) {
			UShader vert = UGfxObject.find(shaders, vertexShaderName);
			UShader frag = UGfxObject.find(shaders, fragmentShaderName);
			if (vert != null && frag != null) {
				core.programAttachShader(__program, vert.__shObj);
				core.programAttachShader(__program, frag.__shObj);
				core.programLink(__program);
			}
		}
	}

	public void use(UGfxRenderer rnd) {
		rnd.getCore().programUse(__program);
	}

	@Override
	public UGfxEngineObjectType getType() {
		return UGfxEngineObjectType.PROGRAM;
	}
}
