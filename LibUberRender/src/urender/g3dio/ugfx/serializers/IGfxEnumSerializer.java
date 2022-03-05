package urender.g3dio.ugfx.serializers;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Interface for serializing a single enum class.
 *
 * @param <E> Type that this serializer can handle.
 */
public interface IGfxEnumSerializer<E extends Enum> {

	/**
	 * Reads an enum value from a data stream.
	 *
	 * @param in Stream to read from.
	 * @return Enum value of type E.
	 * @throws IOException
	 */
	public E readValue(DataInput in) throws IOException;

	/**
	 * Writes an enum value to a data stream.
	 *
	 * @param value Value to write.
	 * @param out Data stream to write into.
	 * @throws IOException
	 */
	public void writeValue(E value, DataOutput out) throws IOException;
}
