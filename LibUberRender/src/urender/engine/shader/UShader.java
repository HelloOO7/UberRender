package urender.engine.shader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import urender.api.UObjHandle;
import urender.api.UShaderType;
import urender.api.backend.RenderingBackend;
import urender.engine.UGfxEngineObject;
import urender.engine.UGfxEngineObjectType;

/**
 * Vertex/fragment shader source component.
 *
 * Note that this is not a shader program.
 */
public class UShader extends UGfxEngineObject {

	UObjHandle __shObj = new UObjHandle();

	UShaderType type;
	String shaderData;

	Map<UShaderProgram, Boolean> programsRegisteredChange = new HashMap<>();

	/**
	 * Creates a shader resource.
	 *
	 * @param name Local name of the resource.
	 * @param type Type of the shader.
	 * @param source Shader source code in backend-defined format.
	 */
	public UShader(String name, UShaderType type, String source) {
		this.name = name;
		this.type = type;
		this.shaderData = source;
	}
	
	/**
	 * Changes the shader's local name.
	 *
	 * @param newName
	 */
	public void renameTo(String newName) {
		this.name = newName;
	}

	/**
	 * Creates a vertex shader resource.
	 *
	 * @param name Local name of the resource.
	 * @param source Shader source code in backend-defined format.
	 * @return An UShader vertex shader.
	 */
	public static UShader createVertexShader(String name, String source) {
		return new UShader(name, UShaderType.VERTEX, source);
	}

	/**
	 * Creates a fragment shader resource.
	 *
	 * @param name Local name of the resource.
	 * @param source Shader source code in backend-defined format.
	 * @return An UShader fragment shader.
	 */
	public static UShader createFragmentShader(String name, String source) {
		return new UShader(name, UShaderType.FRAGMENT, source);
	}

	/**
	 * Gets the type of this shader.
	 *
	 * @return Whether the shader is a vertex or fragment shader.
	 */
	public UShaderType getShaderType() {
		return type;
	}

	/**
	 * Gets the plain-text source code of the shader.
	 *
	 * @return Shader source code in backend-defined format.
	 */
	public String getShaderData() {
		return shaderData;
	}

	/**
	 * Replaces the shader's source code. This will notify all renderers to reupload the shader data.
	 *
	 * @param shaderData New source code for the shader.
	 */
	public void setShaderData(String shaderData) {
		this.shaderData = shaderData;
		__shObj.forceUploadAll();
		programsRegisteredChange.clear(); //all programs will be relinked
	}

	/**
	 * Readies the shader for usage in programs.
	 *
	 * @param rnd Rendering backend core.
	 */
	public void setup(RenderingBackend rnd) {
		if (!__shObj.isInitialized(rnd)) {
			rnd.shaderInit(__shObj, type);
		}
		if (__shObj.getAndResetForceUpload(rnd)) {
			rnd.shaderCompileSource(__shObj, shaderData);
		}
	}

	public void delete(RenderingBackend rnd) {
		if (__shObj.isValid(rnd)) {
			rnd.shaderDelete(__shObj);
		}
	}
	
	public static void deleteAll(RenderingBackend rnd, Iterable<UShader> shaders) {
		List<UObjHandle> handles = new ArrayList<>();
		for (UShader shader : shaders) {
			if (shader.__shObj.isValid(rnd)) {
				handles.add(shader.__shObj);
			}
		}
		rnd.shaderDelete(handles.toArray(new UObjHandle[handles.size()]));
	}

	/**
	 * Checks if a change in the shader object is pending for a program.
	 *
	 * @param program The program to check for.
	 * @return True if the program should be relinked to account for shader modifications.
	 */
	public boolean needsProgramRelink(UShaderProgram program) {
		boolean value = programsRegisteredChange.getOrDefault(program, true);
		if (value) {
			programsRegisteredChange.put(program, false);
		}
		return value;
	}

	@Override
	public UGfxEngineObjectType getType() {
		return UGfxEngineObjectType.SHADER;
	}
}
