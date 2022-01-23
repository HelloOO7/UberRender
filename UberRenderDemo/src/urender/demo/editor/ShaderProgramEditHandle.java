package urender.demo.editor;

import javax.swing.ListModel;
import urender.api.UShaderType;
import urender.engine.shader.UShaderProgram;

public class ShaderProgramEditHandle implements IEditHandle<UShaderProgram> {

	public final UShaderProgram program;

	public final ShaderComboBoxModel shaderSelectV;
	public final ShaderComboBoxModel shaderSelectF;

	public ShaderProgramEditHandle(UShaderProgram program, ListModel<ShaderEditHandle> shaderList) {
		this.program = program;
		shaderSelectV = new ShaderComboBoxModel(shaderList, UShaderType.VERTEX);
		shaderSelectF = new ShaderComboBoxModel(shaderList, UShaderType.FRAGMENT);
		shaderSelectV.setSelectedItemByName(program.getVshName());
		shaderSelectF.setSelectedItemByName(program.getFshName());
	}

	@Override
	public String toString() {
		return program.getName();
	}

	@Override
	public void save() {
		if (shaderSelectV.getSelectedItem() != null) {
			program.setVsh(shaderSelectV.getSelectedItem().shader);
		}
		if (shaderSelectF.getSelectedItem() != null) {
			program.setFsh(shaderSelectF.getSelectedItem().shader);
		}
	}

	@Override
	public UShaderProgram getContent() {
		return program;
	}
}
