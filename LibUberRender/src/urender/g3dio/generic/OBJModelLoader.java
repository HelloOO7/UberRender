package urender.g3dio.generic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joml.Vector2f;
import org.joml.Vector3f;
import urender.api.UDataType;
import urender.api.UPrimitiveType;
import urender.api.UShaderType;
import urender.common.StringEx;
import urender.engine.UMaterial;
import urender.engine.UMesh;
import urender.engine.shader.UShader;
import urender.engine.shader.UShaderProgram;
import urender.engine.UVertexAttribute;
import urender.scenegraph.UModel;
import urender.scenegraph.USceneNode;

public class OBJModelLoader {

	private static final String OBJ_DEFAULT_SHADER_VERT
		= "#version 400 core\n"
		+ "\n"
		+ "layout(location = 0) in vec3 a_Position;\n"
		+ "layout(location = 1) in vec3 a_Normal;\n"
		+ "layout(location = 2) in vec2 a_Texcoord0;\n"
		+ "\n"
		+ "uniform mat4 UBR_WorldMatrix;\n"
		+ "uniform mat4 UBR_ProjectionMatrix;\n"
		+ "uniform mat3 UBR_NormalMatrix;\n"
		+ "\n"
		+ "out vec3 FS_Normal;\n"
		+ "out vec3 FS_View;\n"
		+ "out vec2 FS_Texcoord0;\n"
		+ "\n"
		+ "void main(void) {\n"
		+ "	vec4 outPosition = UBR_WorldMatrix * vec4(a_Position, 1.0);\n"
		+ " FS_View = outPosition.xyz;"
		+ " outPosition = UBR_ProjectionMatrix * outPosition;"
		+ "	FS_Normal = UBR_NormalMatrix * a_Normal;\n"
		+ "	FS_Texcoord0 = a_Texcoord0;\n"
		+ "	gl_Position = outPosition;\n"
		+ "}";

	private static final String OBJ_DEFAULT_SHADER_FRAG
		= "#version 400 core\n"
		+ "uniform sampler2D Textures[1];"
		+ "\n"
		+ "in vec3 FS_Normal;\n"
		+ "in vec3 FS_View;\n"
		+ "in vec2 FS_Texcoord0;\n"
		+ "\n"
		+ "out vec4 FragColor;\n"
		+ "\n"
		+ "void main(void) {\n"
		+ "	FragColor = texture2D(Textures[0], FS_Texcoord0) * clamp(dot(FS_Normal, -vec3(0.0, 0.0, -1.0)), 0.0, 1.0);\n"
		+ "}";

	public static void loadMTL(String resourceRoot, String mtlFileName, USceneNode dest) {
		Scanner s = new Scanner(OBJModelLoader.class.getClassLoader().getResourceAsStream(resourceRoot + "/" + mtlFileName));

		UMaterial mat = new UMaterial();

		String line;
		while (s.hasNextLine()) {
			line = s.nextLine();

			String[] commands = StringEx.splitOnecharFastNoBlank(line, ' ');
			int texIdx = 0;
			if (commands.length > 0) {
				switch (commands[0]) {
					case "newmtl":
						if (mat.name != null) {
							dest.materials.add(mat);
							mat = new UMaterial();
						}
						mat.name = commands[1];
						mat.shaderProgramName = "OBJDefaultShader";
						texIdx = 0;
						break;
					case "map_Kd":
						UMaterial.UTextureMapper mapper = new UMaterial.UTextureMapper();
						mapper.shaderVariableName = "Textures[" + (texIdx++) + "]";
						mapper.meshUVSetName = "a_Texcoord0";
						mapper.textureName = commands[1];
						File fileTest = new File(mapper.textureName);
						if (fileTest.exists()) {
							try {
								FileInputStream in = new FileInputStream(fileTest);
								dest.textures.add(IIOTextureLoader.createIIOTexture(in, mapper.textureName));
								in.close();
							} catch (IOException ex) {
								Logger.getLogger(OBJModelLoader.class.getName()).log(Level.SEVERE, null, ex);
							}
						} else {
							InputStream texStream = OBJModelLoader.class.getClassLoader().getResourceAsStream(resourceRoot + "/" + mapper.textureName);
							if (texStream != null) {
								dest.textures.add(IIOTextureLoader.createIIOTexture(texStream, mapper.textureName));
							} else {
								System.err.println("Could not load texture " + mapper.textureName);
							}
						}
						mat.textureMappers.add(mapper);
						break;
				}
			}
		}

		if (mat.name != null) {
			dest.materials.add(mat);
		}
	}

	public static USceneNode createOBJModelSceneNode(String resourceRoot, String filename) {
		USceneNode node = new USceneNode();

		UModel model = new UModel();

		String line;

		UModel.UMeshInstance meshInst = new UModel.UMeshInstance();
		UMesh mesh = new UMesh();

		List<Vector3f> positions = new ArrayList<>();
		List<Vector3f> normals = new ArrayList<>();
		List<Vector2f> texcoords = new ArrayList<>();
		List<OBJFacepoint> faces = new ArrayList<>();

		Scanner scanner = new Scanner(OBJModelLoader.class.getClassLoader().getResourceAsStream(resourceRoot + "/" + filename));

		while (scanner.hasNextLine()) {
			line = scanner.nextLine();

			String[] commands = StringEx.splitOnecharFastNoBlank(line, ' ');
			if (commands.length > 0) {
				switch (commands[0]) {
					case "mtllib":
						//Set material file
						loadMTL(resourceRoot, commands[1], node);
						break;
					case "o":
					case "g":
						if (setupMesh(mesh, positions, normals, texcoords, faces)) {
							node.meshes.add(mesh);
							model.meshes.add(meshInst);

							mesh = new UMesh();
							meshInst = new UModel.UMeshInstance();
							faces.clear();
						}
						//Object or Mesh Group
						mesh.name = commands[1];
						meshInst.meshName = commands[1];
						break;
					case "usemtl":
						if (meshInst.materialName == null) {
							meshInst.materialName = commands[1];
						} else {
							String oldMeshName = mesh.name;
							if (setupMesh(mesh, positions, normals, texcoords, faces)) {
								node.meshes.add(mesh);
								model.meshes.add(meshInst);

								mesh = new UMesh();
								meshInst = new UModel.UMeshInstance();
								faces.clear();
							}
							mesh.name = oldMeshName + "_" + commands[1];
							meshInst.materialName = commands[1];
							meshInst.meshName = mesh.name;
						}
						break;
					case "f":
						//face
						int pointCount = commands.length - 1;
						if (pointCount > 4) {
							throw new RuntimeException("Only triangle and quad faces are supported!");
						}
						for (int i = 0; i < pointCount; i++) {
							OBJFacepoint fp = new OBJFacepoint();
							String[] coords = StringEx.splitOnecharFast(commands[i + 1], '/');
							for (int j = 0; j < coords.length; j++) {
								String in = coords[j];
								if (!in.isEmpty()) {
									int idx = Integer.parseInt(in) - 1; //OBJ is one-indexed

									switch (j) {
										case 0:
											fp.posIndex = idx;
											break;
										case 1:
											fp.texcoordIndex = idx;
											break;
										case 2:
											fp.norIndex = idx;
											break;
									}
								}
							}
							faces.add(fp);
						}
						//triangulate quads
						if (pointCount == 4) {
							int sz = faces.size();
							OBJFacepoint fp0 = faces.get(sz - 4);
							OBJFacepoint fp1 = faces.get(sz - 3);
							OBJFacepoint fp2 = faces.get(sz - 2);
							OBJFacepoint fp3 = faces.get(sz - 1);
							faces.add(fp0);
							faces.add(fp2);
						}
						break;
					case "v":
						positions.add(parseVector3(commands));
						break;
					case "vn":
						normals.add(parseVector3(commands));
						break;
					case "vt":
						texcoords.add(parseVector2(commands));
						break;
				}
			}
		}
		if (setupMesh(mesh, positions, normals, texcoords, faces)) {
			node.meshes.add(mesh);
			model.meshes.add(meshInst);
		}
		node.models.add(model);

		UShader vertShader = new UShader();
		vertShader.type = UShaderType.VERTEX;
		vertShader.name = "OBJDefaultShader_V";
		vertShader.shaderData = OBJ_DEFAULT_SHADER_VERT;
		UShader fragShader = new UShader();
		fragShader.type = UShaderType.FRAGMENT;
		fragShader.name = "OBJDefaultShader_F";
		fragShader.shaderData = OBJ_DEFAULT_SHADER_FRAG;

		node.shaders.add(vertShader);
		node.shaders.add(fragShader);

		UShaderProgram program = new UShaderProgram();
		program.name = "OBJDefaultShader";
		program.fragmentShaderName = "OBJDefaultShader_V";
		program.vertexShaderName = "OBJDefaultShader_F";

		node.programs.add(program);

		scanner.close();
		return node;
	}

	private static Vector3f parseVector3(String[] commands) {
		return new Vector3f(Float.parseFloat(commands[1]), Float.parseFloat(commands[2]), Float.parseFloat(commands[3]));
	}

	private static Vector2f parseVector2(String[] commands) {
		return new Vector2f(Float.parseFloat(commands[1]), Float.parseFloat(commands[2]));
	}

	private static boolean setupMesh(UMesh mesh, List<Vector3f> positions, List<Vector3f> normals, List<Vector2f> texcoords, List<OBJFacepoint> faces) {
		mesh.primitiveType = UPrimitiveType.TRIS;
		if (!positions.isEmpty()) {
			OBJVertex vtx = new OBJVertex();
			List<OBJVertex> vertices = new ArrayList<>();
			List<Integer> indices = new ArrayList<>();

			boolean hasNormal = false;
			boolean hasTexcoord = false;

			for (OBJFacepoint vp : faces) {
				//System.out.println("vp " + vp.posIndex + "//" + vp.norIndex);
				vtx.position = positions.get(vp.posIndex);
				if (vp.norIndex >= 0) {
					vtx.normal = normals.get(vp.norIndex);
				}
				if (vp.texcoordIndex >= 0) {
					vtx.texcoord = texcoords.get(vp.texcoordIndex);
				}
				hasNormal |= vtx.normal != null;
				hasTexcoord |= vtx.texcoord != null;

				int index = vertices.indexOf(vtx);
				if (index == -1) {
					index = vertices.size();
					vertices.add(vtx);
					vtx = new OBJVertex();
				}
				indices.add(index);
			}

			mesh.indexBufferFormat = indices.size() > 0xFFFF ? UDataType.INT32 : indices.size() > 0xFF ? UDataType.INT16 : UDataType.INT8;

			mesh.indexBuffer = ByteBuffer.allocateDirect(mesh.indexBufferFormat.sizeof * indices.size());
			mesh.indexBuffer.order(ByteOrder.LITTLE_ENDIAN);

			for (Integer idx : indices) {
				writeIBO(mesh.indexBufferFormat, mesh.indexBuffer, idx);
			}

			Vector3f dmyNormal = new Vector3f(0f, 0f, 1f);
			Vector2f dmyTexcoord = new Vector2f(0f, 0f);

			Vector3f p;
			Vector3f n;
			Vector2f tc;

			int vtxStride = Float.BYTES * 3;
			if (hasNormal) {
				vtxStride += Float.BYTES * 3;
			}
			if (hasTexcoord) {
				vtxStride += Float.BYTES * 2;
			}

			mesh.vertexBuffer = ByteBuffer.allocateDirect(vtxStride * vertices.size());
			mesh.vertexBuffer.order(ByteOrder.LITTLE_ENDIAN);

			UVertexAttribute pos = new UVertexAttribute();
			pos.elementCount = 3;
			pos.format = UDataType.FLOAT32;
			pos.normalized = false;
			pos.unsigned = false;
			pos.shaderAttrName = "a_Position";
			pos.offset = 0;
			mesh.vertexAttributes.add(pos);

			int size = pos.getSize();

			if (hasNormal) {
				UVertexAttribute nor = new UVertexAttribute();
				nor.elementCount = 3;
				nor.format = UDataType.FLOAT32;
				nor.normalized = false;
				nor.unsigned = false;
				nor.shaderAttrName = "a_Normal";
				nor.offset = size;
				mesh.vertexAttributes.add(nor);
				size += nor.getSize();
			}
			if (hasTexcoord) {
				UVertexAttribute uv = new UVertexAttribute();
				uv.elementCount = 2;
				uv.format = UDataType.FLOAT32;
				uv.normalized = false;
				uv.unsigned = false;
				uv.shaderAttrName = "a_Texcoord0";
				uv.offset = size;
				mesh.vertexAttributes.add(uv);
				size += uv.getSize();
			}

			for (OBJVertex v : vertices) {
				p = v.position;
				n = v.normal == null ? dmyNormal : v.normal;
				tc = v.texcoord == null ? dmyTexcoord : v.texcoord;

				mesh.vertexBuffer.putFloat(v.position.x);
				mesh.vertexBuffer.putFloat(v.position.y);
				mesh.vertexBuffer.putFloat(v.position.z);

				if (hasNormal) {
					mesh.vertexBuffer.putFloat(v.normal.x);
					mesh.vertexBuffer.putFloat(v.normal.y);
					mesh.vertexBuffer.putFloat(v.normal.z);
				}
				if (hasTexcoord) {
					mesh.vertexBuffer.putFloat(v.texcoord.x);
					mesh.vertexBuffer.putFloat(v.texcoord.y);
				}
			}

			return true;
		}
		return false;
	}

	private static void writeIBO(UDataType type, ByteBuffer buf, int value) {
		switch (type) {
			case INT8:
				buf.put((byte) value);
				break;
			case INT16:
				buf.putShort((short) value);
				break;
			case INT32:
				buf.putInt(value);
				break;
		}
	}

	private static class OBJVertex {

		public Vector3f position;
		public Vector2f texcoord;
		public Vector3f normal;

		@Override
		public int hashCode() {
			int hash = 3;
			hash = 23 * hash + Objects.hashCode(this.position);
			hash = 23 * hash + Objects.hashCode(this.texcoord);
			hash = 23 * hash + Objects.hashCode(this.normal);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || !(obj instanceof OBJVertex)) {
				return false;
			}
			final OBJVertex other = (OBJVertex) obj;
			return Objects.equals(position, other.position) && Objects.equals(normal, other.normal) && Objects.equals(texcoord, other.texcoord);
		}
	}

	private static class OBJFacepoint {

		public int posIndex = -1;
		public int texcoordIndex = -1;
		public int norIndex = -1;
	}
}
