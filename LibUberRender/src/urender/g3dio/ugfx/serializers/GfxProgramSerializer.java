package urender.g3dio.ugfx.serializers;

import java.io.IOException;
import urender.common.io.base.iface.DataInputEx;
import urender.common.io.base.iface.DataOutputEx;
import urender.engine.shader.UShaderProgram;
import urender.g3dio.ugfx.adapters.IGfxResourceConsumer;

public class GfxProgramSerializer implements IGfxResourceSerializer<UShaderProgram> {
	
	@Override
	public String getTagIdent() {
		return "SPRG";
	}
	
	@Override
	public void deserialize(DataInputEx in, IGfxResourceConsumer consumer) throws IOException {
		consumer.loadObject(new UShaderProgram(in.readString(), in.readString(), in.readString()));
	}

	@Override
	public void serialize(UShaderProgram prog, DataOutputEx out) throws IOException {
		out.writeString(prog.getName());
		out.writeString(prog.getVshName());
		out.writeString(prog.getFshName());
	}

	@Override
	public boolean accepts(Object o) {
		return o instanceof UShaderProgram;
	}
}
