package urender.g3dio.ugfx.serializers;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface IGfxEnumSerializer<E extends Enum> {
	public E readValue(DataInput in) throws IOException;
	public void writeValue(E value, DataOutput out) throws IOException;
}
