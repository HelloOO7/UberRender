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

public class UShaderProgram extends UGfxEngineObject {

	private UObjHandle __program = new UObjHandle();

	private Map<String, UniformState> uniforms = new HashMap<>();
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
	
	private boolean checkNeedUniformUpdate(RenderingBackend rnd, int hashCode, UniformState state) {
		if (state != null) {
			if (state.value.isInitialized(rnd)) {
				return !state.value.isCurrent(rnd, hashCode);
			}
		}
		return true;
	}

	public boolean checkNeedUniformUpdate(RenderingBackend rnd, UUniform uniform) {
		UniformState state = uniforms.get(uniform.getName());
		return checkNeedUniformUpdate(rnd, uniform.hashCode(), state);
	}
	
	private UniformState getUniformState(String name) {
		UniformState uniform = uniforms.get(name);
		if (uniform == null) {
			uniform = new UniformState(name);
			uniforms.put(name, uniform);
		}
		return uniform;
	}
	
	private UObjHandle getUniformLocation(RenderingBackend rnd, UniformState uniform) {
		if (!uniform.handle.isInitialized(rnd)) {
			rnd.uniformLocationInit(__program, uniform.handle, uniform.name);
		}
		return uniform.handle;
	}

	public UObjHandle getUniformLocation(RenderingBackend rnd, String name) {
		UniformState uniform = getUniformState(name);
		return getUniformLocation(rnd, uniform);
	}

	public void setUniform(UUniform u, RenderingBackend rnd) {
		UniformState state = getUniformState(u.getName());
		
		UObjHandle loc = getUniformLocation(rnd, state);
		int hashCode = u.hashCode();
		if (loc.isValid(rnd) && checkNeedUniformUpdate(rnd, hashCode, state)) {
			u.setData(loc, rnd);
			//System.out.println("set uniform " + u.getName());
			state.value.initialize(rnd, hashCode);
		}
	}

	public UObjHandle getAttributeLocation(RenderingBackend rnd, String name) {
		UObjHandle attribute = attributes.get(name);
		if (attribute == null) {
			attribute = new UObjHandle();
			attributes.put(name, attribute);
		}
		if (!attribute.isInitialized(rnd)) {
			rnd.attributeLocationInit(__program, attribute, name);
		}
		return attribute;
	}

	public void setup(RenderingBackend rnd, List<UShader> shaders) {
		if (!__program.isInitialized(rnd)) {
			rnd.programInit(__program);
		}
		if (__program.getAndResetForceUpload(rnd)) {
			UShader vert = UGfxObject.find(shaders, vertexShaderName);
			UShader frag = UGfxObject.find(shaders, fragmentShaderName);
			if (vert != null && frag != null) {
				rnd.programAttachShader(__program, vert.__shObj);
				rnd.programAttachShader(__program, frag.__shObj);
				rnd.programLink(__program);
			}
		}
	}

	public void use(RenderingBackend rnd) {
		rnd.programUse(__program);
	}

	@Override
	public UGfxEngineObjectType getType() {
		return UGfxEngineObjectType.PROGRAM;
	}

	private static class UniformState {
		public final String name;

		public UObjHandle handle = new UObjHandle();
		public UObjHandle value = new UObjHandle();
		
		public UniformState(String name) {
			this.name = name;
		}
	}
}
