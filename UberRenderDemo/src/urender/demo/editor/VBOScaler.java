package urender.demo.editor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import urender.engine.UMesh;

public class VBOScaler {

	public static void scale(float factor, List<UMesh> meshes) {
		if (factor != 0f) {
			for (UMesh mesh : meshes) {
				ByteBuffer bbuf = mesh.getVBO();
				int vcount = mesh.getVertexCount();
				int stride = mesh.getOneVertexSize();

				bbuf.order(ByteOrder.LITTLE_ENDIAN);

				for (int i = 0; i < vcount; i++) {
					//assume position attribute as first
					bbuf.position(stride * i);
					for (int comp = 0; comp < 3; comp++) {
						bbuf.mark();
						float v = bbuf.getFloat();
						bbuf.reset();
						bbuf.putFloat(v * factor);
					}
				}
			}
		}
	}
}
