package urender.g3dio.ugfx.serializers;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * IGfxEnumSerializer implementation using lookup tables.
 *
 * @param <E> Type of the enum that can be serialized.
 */
public abstract class AbstractGfxEnumSerializer<E extends Enum> implements IGfxEnumSerializer<E> {

	/**
	 * Gets an array of all constants of the enum class that is to be used as index-element lookup.
	 * @return 
	 */
	protected abstract E[] lut();

	@Override
	public E readValue(DataInput in) throws IOException {
		E[] lut = lut();
		int len = lut.length;
		int index;
		if (len < 256) {
			index = in.readUnsignedByte();
		} else {
			index = in.readUnsignedShort();
		}
		if (index >= 0 && index < lut.length) {
			return lut[index];
		}
		return null;
	}

	@Override
	public void writeValue(E value, DataOutput out) throws IOException {
		E[] lut = lut();
		int len = lut.length;
		int index = findEnumIndex(lut, value);
		if (len < 256) {
			out.writeByte(index);
		} else {
			out.writeShort(len);
		}
	}

	public static <E> int findEnumIndex(E[] enums, E value) {
		for (int i = 0; i < enums.length; i++) {
			if (enums[i] == value) {
				return i;
			}
		}
		return -1;
	}
}
