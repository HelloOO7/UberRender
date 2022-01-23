package urender.g3dio.ugfx.serializers;

import java.io.IOException;
import urender.common.io.base.iface.DataInputEx;
import urender.common.io.base.iface.DataOutputEx;
import urender.g3dio.ugfx.UGfxDataInput;
import urender.g3dio.ugfx.adapters.IGfxResourceConsumer;

public interface IGfxResourceSerializer<R> {
	public String getTagIdent();
	public boolean accepts(Object o);
	
	public void deserialize(UGfxDataInput in, IGfxResourceConsumer consumer) throws IOException;
	public void serialize(R obj, DataOutputEx out) throws IOException;
	
	public static <E> int findEnumIndex(E[] enums, E value) {
		for (int i = 0; i < enums.length; i++) {
			if (enums[i] == value) {
				return i;
			}
		}
		return -1;
	}
}
