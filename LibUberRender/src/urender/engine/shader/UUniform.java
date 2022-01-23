package urender.engine.shader;

import java.lang.reflect.Array;
import urender.api.UObjHandle;
import urender.engine.UGfxRenderer;

public abstract class UUniform<T> {

	protected T[] value;

	String name;

	public UUniform(String name, T... value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public int valueCount() {
		return Array.getLength(array());
	}

	public T get() {
		return get(0);
	}

	public T get(int index) {
		return (T) Array.get(array(), index);
	}
	
	public void set(int index, T value) {
		Array.set(array(), index, value);
	}
	
	public void set(T value) {
		set(0, value);
	}

	protected Object array() {
		return value;
	}

	public abstract UUniformType getUniformType();

	public abstract void setData(UObjHandle location, UGfxRenderer rnd);
}
