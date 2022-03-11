package urender.engine;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Named engine object base class.
 */
public abstract class UGfxObject {

	protected String name;

	/**
	 * Gets the local name of the object.
	 *
	 * @return The object's local name.
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Finds an UGfxObject in a collection by name.
	 *
	 * @param <T> Object class.
	 * @param collection Collection to search.
	 * @param name Name of the object to search for.
	 * @return An object with a matching name, or null if none was found.
	 */
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

	/**
	 * Removes a named object from a collection.
	 *
	 * @param collection Collection to remove the object from.
	 * @param name Name of the object to remove.
	 * @return Index of the object before it was removed, or -1 if no matching object was found.
	 */
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
