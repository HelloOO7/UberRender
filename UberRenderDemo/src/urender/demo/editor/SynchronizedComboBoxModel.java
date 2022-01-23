package urender.demo.editor;

import java.util.List;
import javax.swing.ComboBoxModel;

public abstract class SynchronizedComboBoxModel<E, H extends IEditHandle<E>> extends SynchronizedListModel<E, H> implements ComboBoxModel<H> {

	private H selectedObject;

	public SynchronizedComboBoxModel(List<E> subList) {
		super(subList);
	}

	@Override
	public void setSelectedItem(Object anItem) {
		selectedObject = (H) anItem;
	}

	@Override
	public H getSelectedItem() {
		return selectedObject;
	}

}
