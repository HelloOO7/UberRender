package urender.demo.editor;

import urender.demo.editor.uniform.FloatUniformEditor;
import urender.demo.editor.uniform.IUniformEditor;
import urender.demo.editor.uniform.IntUniformEditor;
import urender.demo.editor.uniform.Vector2UniformEditor;
import urender.demo.editor.uniform.Vector3UniformEditor;
import urender.demo.editor.uniform.Vector4UniformEditor;
import urender.engine.shader.UUniform;

public class UniformEditHandle implements IEditHandle<UUniform> {

	private IUniformEditor editor;
	
	public final UUniform uniform;

	public UniformEditHandle(UUniform uniform) {
		this.uniform = uniform;
		
		switch (uniform.getUniformType()) {
			case FLOAT:
				editor = new FloatUniformEditor();
				break;
			case INT:
				editor = new IntUniformEditor();
				break;
			case VEC2:
				editor = new Vector2UniformEditor();
				break;
			case VEC3:
				editor = new Vector3UniformEditor();
				break;
			case VEC4:
				editor = new Vector4UniformEditor();
				break;
		}
		
		editor.load(uniform);
	}

	public IUniformEditor getEditor() {
		return editor;
	}
	
	@Override
	public String toString() {
		return uniform.getName();
	}

	@Override
	public void save() {
		editor.save(uniform);
	}

	@Override
	public UUniform getContent() {
		return uniform;
	}
}
