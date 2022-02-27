package urender.engine.shader;

import java.util.Arrays;
import java.util.Objects;
import urender.api.UObjHandle;
import urender.api.backend.RenderingBackend;
import urender.engine.UGfxObject;

public abstract class UUniform<T> extends UGfxObject {

	protected T[] value;

	public UUniform(String name, T... value) {
		this.name = name;
		this.value = value;
	}

	public int valueCount() {
		return value.length;
	}

	public T get() {
		return get(0);
	}

	public T get(int index) {
		return value[index];
	}

	public void set(int index, T value) {
		this.value[index] = value;
	}

	public void set(T value) {
		set(0, value);
	}

	public void setData(UObjHandle loc, RenderingBackend rnd) {
		if (valueCount() != 0) {
			setDataImpl(loc, rnd);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}
		final UUniform<?> other = (UUniform<?>) obj;
		return Objects.equals(name, other.name) && Arrays.deepEquals(this.value, other.value);
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 31 * hash + Arrays.deepHashCode(this.value);
		hash = 31 * hash + Objects.hash(this.name);
		return hash;
	}

	protected abstract void setDataImpl(UObjHandle loc, RenderingBackend rnd);

	public abstract UUniformType getUniformType();
}
