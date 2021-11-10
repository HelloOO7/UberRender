package urender.engine.shader;

import org.joml.Matrix3f;
import urender.engine.UGfxRenderer;

public class UUniformMatrix3 extends UUniform {

	private Matrix3f val;

	public UUniformMatrix3(String name) {
		this(name, new Matrix3f());
	}
	
	public UUniformMatrix3(String name, Matrix3f value) {
		super(name);
		this.val = value;
	}
	
	public Matrix3f get() {
		return val;
	}
	
	@Override
	public void setData(UShaderProgram prog, UGfxRenderer rnd) {
		rnd.getCore().uniformMat3(prog.getUniformLocation(rnd, name), val);
	}
}
