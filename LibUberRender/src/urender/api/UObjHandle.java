package urender.api;

import java.util.HashMap;
import java.util.Map;
import urender.api.backend.RenderingBackend;

public class UObjHandle {
	private Map<Integer, Integer> values = new HashMap<>();
	private Map<Integer, Boolean> forceUpload = new HashMap<>();
	
	public void reset() {
		values.clear();
		forceUpload.clear();
	}
	
	public void forceUpload(RenderingBackend backend) {
		forceUpload.put(backend.getIdent().hashCode(), true);
	}
	
	public boolean getAndResetForceUpload(RenderingBackend backend) {
		int hash = backend.getIdent().hashCode();
		boolean r = forceUpload.getOrDefault(hash, true);
		forceUpload.put(hash, false);
		return r;
	}
	
	public void initialize(RenderingBackend backend, int value) {
		values.put(backend.getIdent().hashCode(), value);
	}
	
	public int getValue(RenderingBackend backend) {
		if (!isInitialized(backend)) {
			throw new RuntimeException("Handle not initialized for " + backend + "!");
		}
		return getValueInternal(backend);
	}
	
	private int getValueInternal(RenderingBackend backend) {
		return values.getOrDefault(backend.getIdent().hashCode(), -1);
	}
	
	public boolean isInitialized(RenderingBackend backend) {
		return values.containsKey(backend.getIdent().hashCode());
	}
	
	public boolean isValid(RenderingBackend backend) {
		return getValueInternal(backend) != -1;
	}
	
	public boolean isCurrent(RenderingBackend backend, int current) {
		return getValueInternal(backend) == current;
	}
}
