package urender.g3dio.ugfx.serializers;

import java.io.IOException;
import urender.api.UShaderType;
import urender.common.io.base.iface.DataInputEx;
import urender.common.io.base.iface.DataOutputEx;
import urender.engine.shader.UShader;
import urender.g3dio.ugfx.UGfxDataInput;
import urender.g3dio.ugfx.adapters.IGfxResourceConsumer;

public class GfxShaderSerializer implements IGfxResourceSerializer<UShader> {

	private static final UShaderType[] SHA_TYPE_LOOKUP = new UShaderType[]{UShaderType.VERTEX, UShaderType.FRAGMENT};
	
	@Override
	public String getTagIdent() {
		return "SHDR";
	}
	
	@Override
	public void deserialize(UGfxDataInput in, IGfxResourceConsumer consumer) throws IOException {
		UShaderType type = SHA_TYPE_LOOKUP[in.read()];

		consumer.loadObject(new UShader(in.readString(), type, in.readPaddedString(in.readInt())));
	}

	@Override
	public void serialize(UShader sha, DataOutputEx out) throws IOException {
		out.write(IGfxResourceSerializer.findEnumIndex(SHA_TYPE_LOOKUP, sha.getShaderType()));
		
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
