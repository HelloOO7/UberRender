package urender.engine;

import java.util.Collection;
import java.util.Objects;

public abstract class UGfxObject {
	protected String name;
	
	public String getName() {
		return name;
	}
	
	public static <T extends UGfxObject> T find(Collection<T> collection, String name) {
		for (T t : collection) {
			if (Objects.equals(t.name, name)) {
				return t;
			}
		}
		return null;
	}
}
