package urender.demo.editor;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ListModel;
import javax.swing.SpinnerNumberModel;
import urender.engine.UShadingMethod;
import urender.engine.UMaterial;
import urender.engine.UMaterialDrawLayer;
import urender.engine.UTextureMapper;

public class MaterialEditHandle implements IEditHandle<UMaterial> {

	public final UMaterial material;

	public final MultiComboBoxModel<ShaderProgramEditHandle> programSelect;

	public final SynchronizedComboBoxModel<UTextureMapper, TextureMapperEditHandle> texMapperListModel;
	
	public final DefaultComboBoxModel<UShadingMethod> shadingMethodModel = new DefaultComboBoxModel<>();
	
	public final SpinnerNumberModel shadingPriorityModel = new SpinnerNumberModel(0, 0, 65535, 1);

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
		for (UShadingMethod sm : UShadingMethod.values()) {
			shadingMethodModel.addElement(sm);
		}
		shadingMethodModel.setSelectedItem(material.getDrawLayer().method);
		shadingPriorityModel.setValue(material.getDrawLayer().priority);
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
		material.setDrawLayer(new UMaterialDrawLayer((UShadingMethod)shadingMethodModel.getSelectedItem(), shadingPriorityModel.getNumber().intValue()));
	}

	@Override
	public UMaterial getContent() {
		return material;
	}
}
