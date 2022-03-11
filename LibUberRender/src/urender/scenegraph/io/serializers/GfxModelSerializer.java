package urender.scenegraph.io.serializers;

import urender.g3dio.ugfx.serializers.*;
import java.io.IOException;
import urender.g3dio.ugfx.UGfxDataInput;
import urender.g3dio.ugfx.UGfxDataOutput;
import urender.g3dio.ugfx.adapters.IGfxResourceConsumer;
import urender.scenegraph.UModel;

/**
 * UGfx Model resource serializer.
 */
public class GfxModelSerializer implements IGfxResourceSerializer<UModel> {

	@Override
	public String getTagIdent() {
		return "GMDL";
	}

	@Override
	public void deserialize(UGfxDataInput in, IGfxResourceConsumer consumer) throws IOException {
		UModel model = new UModel();
		model.setName(in.readString());

		int meshInstCount = in.readUnsignedShort();
		for (int i = 0; i < meshInstCount; i++) {
			UModel.UMeshInstance inst = new UModel.UMeshInstance();
			inst.meshName = in.readString();
			inst.materialName = in.readString();
			model.meshes.add(inst);
		}

		consumer.loadObject(model);
	}

	@Override
	public void serialize(UModel model, UGfxDataOutput out) throws IOException {
		out.writeString(model.getName());
		out.writeShort(model.meshes.size());
		for (UModel.UMeshInstance inst : model.meshes) {
			out.writeString(inst.meshName);
			out.writeString(inst.materialName);
		}
	}

	@Override
	public boolean accepts(Object o) {
		return o instanceof UModel;
	}
}
