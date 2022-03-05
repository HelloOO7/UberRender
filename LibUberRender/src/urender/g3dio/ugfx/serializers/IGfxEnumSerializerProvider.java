package urender.g3dio.ugfx.serializers;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Interface for handling sets of enum serializer.
 */
public interface IGfxEnumSerializerProvider {

	/**
	 * Checks if an enum class can be de/serialized.
	 *
	 * @param clazz Class of the enum.
	 * @return True if this enum can be de/serialized using this class, in one way or another.
	 */
	public boolean canSerialize(Class<? extends Enum> clazz);

	/**
	 * Converts data read from a stream to an enum constant.
	 *
	 * @param <E> Type of the enum.
	 * @param clazz Class of the enum type.
	 * @param in The stream to read from.
	 * @return Enum constant.
	 * @throws IOException
	 */
	public <E extends Enum> E deserialize(Class<E> clazz, DataInput in) throws IOException;

	/**
	 * Writes an enum constant into a data stream.
	 *
	 * @param value The enum constant to write.
	 * @param out Stream to write into.
	 * @throws IOException
	 */
	public void serialize(Enum value, DataOutput out) throws IOException;
}
