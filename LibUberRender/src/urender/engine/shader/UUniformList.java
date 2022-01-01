package urender.engine.shader;

import java.util.ArrayList;
import java.util.Collection;
import urender.api.UObjHandle;
import urender.engine.UGfxRenderer;

public class UUniformList extends ArrayList<UUniform> {

	public void setup(UShaderProgram prog, UGfxRenderer rnd) {
		for (UUniform u : this) {
			UObjHandle loc = prog.getUniformLocation(rnd, u.name);
			if (loc.isInitialized(rnd.getCore())) {
				u.setData(loc, rnd);
			}
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
}
