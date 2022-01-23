package urender.demo.editor;

import javax.swing.ComboBoxModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

public class MultiComboBoxModel<E> implements ComboBoxModel<E> {

	protected final ListModel<E> dataModel;

	private E selectedObject;

	public MultiComboBoxModel(ListModel<E> sharedDataModel) {
		this.dataModel = sharedDataModel;
	}

	@Override
	public void setSelectedItem(Object anItem) {
		selectedObject = (E) anItem;
	}

	public void setSelectedItemByName(String name) {
		for (int i = 0; i < getSize(); i++) {
			E elem = getElementAt(i);
			if (elem.toString().equals(name)) {
				setSelectedItem(elem);
				return;
			}
		}
		if (name != null) {
			System.out.println("Notfound item by name " + name);
		}
		setSelectedItem(null);
	}

	@Override
	public E getSelectedItem() {
		return selectedObject;
	}

	@Override
	public int getSize() {
		return dataModel.getSize();
	}

	@Override
	public E getElementAt(int index) {
		return dataModel.getElementAt(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		dataModel.addListDataListener(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		dataModel.removeListDataListener(l);
	}
}
