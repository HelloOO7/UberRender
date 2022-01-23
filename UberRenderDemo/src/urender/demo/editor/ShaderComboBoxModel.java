package urender.demo.editor;

import javax.swing.ListModel;
import urender.api.UShaderType;

public class ShaderComboBoxModel extends MultiComboBoxModel<ShaderEditHandle> {

	private final UShaderType type;
	
	public ShaderComboBoxModel(ListModel<ShaderEditHandle> sharedDataModel, UShaderType type) {
		super(sharedDataModel);
		this.type = type;
	}

	@Override
	public int getSize() {
		int count = 0;
		for (int i = 0; i < dataModel.getSize(); i++) {
			if (dataModel.getElementAt(i).shader.getShaderType() == type) {
				count++;
			}
		}
		return count;
	}

	@Override
	public ShaderEditHandle getElementAt(int index) {
		int nowIdx = 0;
		for (int i = 0; i < dataModel.getSize(); i++) {
			ShaderEditHandle e = dataModel.getElementAt(i);
			if (e.shader.getShaderType() == type) {
				if (nowIdx == index) {
					return e;
				}
				else {
					nowIdx++;
				}
			}
		}
		throw new ArrayIndexOutOfBoundsException(index);
	}
}
