package urender.engine.shader;

import java.util.ArrayList;
import java.util.Collection;
import urender.api.backend.RenderingBackend;

public class UUniformList extends ArrayList<UUniform> {

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
	
	public boolean valuesMatch(UUniformList other) {
		return this.containsAll(other) && other.containsAll(this);
	}
}
