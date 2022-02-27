package urender.g3dio.ugfx.serializers;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface IGfxEnumSerializerProvider {
	public boolean canSerialize(Class<? extends Enum> clazz);
	public <E extends Enum> E deserialize(Class<E> clazz, DataInput in) throws IOException;
	public void serialize(Enum value, DataOutput out) throws IOException;
}
