package urender.g3dio.ugfx.serializers;

import java.io.IOException;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import urender.common.io.base.iface.DataInputEx;
import urender.common.io.base.iface.DataOutputEx;
import urender.engine.shader.UUniform;
import urender.engine.shader.UUniformFloat;
import urender.engine.shader.UUniformInt;
import urender.engine.shader.UUniformList;
import urender.engine.shader.UUniformType;
import urender.engine.shader.UUniformVector2;
import urender.engine.shader.UUniformVector3;
import urender.engine.shader.UUniformVector4;

public class GfxUniformListIO {

	private static final UUniformType[] UNIFORM_TYPE_LUT = new UUniformType[]{
		UUniformType.INT,
		UUniformType.FLOAT,
		UUniformType.VEC2,
		UUniformType.VEC3,
		UUniformType.VEC4,
		UUniformType.MATRIX3,
		UUniformType.MATRIX4
	};

	public static void readUniformList(UUniformList dest, DataInputEx in) throws IOException {
		int uniformCount = in.readUnsignedShort();
		for (int uniformIdx = 0; uniformIdx < uniformCount; uniformIdx++) {
			String name = in.readString();
			UUniformType type = UNIFORM_TYPE_LUT[in.read()];
			int valueCount = in.readUnsignedShort();

			switch (type) {
				case FLOAT: {
					float[] floats = new float[valueCount];
					for (int i = 0; i < valueCount; i++) {
						floats[i] = in.readFloat();
					}
					dest.add(new UUniformFloat(name, floats));
					break;
				}
				case INT: {
					int[] ints = new int[valueCount];
					for (int i = 0; i < valueCount; i++) {
						ints[i] = in.readInt();
					}
					dest.add(new UUniformInt(name, ints));
					break;
				}
				case VEC2: {
					Vector2f[] vectors = new Vector2f[valueCount];
					for (int i = 0; i < valueCount; i++) {
						vectors[i] = new Vector2f(in.readFloat(), in.readFloat());
					}
					dest.add(new UUniformVector2(name, vectors));
					break;
				}
				case VEC3: {
					Vector3f[] vectors = new Vector3f[valueCount];
					for (int i = 0; i < valueCount; i++) {
						vectors[i] = new Vector3f(in.readFloat(), in.readFloat(), in.readFloat());
					}
					dest.add(new UUniformVector3(name, vectors));
					break;
				}
				case VEC4: {
					Vector4f[] vectors = new Vector4f[valueCount];
					for (int i = 0; i < valueCount; i++) {
						vectors[i] = new Vector4f(in.readFloat(), in.readFloat(), in.readFloat(), in.readFloat());
					}
					dest.add(new UUniformVector4(name, vectors));
					break;
				}
				case MATRIX3: {
					Matrix3f[] matrices = new Matrix3f[valueCount];
					float[] tmp = new float[3 * 3];
					for (int i = 0; i < valueCount; i++) {
						readFloatArr(tmp, 9, in);
						matrices[i] = new Matrix3f();
						matrices[i].set(tmp);
					}
					break;
				}
				case MATRIX4: {
					Matrix4f[] matrices = new Matrix4f[valueCount];
					float[] tmp = new float[4 * 4];
					for (int i = 0; i < valueCount; i++) {
						readFloatArr(tmp, 16, in);
						matrices[i] = new Matrix4f();
						matrices[i].set(tmp);
					}
					break;
				}
			}
		}
	}

	private static void readFloatArr(float[] dest, int count, DataInputEx in) throws IOException {
		for (int i = 0; i < count; i++) {
			dest[i] = in.readFloat();
		}
	}

	public static void writeUniformList(UUniformList list, DataOutputEx out) throws IOException {
		out.writeShort(list.size());
		for (UUniform u : list) {
			out.writeString(u.getName());
			out.write(IGfxResourceSerializer.findEnumIndex(UNIFORM_TYPE_LUT, u.getUniformType()));
			int count = u.valueCount();
			out.writeShort(count);
			for (int i = 0; i < count; i++) {
				Object v = u.get(i);
				switch (u.getUniformType()) {
					case FLOAT:
						out.writeFloat((Float) v);
						break;
					case INT:
						out.writeInt((Integer) v);
						break;
					case VEC2: {
						Vector2f vec = (Vector2f) v;
						out.writeFloat(vec.x);
						out.writeFloat(vec.y);
						break;
					}
					case VEC3: {
						Vector3f vec = (Vector3f) v;
						out.writeFloat(vec.x);
						out.writeFloat(vec.y);
						out.writeFloat(vec.z);
						break;
					}
					case VEC4: {
						Vector4f vec = (Vector4f) v;
						out.writeFloat(vec.x);
						out.writeFloat(vec.y);
						out.writeFloat(vec.z);
						out.writeFloat(vec.w);
						break;
					}
					case MATRIX3: {
						float[] tmp = new float[3 * 3];
						((Matrix3f) v).get(tmp);
						out.writeFloats(tmp);
						break;
					}
					case MATRIX4: {
						float[] tmp = new float[4 * 4];
						((Matrix4f) v).get(tmp);
						out.writeFloats(tmp);
						break;
					}
				}
			}
		}
	}
}
