package urender.engine.shader;

import java.util.ArrayList;
import urender.engine.UGfxRenderer;

public class UUniformList extends ArrayList<UUniform> {
	public void setup(UShaderProgram prog, UGfxRenderer rnd) {
		for (UUniform u : this) {
			u.setData(prog, rnd);
		}
	}
	
	@Override
	public boolean add(UUniform uniform) {
		if (!contains(uniform)) {
			return super.add(uniform);
		}
		return false;
	}
}
