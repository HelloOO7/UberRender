package urender.engine.shader;

import org.joml.Matrix4f;
import urender.api.UObjHandle;
import urender.engine.UGfxRenderer;

public class UUniformMatrix4 extends UUniform {

	private Matrix4f val;

	public UUniformMatrix4(String name) {
		this(name, new Matrix4f());
	}
	
	public UUniformMatrix4(String name, Matrix4f value) {
		super(name);
		this.val = value;
	}
	
	public Matrix4f get() {
		return val;
	}
	
	@Override
	public void setData(UObjHandle loc, UGfxRenderer rnd) {
		rnd.getCore().uniformMat4(loc, val);
	}
}
