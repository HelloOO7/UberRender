package urender.g3dio.ugfx.serializers;

import java.io.IOException;
import urender.g3dio.ugfx.UGfxDataInput;
import urender.g3dio.ugfx.UGfxDataOutput;
import urender.g3dio.ugfx.adapters.IGfxResourceConsumer;

public interface IGfxResourceSerializer<R> {
	public String getTagIdent();
	public boolean accepts(Object o);
	
	public void deserialize(UGfxDataInput in, IGfxResourceConsumer consumer) throws IOException;
	public void serialize(R obj, UGfxDataOutput out) throws IOException;
}
