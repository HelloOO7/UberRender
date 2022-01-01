package urender.engine.shader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import urender.api.UObjHandle;
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
		UShader vert = UGfxObject.find(shaders, vertexShaderName);
		UShader frag = UGfxObject.find(shaders, fragmentShaderName);
		if (vert != null && frag != null) {
			RenderingBackend core = rnd.getCore();
			core.programInit(__program);
			core.programAttachShader(__program, vert.__shObj);
			core.programAttachShader(__program, frag.__shObj);
			core.programLink(__program);
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
