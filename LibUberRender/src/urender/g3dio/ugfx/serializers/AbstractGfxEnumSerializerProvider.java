package urender.g3dio.ugfx.serializers;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

/**
 * Implementation of IGfxEnumSerializerProvider using hash maps.
 */
public abstract class AbstractGfxEnumSerializerProvider implements IGfxEnumSerializerProvider {

	/**
	 * Gets the hash map containing the serializer in this set.
	 *
	 * @return
	 */
	protected abstract Map<Class<? extends Enum>, IGfxEnumSerializer> serializerMap();

	@Override
	public boolean canSerialize(Class<? extends Enum> clazz) {
		return serializerMap().containsKey(clazz);
	}

	@Override
	public <E extends Enum> E deserialize(Class<E> clazz, DataInput in) throws IOException {
		return (E) serializerMap().get(clazz).readValue(in);
	}

	@Override
	public void serialize(Enum value, DataOutput out) throws IOException {
		serializerMap().get(value.getClass()).writeValue(value, out);
	}
}
