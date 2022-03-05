package urender.engine.shader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import urender.api.UObjHandle;
import urender.api.UShaderType;
import urender.api.backend.RenderingBackend;
import urender.engine.UGfxEngineObject;
import urender.engine.UGfxEngineObjectType;
import urender.engine.UGfxObject;

/**
 * Shader program resource and interface.
 */
public class UShaderProgram extends UGfxEngineObject {

	private UObjHandle __program = new UObjHandle();

	private Map<String, UniformState> uniforms = new HashMap<>();
	private Map<String, UObjHandle> attributes = new HashMap<>();

	String vertexShaderName;
	String fragmentShaderName;

	/**
	 * Creates a shader program resource from a vertex and fragment shader.
	 *
	 * @param name Local name of the resource.
	 * @param vshName Local name of the vertex shader to be attached.
	 * @param fshName Local name of the fragment shader to be attached.
	 */
	public UShaderProgram(String name, String vshName, String fshName) {
		this.name = name;
		this.vertexShaderName = vshName;
		this.fragmentShaderName = fshName;
	}

	/**
	 * Changes the vertex shader bound to this program. This will request all rendering engines to relink the
	 * program.
	 *
	 * @param vsh An UShader of type VERTEX.
	 */
	public void setVsh(UShader vsh) {
		if (vsh.getShaderType() != UShaderType.VERTEX) {
			throw new IllegalArgumentException("Shader is not a vertex shader!");
		}
		if (!Objects.equals(vsh.getName(), vertexShaderName)) {
			vertexShaderName = vsh.getName();
			forceReupload();
		}
	}

	/**
	 * Changes the fragment shader bound to this program. This will request all rendering engines to relink
	 * the program.
	 *
	 * @param fsh An UShader of type FRAGMENT.
	 */
	public void setFsh(UShader fsh) {
		if (fsh.getShaderType() != UShaderType.FRAGMENT) {
			throw new IllegalArgumentException("Shader is not a fragment shader!");
		}
		if (!Objects.equals(fsh.getName(), fragmentShaderName)) {
			fragmentShaderName = fsh.getName();
			forceReupload();
		}
	}

	/**
	 * Gets the local name of the vertex shader bound to this program.
	 *
	 * @return
	 */
	public String getVshName() {
		return vertexShaderName;
	}

	/**
	 * Gets the local name of the fragment shader bound to this program.
	 *
	 * @return
	 */
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

	/**
	 * Checks if an uniform's data is not current for this program and needs to be re-sent to the GPU.
	 *
	 * @param rnd Rendering backend core.
	 * @param uniform The uniform to check.
	 * @return True if this uniform should be updated.
	 */
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

	/**
	 * Gets a handle to the location of a uniform within the program.
	 *
	 * @param rnd Rendering backend core.
	 * @param name Name of the uniform.
	 * @return A handle of the program uniform, which may be 'invalid' if the uniform was not found.
	 */
	public UObjHandle getUniformLocation(RenderingBackend rnd, String name) {
		UniformState uniform = getUniformState(name);
		return getUniformLocation(rnd, uniform);
	}

	/**
	 * Sets a uniform value for the program.
	 *
	 * @param u An UUniform value, allowed to be invalid.
	 * @param rnd Rendering backend core.
	 */
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

	/**
	 * Gets a handle of the location of a vertex attribute within the program.
	 *
	 * @param rnd Rendering backend core.
	 * @param name Name of the vertex attribute.
	 * @return A handle of the program attribute, which may be 'invalid' if the attribute was not found.
	 */
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

	/**
	 * Requests re-link of the program for all rendering engines.
	 */
	public void forceReupload() {
		__program.forceUploadAll();
		uniforms.clear();
		attributes.clear();
	}

	/**
	 * Readies the program for usage in drawing.
	 *
	 * @param rnd Rendering backend core.
	 * @param shaders List to search for required shader resources.
	 */
	public void setup(RenderingBackend rnd, List<UShader> shaders) {
		if (!__program.isInitialized(rnd)) {
			rnd.programInit(__program);
		}

		UShader vert = UGfxObject.find(shaders, vertexShaderName);
		UShader frag = UGfxObject.find(shaders, fragmentShaderName);

		if (vert != null && frag != null) {
			//ensure that these methods are called to reset the flags
			boolean needsVertRelink = vert.needsProgramRelink(this);
			boolean needsFragRelink = frag.needsProgramRelink(this);
			if (__program.getAndResetForceUpload(rnd) || needsVertRelink || needsFragRelink) {
				rnd.programAttachShader(__program, vert.__shObj);
				rnd.programAttachShader(__program, frag.__shObj);
				rnd.programLink(__program);
			}
		}
	}

	/**
	 * Sets the program as current for all subsequent draw calls.
	 *
	 * @param rnd Rendering backend core.
	 */
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
