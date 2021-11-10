package urender.engine;

import java.util.Collection;
import java.util.Objects;

public class UGfxObject {
	public String name;
	
	public static <T extends UGfxObject> T find(Collection<T> collection, String name) {
		for (T t : collection) {
			if (Objects.equals(t.name, name)) {
				return t;
			}
		}
		return null;
	}
}
