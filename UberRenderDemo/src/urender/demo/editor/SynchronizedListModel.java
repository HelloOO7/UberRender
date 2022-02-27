package urender.demo.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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

	public H findHandleByName(String name) {
		for (H h : handles) {
			if (Objects.equals(h.toString(), name)) {
				return h;
			}
		}
		return null;
	}

	@Override
	public H getElementAt(int index) {
		if (index < 0 || index >= handles.size()) {
			return null;
		}
		return handles.get(index);
	}

	public boolean isUnique(E elem) {
		int index = content.indexOf(elem);
		if (index != -1) {
			String name = handles.get(index).toString();
			for (IEditHandle h : handles) {
				if (Objects.equals(h.toString(), name) && h.getContent() != elem) {
					return false;
				}
			}
		}
		return true;
	}

	public void addUnique(int index, E elem) {
		String uid = createHandle(elem).toString();
		if (findHandleByName(uid) == null) {
			add(index, elem);
		}
	}

	public void addUnique(E elem) {
		String uid = createHandle(elem).toString();
		if (findHandleByName(uid) == null) {
			add(elem);
		}
	}
	
	public void addOrReplace(E elem) {
		H newHandle = createHandle(elem);
		H oldHandle = findHandleByName(newHandle.toString());
		if (oldHandle != null) {
			int oldIdx = remove(oldHandle.getContent());
			addHandle(oldIdx, newHandle);
		}
		else {
			addHandle(getSize(), newHandle);
		}
	}

	public void add(E elem) {
		add(content.size(), elem);
	}

	public void add(int index, E elem) {
		if (index == -1) {
			index = content.size();
		}
		addHandle(index, createHandle(elem));
	}
	
	private void addHandle(int index, H handle) {
		content.add(index, handle.getContent());
		handles.add(index, handle);
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

	public int remove(E obj) {
		int idx = indexOf(obj);
		if (idx != -1) {
			remove(idx);
		}
		return idx;
	}

	@Override
	public Iterator<H> iterator() {
		return handles.iterator();
	}
}
