package urender.demo.editor;

import javax.swing.ListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import urender.engine.UTextureMapper;

public class TextureMapperEditHandle implements IEditHandle<UTextureMapper> {

	private final SynchronizedComboBoxModel<UTextureMapper, TextureMapperEditHandle> parentBoxModel;
	
	public final TextureMapperEditor editor;

	public final UTextureMapper mapper;

	public final MultiComboBoxModel<TextureEditHandle> textureSelect;

	private final DocumentListener docListener = new DocumentListener() {
		@Override
		public void insertUpdate(DocumentEvent e) {
			changedUpdate(e);
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			changedUpdate(e);
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			mapper.setShaderVariableName(editor.getNowSamplerName());
			parentBoxModel.fireUpdatedHandle(TextureMapperEditHandle.this);
		}
	};

	public TextureMapperEditHandle(UTextureMapper mapper, ListModel<TextureEditHandle> textureList, SynchronizedComboBoxModel<UTextureMapper, TextureMapperEditHandle> parentBoxModel) {
		this.mapper = mapper;
		this.parentBoxModel = parentBoxModel;
		textureSelect = new MultiComboBoxModel<>(textureList);
		textureSelect.setSelectedItemByName(mapper.getTextureName());
		editor = new TextureMapperEditor();
		editor.load(this);
		editor.getNameEditField().getDocument().addDocumentListener(docListener);
	}

	@Override
	public void onRemoved() {
		editor.getNameEditField().getDocument().removeDocumentListener(docListener);
	}

	@Override
	public String toString() {
		return mapper.getShaderVariableName();
	}

	@Override
	public void save() {
		editor.save(this);
	}

	@Override
	public UTextureMapper getContent() {
		return mapper;
	}
}
