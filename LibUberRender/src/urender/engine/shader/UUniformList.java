package urender.engine.shader;

import java.util.ArrayList;
import java.util.Collection;
import urender.api.backend.RenderingBackend;

/**
 * Collection of shader program uniforms.
 */
public class UUniformList extends ArrayList<UUniform> {

	/**
	 * Synchronizes all uniforms in this collection with a program's GPU state, if needed.
	 *
	 * @param prog The program to target.
	 * @param rnd Rendering backend core.
	 */
	public void setup(UShaderProgram prog, RenderingBackend rnd) {
		for (UUniform u : this) {
			prog.setUniform(u, rnd);
		}
	}

	@Override
	public boolean addAll(Collection<? extends UUniform> uniforms) {
		boolean retval = false;
		for (UUniform u : uniforms) {
			retval |= add(u);
		}
		return retval;
	}

	@Override
	public boolean add(UUniform uniform) {
		if (!contains(uniform)) {
			return super.add(uniform);
		}
		return false;
	}

	/**
	 * Checks if the uniform list contains all uniforms in another, and vice versa.
	 *
	 * @param other The other uniform list.
	 * @return True if all of 'other''s elements are present in this list, and vice versa.
	 */
	public boolean valuesMatch(UUniformList other) {
		return this.containsAll(other) && other.containsAll(this);
	}
}
