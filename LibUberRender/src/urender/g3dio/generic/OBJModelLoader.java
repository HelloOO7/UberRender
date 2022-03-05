package urender.g3dio.generic;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joml.Vector2f;
import org.joml.Vector3f;
import urender.api.UDataType;
import urender.api.UPrimitiveType;
import urender.common.StringEx;
import urender.common.fs.FSUtil;
import urender.engine.UMaterialBuilder;
import urender.engine.UMeshBuilder;
import urender.engine.UTextureMapperBuilder;
import urender.engine.shader.UShader;
import urender.engine.shader.UShaderProgram;
import urender.engine.UVertexAttribute;
import urender.engine.UVertexAttributeBuilder;
import urender.scenegraph.UModel;
import urender.scenegraph.USceneNode;

/**
 * Resource loader for Wavefront OBJ model data.
 */
public class OBJModelLoader {

	private static final String OBJ_DEFAULT_SHADER_NAME = "OBJDefaultShader";
	private static final String OBJ_DEFAULT_SHADER_VERT_NAME = "OBJDefaultShader_V";
	private static final String OBJ_DEFAULT_SHADER_FRAG_NAME = "OBJDefaultShader_F";

	private static final String OBJ_DEFAULT_SHADER_VERT
		= "#version 400 core\n"
		+ "\n"
		+ "layout(location = 0) in vec3 a_Position;\n"
		+ "layout(location = 1) in vec3 a_Normal;\n"
		+ "layout(location = 2) in vec3 a_Tangent;\n"
		+ "layout(location = 3) in vec2 a_Texcoord0;\n"
		+ "layout(location = 4) in vec2 a_Texcoord1;\n"
		+ "\n"
		+ "uniform mat4 UBR_ModelMatrix;\n"
		+ "uniform mat4 UBR_ViewMatrix;\n"
		+ "uniform mat4 UBR_ProjectionMatrix;\n"
		+ "uniform mat3 UBR_NormalMatrix;\n"
		+ "\n"
		+ "out vec3 FS_Normal;\n"
		+ "out vec3 FS_Tangent;\n"
		+ "out vec3 FS_View;\n"
		+ "out vec2 FS_Texcoord0;\n"
		+ "out vec2 FS_Texcoord1;\n"
		+ "\n"
		+ "void main(void) {\n"
		+ "	vec4 outPosition = UBR_ModelMatrix * vec4(a_Position, 1.0);\n"
		+ " FS_View = outPosition.xyz;\n"
		+ " outPosition = UBR_ProjectionMatrix * UBR_ViewMatrix * outPosition;\n"
		+ "	FS_Normal = UBR_NormalMatrix * a_Normal;\n"
		+ "	FS_Tangent = UBR_NormalMatrix * a_Tangent;\n"
		+ "	FS_Texcoord0 = a_Texcoord0;\n"
		+ "	FS_Texcoord1 = a_Texcoord1;\n"
		+ "	gl_Position = outPosition;\n"
		+ "}";

	private static final String OBJ_DEFAULT_SHADER_FRAG
		= "#version 400 core\n"
		+ "uniform sampler2D Textures[1];"
		+ "\n"
		+ "in vec3 FS_Normal;\n"
		+ "in vec3 FS_Tangent;\n"
		+ "in vec3 FS_View;\n"
		+ "in vec2 FS_Texcoord0;\n"
		+ "in vec2 FS_Texcoord1;\n"
		+ "\n"
		+ "out vec4 FragColor;\n"
		+ "\n"
		+ "void main(void) {\n"
		+ "	FragColor = texture2D(Textures[0], FS_Texcoord0) * clamp(dot(FS_Normal, -vec3(0.0, 0.0, -1.0)), 0.0, 1.0);\n"
		+ "}";

	private static void loadMTL(OBJFilesystemAccessor fs, String mtlFileName, USceneNode dest) {
		Scanner s = new Scanner(fs.getStream(mtlFileName));

		UMaterialBuilder matBuilder = new UMaterialBuilder();
		String nowMatName = null;
		int texIdx = 0;

		HashSet<String> loadedTextureNames = new HashSet<>();

		String line;
		while (s.hasNextLine()) {
			line = s.nextLine();

			String[] commands = StringEx.splitOnecharFastNoBlank(line, ' ');

			if (commands.length > 0) {
				switch (commands[0]) {
					case "newmtl":
						if (nowMatName != null) {
							dest.materials.add(matBuilder.build());
							matBuilder.reset();
						}
						nowMatName = commands[1];
						matBuilder.setName(nowMatName);
						matBuilder.setShaderProgramName(OBJ_DEFAULT_SHADER_NAME);
						texIdx = 0;
						break;
					case "map_Kd":
						UTextureMapperBuilder mapperBld = new UTextureMapperBuilder();
						mapperBld.setShaderVariableName("Textures[" + (texIdx++) + "]");

						String texturePath = commands[1];
						String textureName = FSUtil.getFileNameWithoutExtension(texturePath);
						mapperBld.setTextureName(textureName);
						if (!loadedTextureNames.contains(textureName)) {
							File fileTest = new File(texturePath);
							if (fileTest.exists()) {
								try {
									FileInputStream in = new FileInputStream(fileTest);
									dest.textures.add(IIOTextureLoader.createIIOTexture(in, textureName));
									in.close();
								} catch (IOException ex) {
									Logger.getLogger(OBJModelLoader.class.getName()).log(Level.SEVERE, null, ex);
								}
							} else {
								InputStream texStream = fs.getStream(texturePath);
								if (texStream != null) {
									dest.textures.add(IIOTextureLoader.createIIOTexture(texStream, textureName));
								} else {
									System.err.println("Could not load texture " + texturePath);
								}
							}
							loadedTextureNames.add(textureName);
						}
						matBuilder.addTextureMapper(mapperBld.build());
						break;
				}
			}
		}

		if (nowMatName != null) {
			dest.materials.add(matBuilder.build());
		}
	}

	public static USceneNode createOBJModelSceneNode(File objFile) {
		File parent = objFile.getAbsoluteFile().getParentFile();
		String name = objFile.getName();
		return createOBJModelSceneNode(new OBJDiskFilesystemAccessor(parent), name);
	}

	public static USceneNode createOBJModelSceneNode(String resourceRoot, String filename) {
		return createOBJModelSceneNode(new OBJRuntimeResourceFilesystemAccessor(resourceRoot), filename);
	}

	private static USceneNode createOBJModelSceneNode(OBJFilesystemAccessor fs, String filename) {
		USceneNode node = new USceneNode();

		UModel model = new UModel();
		model.setName(filename);

		String line;

		UModel.UMeshInstance meshInst = new UModel.UMeshInstance();
		UMeshBuilder mesh = new UMeshBuilder();
		String nowMeshName = null;

		List<Vector3f> positions = new ArrayList<>();
		List<Vector3f> normals = new ArrayList<>();
		List<Vector2f>[] texcoords = new ArrayList[2];
		List<OBJFacepoint> faces = new ArrayList<>();

		for (int i = 0; i < texcoords.length; i++) {
			texcoords[i] = new ArrayList<>();
		}

		Scanner scanner = new Scanner(fs.getStream(filename));

		while (scanner.hasNextLine()) {
			line = scanner.nextLine();

			String[] commands = StringEx.splitOnecharFastNoBlank(line, ' ');
			if (commands.length > 0) {
				switch (commands[0]) {
					case "mtllib":
						//Set material file
						loadMTL(fs, commands[1], node);
						break;
					case "o":
					case "g":
						if (setupMesh(mesh, positions, normals, texcoords, faces)) {
							node.meshes.add(mesh.build());
							model.meshes.add(meshInst);

							mesh.reset();
							meshInst = new UModel.UMeshInstance();
							faces.clear();
						}
						//Object or Mesh Group
						nowMeshName = commands[1];
						mesh.setName(nowMeshName);
						meshInst.meshName = nowMeshName;
						break;
					case "usemtl":
						if (meshInst.materialName == null) {
							meshInst.materialName = commands[1];
						} else {
							String oldMeshName = nowMeshName;
							if (setupMesh(mesh, positions, normals, texcoords, faces)) {
								node.meshes.add(mesh.build());
								model.meshes.add(meshInst);

								mesh.reset();
								meshInst = new UModel.UMeshInstance();
								faces.clear();
							}
							nowMeshName = oldMeshName + "_" + commands[1];
							mesh.setName(nowMeshName);
							meshInst.materialName = commands[1];
							meshInst.meshName = nowMeshName;
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
						texcoords[0].add(parseVector2(commands));
						break;
					case "vt1":
						texcoords[1].add(parseVector2(commands));
						break;
				}
			}
		}
		if (setupMesh(mesh, positions, normals, texcoords, faces)) {
			node.meshes.add(mesh.build());
			model.meshes.add(meshInst);
		}
		node.models.add(model);

		node.shaders.add(UShader.createVertexShader(OBJ_DEFAULT_SHADER_VERT_NAME, OBJ_DEFAULT_SHADER_VERT));
		node.shaders.add(UShader.createFragmentShader(OBJ_DEFAULT_SHADER_FRAG_NAME, OBJ_DEFAULT_SHADER_FRAG));

		node.programs.add(new UShaderProgram(OBJ_DEFAULT_SHADER_NAME, OBJ_DEFAULT_SHADER_VERT_NAME, OBJ_DEFAULT_SHADER_FRAG_NAME));

		scanner.close();
		return node;
	}

	private static Vector3f parseVector3(String[] commands) {
		return new Vector3f(Float.parseFloat(commands[1]), Float.parseFloat(commands[2]), Float.parseFloat(commands[3]));
	}

	private static Vector2f parseVector2(String[] commands) {
		if (commands[1].equals("NULL")) {
			return null;
		}
		return new Vector2f(Float.parseFloat(commands[1]), Float.parseFloat(commands[2]));
	}

	private static boolean setupMesh(UMeshBuilder mesh, List<Vector3f> positions, List<Vector3f> normals, List<Vector2f>[] texcoords, List<OBJFacepoint> faces) {
		mesh.setPrimitiveType(UPrimitiveType.TRIS);
		if (!positions.isEmpty()) {
			OBJVertex vtx = new OBJVertex();
			List<OBJVertex> vertices = new ArrayList<>();
			List<OBJVertex> verticesUnindexed = new ArrayList<>();
			List<Integer> indices = new ArrayList<>();

			boolean hasNormal = false;
			boolean[] hasTexcoord = new boolean[2];

			System.out.println("Converting " + faces.size() + " faces...");

			for (OBJFacepoint vp : faces) {
				//System.out.println("vp " + vp.posIndex + "//" + vp.norIndex);
				vtx.position = positions.get(vp.posIndex);
				if (vp.norIndex >= 0) {
					vtx.normal = normals.get(vp.norIndex);
				}
				if (vp.texcoordIndex >= 0) {
					for (int i = 0; i < texcoords.length; i++) {
						if (vp.texcoordIndex < texcoords[i].size()) {
							vtx.texcoords[i] = texcoords[i].get(vp.texcoordIndex);
						}
					}
				}
				hasNormal |= vtx.normal != null;
				for (int i = 0; i < hasTexcoord.length; i++) {
					hasTexcoord[i] |= vtx.texcoords[i] != null;
					if (vtx.texcoords[i] == null && hasTexcoord[i]) {
						throw new RuntimeException("Facepoint " + faces.indexOf(vp) + " of mesh " + mesh.getName() + " does not have texcoord " + i);
					}
				}

				int index = vertices.indexOf(vtx);
				verticesUnindexed.add(vtx);
				if (index == -1) {
					index = vertices.size();
					vertices.add(vtx);
				}
				vtx = new OBJVertex();
				indices.add(index);
			}

			UDataType indexBufferFormat = indices.size() > 0xFFFF ? UDataType.INT32 : indices.size() > 0xFF ? UDataType.INT16 : UDataType.INT8;

			ByteBuffer indexBuffer = ByteBuffer.allocateDirect(indexBufferFormat.sizeof * indices.size());
			indexBuffer.order(ByteOrder.LITTLE_ENDIAN);

			for (Integer idx : indices) {
				writeIBO(indexBufferFormat, indexBuffer, idx);
			}

			Vector3f dmyNormal = new Vector3f(0f, 0f, 1f);
			Vector2f dmyTexcoord = new Vector2f(0f, 0f);

			Vector3f p;
			Vector3f n;
			Vector2f tc;

			boolean hasTangent = hasNormal && hasTexcoord[0];

			int vtxStride = Float.BYTES * 3;
			if (hasNormal) {
				vtxStride += Float.BYTES * 3;
			}
			if (hasTangent) {
				vtxStride += Float.BYTES * 3;
			}
			for (int i = 0; i < hasTexcoord.length; i++) {
				if (hasTexcoord[i]) {
					vtxStride += Float.BYTES * 2;
				}
			}

			ByteBuffer vertexBuffer = ByteBuffer.allocateDirect(vtxStride * vertices.size());
			vertexBuffer.order(ByteOrder.LITTLE_ENDIAN);

			UVertexAttributeBuilder attrBld = new UVertexAttributeBuilder();

			UVertexAttribute pos
				= attrBld
					.setShaderAttrName("a_Position")
					.setOffset(0)
					.setElementCount(3)
					.setFormat(UDataType.FLOAT32)
					.setNormalized(false)
					.build();
			mesh.addVertexAttribute(pos);

			int size = pos.getSize();

			if (hasNormal) {
				attrBld.reset();

				UVertexAttribute nor
					= attrBld
						.setShaderAttrName("a_Normal")
						.setOffset(size)
						.setElementCount(3)
						.setFormat(UDataType.FLOAT32)
						.setNormalized(false)
						.build();
				mesh.addVertexAttribute(nor);
				size += nor.getSize();
			}
			if (hasTangent) {
				attrBld.reset();

				UVertexAttribute tgt
					= attrBld
						.setShaderAttrName("a_Tangent")
						.setOffset(size)
						.setElementCount(3)
						.setFormat(UDataType.FLOAT32)
						.setNormalized(false)
						.build();
				mesh.addVertexAttribute(tgt);
				size += tgt.getSize();
			}
			for (int i = 0; i < hasTexcoord.length; i++) {
				if (hasTexcoord[i]) {
					attrBld.reset();

					UVertexAttribute uv
						= attrBld
							.setShaderAttrName("a_Texcoord" + i)
							.setOffset(size)
							.setElementCount(2)
							.setFormat(UDataType.FLOAT32)
							.setNormalized(false)
							.build();
					mesh.addVertexAttribute(uv);
					size += uv.getSize();
				}
			}

			OBJVertex[] triVertices = new OBJVertex[3];
			int[] triIndices = new int[3];

			Vector3f edge1 = new Vector3f();
			Vector3f edge2 = new Vector3f();
			Vector2f deltaUV1 = new Vector2f();
			Vector2f deltaUV2 = new Vector2f();

			Vector3f tangent = new Vector3f();

			HashSet<OBJVertex> convVerts = new HashSet<>();

			System.out.println("About to convert " + verticesUnindexed.size() + " vertices.");

			for (int ti = 0; ti < verticesUnindexed.size(); ti += 3) {
				triVertices[0] = verticesUnindexed.get(ti);
				triVertices[1] = verticesUnindexed.get(ti + 1);
				triVertices[2] = verticesUnindexed.get(ti + 2);
				triIndices[0] = indices.get(ti);
				triIndices[1] = indices.get(ti + 1);
				triIndices[2] = indices.get(ti + 2);

				if (hasTangent) {
					triVertices[1].position.sub(triVertices[0].position, edge1);
					triVertices[2].position.sub(triVertices[0].position, edge2);
					triVertices[1].texcoords[0].sub(triVertices[0].texcoords[0], deltaUV1);
					triVertices[2].texcoords[0].sub(triVertices[0].texcoords[0], deltaUV2);

					float r = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV1.y * deltaUV2.x);
					tangent.set(
						edge1.x * deltaUV2.y - edge2.x * deltaUV1.y,
						edge1.y * deltaUV2.y - edge2.y * deltaUV1.y,
						edge1.z * deltaUV2.y - edge2.z * deltaUV1.y
					);
					tangent.mul(r);
					tangent.normalize();
				}

				for (int pointIdx = 0; pointIdx < 3; pointIdx++) {
					OBJVertex v = triVertices[pointIdx];
					if (!convVerts.contains(v)) {
						p = v.position;
						n = v.normal == null ? dmyNormal : v.normal;

						vertexBuffer.position(vtxStride * triIndices[pointIdx]);

						vertexBuffer.putFloat(v.position.x);
						vertexBuffer.putFloat(v.position.y);
						vertexBuffer.putFloat(v.position.z);

						if (hasNormal) {
							vertexBuffer.putFloat(v.normal.x);
							vertexBuffer.putFloat(v.normal.y);
							vertexBuffer.putFloat(v.normal.z);
						}
						if (hasTangent) {
							vertexBuffer.putFloat(tangent.x);
							vertexBuffer.putFloat(tangent.y);
							vertexBuffer.putFloat(tangent.z);
						}
						for (int i = 0; i < hasTexcoord.length; i++) {
							if (hasTexcoord[i]) {
								vertexBuffer.putFloat(v.texcoords[i].x);
								vertexBuffer.putFloat(v.texcoords[i].y);
							}
						}

						convVerts.add(v);
					}
				}
			}

			mesh.setIBO(indexBufferFormat, indexBuffer).setVBO(vertexBuffer);

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
		public Vector2f[] texcoords = new Vector2f[2];
		public Vector3f normal;

		@Override
		public int hashCode() {
			int hash = 3;
			hash = 23 * hash + Objects.hashCode(this.position);
			hash = 23 * hash + Arrays.hashCode(this.texcoords);
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
			return Objects.equals(position, other.position) && Objects.equals(normal, other.normal) && Arrays.equals(texcoords, other.texcoords);
		}
	}

	private static class OBJFacepoint {

		public int posIndex = -1;
		public int texcoordIndex = -1;
		public int norIndex = -1;
	}

	private static interface OBJFilesystemAccessor {

		public InputStream getStream(String path);
	}

	private static class OBJDiskFilesystemAccessor implements OBJFilesystemAccessor {

		private final File relativeRoot;

		public OBJDiskFilesystemAccessor(File relativeRoot) {
			this.relativeRoot = relativeRoot;
		}

		@Override
		public InputStream getStream(String path) {
			File f = new File(path);

			if (!f.exists()) {
				f = Paths.get(relativeRoot.getAbsolutePath(), path).toFile();
			}

			try {
				return new BufferedInputStream(new FileInputStream(f));
			} catch (FileNotFoundException ex) {
				return null;
			}
		}
	}

	private static class OBJRuntimeResourceFilesystemAccessor implements OBJFilesystemAccessor {

		private final String rootPath;

		public OBJRuntimeResourceFilesystemAccessor(String rootPath) {
			this.rootPath = rootPath;
		}

		@Override
		public InputStream getStream(String path) {
			return OBJModelLoader.class.getClassLoader().getResourceAsStream(rootPath + "/" + path);
		}
	}
}
