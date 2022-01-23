package urender.demo.editor;

import javax.swing.JComboBox;
import javax.swing.ListModel;
import urender.engine.UMaterial;
import urender.engine.UTextureMapper;

public class MaterialEditHandle implements IEditHandle<UMaterial> {

	public final UMaterial material;

	public final MultiComboBoxModel<ShaderProgramEditHandle> programSelect;

	public final SynchronizedComboBoxModel<UTextureMapper, TextureMapperEditHandle> texMapperListModel;

	public MaterialEditHandle(UMaterial material, ListModel<ShaderProgramEditHandle> programList, ListModel<TextureEditHandle> textureListModel) {
		this.material = material;
		programSelect = new MultiComboBoxModel<>(programList);
		programSelect.setSelectedItemByName(material.getShaderProgramName());
		texMapperListModel = new SynchronizedComboBoxModel<UTextureMapper, TextureMapperEditHandle>(material.getTextureMappers()) {
			@Override
			public TextureMapperEditHandle createHandle(UTextureMapper element) {
				return new TextureMapperEditHandle(element, textureListModel, this);
			}
		};
	}

	@Override
	public String toString() {
		return material.getName();
	}

	@Override
	public void save() {
		if (programSelect.getSelectedItem() != null) {
			material.bindShaderProgram(programSelect.getSelectedItem().getContent());
		} else {
			material.bindShaderProgram(null);
		}
	}

	@Override
	public UMaterial getContent() {
		return material;
	}
}
