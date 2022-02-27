package urender.g3dio.ugfx.serializers;

import java.io.IOException;
import urender.api.UShaderType;
import urender.engine.shader.UShader;
import urender.g3dio.ugfx.UGfxDataInput;
import urender.g3dio.ugfx.UGfxDataOutput;
import urender.g3dio.ugfx.adapters.IGfxResourceConsumer;

public class GfxShaderSerializer implements IGfxResourceSerializer<UShader> {
	
	@Override
	public String getTagIdent() {
		return "SHDR";
	}
	
	@Override
	public void deserialize(UGfxDataInput in, IGfxResourceConsumer consumer) throws IOException {
		UShaderType type = in.readEnum(UShaderType.class);

		consumer.loadObject(new UShader(in.readString(), type, in.readPaddedString(in.readInt())));
	}

	@Override
	public void serialize(UShader sha, UGfxDataOutput out) throws IOException {
		out.writeEnum(sha.getShaderType());
		
		out.writeString(sha.getName());
		
		String data = sha.getShaderData();
		//Faster de/serialization by providing strlen ahead of time
		out.writeInt(data.length());
		out.writeStringUnterminated(data);
	}

	@Override
	public boolean accepts(Object o) {
		return o instanceof UShader;
	}
}
