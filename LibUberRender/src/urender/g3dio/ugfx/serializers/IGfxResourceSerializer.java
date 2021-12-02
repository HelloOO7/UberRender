package urender.g3dio.ugfx.serializers;

import java.io.IOException;
import urender.common.io.base.iface.DataInputEx;
import urender.common.io.base.iface.DataOutputEx;
import urender.g3dio.ugfx.adapters.IGfxResourceAdapter;

public interface IGfxResourceSerializer<R> {
	public String getTagIdent();
	
	public void deserialize(DataInputEx in, IGfxResourceAdapter adapter) throws IOException;
	public void serialize(R obj, DataOutputEx out) throws IOException;
}
