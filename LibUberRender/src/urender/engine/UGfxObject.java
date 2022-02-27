package urender.engine;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public abstract class UGfxObject {
	protected String name;
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public static <T extends UGfxObject> T find(Collection<T> collection, String name) {
		if (collection == null) {
			return null;
		}
		for (T t : collection) {
			if (Objects.equals(t.name, name)) {
				return t;
			}
		}
		return null;
	}
	
	public static int remove(List<? extends UGfxObject> collection, String name) {
		for (int i = 0; i < collection.size(); i++) {
			if (Objects.equals(collection.get(i).name, name)) {
				collection.remove(i);
				return i;
			}
		}
		return -1;
	}
}
