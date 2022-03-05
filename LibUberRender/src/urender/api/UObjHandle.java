package urender.api;

import java.util.HashMap;
import java.util.Map;
import urender.api.backend.RenderingBackend;

/**
 * Name handle for an abstract GPU object that can be shared between backends and contexts.
 */
public class UObjHandle {

	private Map<Integer, Integer> values = new HashMap<>();
	private Map<Integer, Boolean> forceUpload = new HashMap<>();

	/**
	 * Discards all saved handles.
	 */
	public void reset() {
		values.clear();
		forceUpload.clear();
	}

	/**
	 * Sets a flag that notifies that a rendering engine should reupload any data used by the handle.
	 *
	 * @param backend A rendering backend core.
	 */
	public void forceUpload(RenderingBackend backend) {
		forceUpload.put(backend.getIdent().hashCode(), true);
	}

	/**
	 * Sets a flag that notifies that all rendering engines should reupload any data used by the handle.
	 */
	public void forceUploadAll() {
		for (Map.Entry<Integer, Boolean> e : forceUpload.entrySet()) {
			e.setValue(true);
		}
	}

	/**
	 * Used by a rendering engine to request the state of the data reupload flag. Once the state is retrieved,
	 * it is reset to 'false'.
	 *
	 * @param backend A rendering backend core.
	 * @return
	 */
	public boolean getAndResetForceUpload(RenderingBackend backend) {
		int hash = backend.getIdent().hashCode();
		boolean r = forceUpload.getOrDefault(hash, true);
		forceUpload.put(hash, false);
		return r;
	}

	/**
	 * Initializes the handle with an implementation-defined value bound to a rendering backend.
	 *
	 * @param backend A rendering backend core.
	 * @param value The name/pointer to the core handle.
	 */
	public void initialize(RenderingBackend backend, int value) {
		values.put(backend.getIdent().hashCode(), value);
	}

	/**
	 * Gets the value of this handle that was previously bound to a rendering backend.
	 *
	 * @param backend A rendering backend core.
	 * @return The name/pointer to the core handle.
	 * @throws RuntimeException If the handle was not initialized for the specified backend.
	 */
	public int getValue(RenderingBackend backend) {
		if (!isInitialized(backend)) {
			throw new RuntimeException("Handle not initialized for " + backend + "!");
		}
		return getValueInternal(backend);
	}

	private int getValueInternal(RenderingBackend backend) {
		return values.getOrDefault(backend.getIdent().hashCode(), -1);
	}

	/**
	 * Checks if there is a core handle initialized for a backend.
	 *
	 * @param backend A rendering backend core.
	 * @return True if a core handle is present, false if otherwise.
	 */
	public boolean isInitialized(RenderingBackend backend) {
		return values.containsKey(backend.getIdent().hashCode());
	}

	/**
	 * Checks if there is a core handle initialized for a backend, and that the value is valid (not equal to
	 * -1).
	 *
	 * @param backend A rendering backend core.
	 * @return True if a core handle is present and valid, false if otherwise.
	 */
	public boolean isValid(RenderingBackend backend) {
		return getValueInternal(backend) != -1;
	}

	/**
	 * Checks if the current registered core handle for a backend matches a value.
	 *
	 * @param backend A rendering backend core.
	 * @param current The value to check against.
	 * @return True if there is a valid core handle that is equal to 'current', false if otherwise.
	 */
	public boolean isCurrent(RenderingBackend backend, int current) {
		if (!isInitialized(backend)) {
			return false;
		}
		return getValueInternal(backend) == current;
	}
}
