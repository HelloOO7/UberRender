package urender.demo.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractListModel;

public abstract class SynchronizedListModel<E, H extends IEditHandle<E>> extends AbstractListModel<H> implements Iterable<H> {

	private List<E> content;
	private List<H> handles;
	
	public SynchronizedListModel(List<E> subList) {
		setList(subList);
	}
	
	public final void setList(List<E> list) {
		int lastSize = this.content == null ? 0 : this.content.size();
		this.content = list;
		handles = new ArrayList<>(content.size());
		for (E elem : content) {
			handles.add(createHandle(elem));
		}
		fireContentsChanged(this, 0, lastSize - 1);
		fireIntervalAdded(this, lastSize, list.size());
	}
	
	public abstract H createHandle(E element);
	
	@Override
	public int getSize() {
		return handles.size();
	}

	@Override
	public H getElementAt(int index) {
		return handles.get(index);
	}
	
	public void add(E elem) {
		add(content.size(), elem);
	}
	
	public void add(int index, E elem) {
		content.add(index, elem);
		handles.add(index, createHandle(elem));
		fireIntervalAdded(this, index, index);
	}
	
	public void remove(int index) {
		content.remove(index);
		handles.remove(index);
		fireIntervalRemoved(this, index, index);
	}
	
	public void fireUpdatedHandle(H handle) {
		int idx = handles.indexOf(handle);
		if (idx != -1) {
			fireContentsChanged(this, idx, idx);
		}
	}
	
	public int indexOf(E obj) {
		return content.indexOf(obj);
	}
	
	public void remove(E obj) {
		int idx = indexOf(obj);
		remove(idx);
	}
	
	@Override
	public Iterator<H> iterator() {
		return handles.iterator();
	}
}
