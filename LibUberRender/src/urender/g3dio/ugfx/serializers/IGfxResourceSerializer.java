package urender.g3dio.ugfx.serializers;

import java.io.IOException;
import urender.g3dio.ugfx.UGfxDataInput;
import urender.g3dio.ugfx.UGfxDataOutput;
import urender.g3dio.ugfx.adapters.IGfxResourceConsumer;

public interface IGfxResourceSerializer<R> {

	/**
	 * Gets the unique tag code used for identifying the object type during deserialization.
	 *
	 * @return Four character code that distinguishes the object class.
	 */
	public String getTagIdent();

	/**
	 * Checks if the serializer is able to serialize a given object.
	 *
	 * @param o The object to potentially serialize.
	 * @return True if the object can be serialized using this serializer.
	 */
	public boolean accepts(Object o);

	/**
	 * Deserializes a binary stream into an object.
	 *
	 * @param in The stream of object data.
	 * @param consumer Consumer to provide the objects to.
	 * @throws IOException
	 */
	public void deserialize(UGfxDataInput in, IGfxResourceConsumer consumer) throws IOException;

	/**
	 * Serializes an object into a binary stream.
	 *
	 * @param obj The object to serialize.
	 * @param out The stream to write into.
	 * @throws IOException
	 */
	public void serialize(R obj, UGfxDataOutput out) throws IOException;
}
