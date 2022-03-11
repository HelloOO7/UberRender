package urender.common.math;

public abstract class MathUnit<M> {

	private final MathUnitHandle[] cache;

	public MathUnit(int cacheSize) {
		cache = new MathUnitHandle[cacheSize];
		for (int i = 0; i < cache.length; i++) {
			cache[i] = new MathUnitHandle(createMember());
		}
	}
	
	protected abstract M createMember();
	
	public M malloc() {
		for (MathUnitHandle hnd : cache) {
			if (!hnd.exists) {
				hnd.exists = true;
				return (M) hnd.object;
			}
		}
		throw new OutOfMemoryError("MathUnit allocation capacity exceeded!!");
	}
	
	public void free(M m) {
		for (MathUnitHandle hnd : cache) {
			if (hnd.object == m) {
				hnd.exists = false;
				break;
			}
		}
	}

	private static class MathUnitHandle<T> {

		public boolean exists;

		public final T object;

		public MathUnitHandle(T object) {
			this.object = object;
		}
	}
}
