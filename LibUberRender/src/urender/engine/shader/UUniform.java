package urender.engine.shader;

import java.util.Arrays;
import java.util.Objects;
import urender.api.UObjHandle;
import urender.api.backend.RenderingBackend;
import urender.engine.UGfxObject;

/**
 * Shader program uniform value.
 * @param <T> Class of the underlying values.
 */
public abstract class UUniform<T> extends UGfxObject {

	protected T[] value;

	/**
	 * Creates a new uniform value object.
	 *
	 * @param name Name of the uniform in shader programs.
	 * @param value Value element array.
	 */
	public UUniform(String name, T... value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Gets the length of the value element array.
	 *
	 * @return
	 */
	public int valueCount() {
		return value.length;
	}

	/**
	 * Gets the first value element of the uniform.
	 *
	 * @return
	 */
	public T get() {
		return get(0);
	}

	/**
	 * Gets a value element of the uniform.
	 *
	 * @param index Index of the element within the value array.
	 * @return
	 */
	public T get(int index) {
		return value[index];
	}

	/**
	 * Sets a value element of the uniform.
	 *
	 * @param index Index of the element within the value array.
	 * @param value New value of the element.
	 */
	public void set(int index, T value) {
		this.value[index] = value;
	}

	/**
	 * Sets the first element of the value array.
	 *
	 * @param value The new value of the element.
	 */
	public void set(T value) {
		set(0, value);
	}

	/**
	 * Sends the uniform data to the GPU through a rendering backend.
	 *
	 * @param loc Handle of the destination uniform's location.
	 * @param rnd Rendering backend core.
	 */
	public void setData(UObjHandle loc, RenderingBackend rnd) {
		if (valueCount() != 0) {
			setDataImpl(loc, rnd);
		}
	}

	/**
	 * Checks if the uniform matches another name and value-wise.
	 *
	 * @param obj An object to compare to.
	 * @return
	 */
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

	/**
	 * Calculates a hash code comprised of the uniform's name and values.
	 *
	 * @return
	 */
	@Override
	public int hashCode() {
		int hash = 5;
		hash = 31 * hash + Arrays.deepHashCode(this.value);
		hash = 31 * hash + Objects.hash(this.name);
		return hash;
	}

	protected abstract void setDataImpl(UObjHandle loc, RenderingBackend rnd);

	/**
	 * Gets the non-abstract type of the uniform.
	 *
	 * @return A UUniformType constant whose value guarantees safe type casting.
	 */
	public abstract UUniformType getUniformType();
}
