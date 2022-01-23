package urender.demo.editor.uniform;

import urender.engine.shader.UUniform;

public interface IUniformEditor<T, U extends UUniform<T>> {
	public void load(U uniform);
	public void save(U uniform);
}
