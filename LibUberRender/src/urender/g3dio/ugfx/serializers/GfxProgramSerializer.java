package urender.g3dio.ugfx.serializers;

import java.io.IOException;
import urender.engine.shader.UShaderProgram;
import urender.g3dio.ugfx.UGfxDataInput;
import urender.g3dio.ugfx.UGfxDataOutput;
import urender.g3dio.ugfx.adapters.IGfxResourceConsumer;

/**
 * UGfx Shader program resource serializer.
 */
public class GfxProgramSerializer implements IGfxResourceSerializer<UShaderProgram> {
	
	@Override
	public String getTagIdent() {
		return "SPRG";
	}
	
	@Override
	public void deserialize(UGfxDataInput in, IGfxResourceConsumer consumer) throws IOException {
		consumer.loadObject(new UShaderProgram(in.readString(), in.readString(), in.readString()));
	}

	@Override
	public void serialize(UShaderProgram prog, UGfxDataOutput out) throws IOException {
		out.writeString(prog.getName());
		out.writeString(prog.getVshName());
		out.writeString(prog.getFshName());
	}

	@Override
	public boolean accepts(Object o) {
		return o instanceof UShaderProgram;
	}
}
