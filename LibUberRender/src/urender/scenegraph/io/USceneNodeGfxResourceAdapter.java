package urender.scenegraph.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import urender.engine.UGfxEngineObject;
import urender.engine.UGfxObject;
import urender.engine.UMaterial;
import urender.engine.UMesh;
import urender.engine.UTexture;
import urender.engine.shader.UShader;
import urender.engine.shader.UShaderProgram;
import urender.g3dio.ugfx.adapters.IGfxResourceConsumer;
import urender.g3dio.ugfx.adapters.IGfxResourceProvider;
import urender.scenegraph.UGfxScenegraphObject;
import urender.scenegraph.UModel;
import urender.scenegraph.USceneNode;

public class USceneNodeGfxResourceAdapter implements IGfxResourceConsumer, IGfxResourceProvider {

	private final USceneNode node;

	private List<Object> outputList = null;
	private Iterator<Object> outputListIter = null;

	public USceneNodeGfxResourceAdapter(USceneNode node) {
		this.node = node;
	}

	private void ensureOutputList() {
		if (outputList == null) {
			outputList = new ArrayList<>();
			outputList.addAll(node.textures);
			outputList.addAll(node.meshes);
			outputList.addAll(node.materials);
			outputList.addAll(node.shaders);
			outputList.addAll(node.programs);
			outputList.addAll(node.models);
			outputListIter = outputList.iterator();
		}
	}

	@Override
	public void loadObject(Object obj) {
		if (obj instanceof UGfxEngineObject) {
			UGfxEngineObject gfxObj = (UGfxEngineObject) obj;

			switch (gfxObj.getType()) {
				case MATERIAL:
					node.materials.add((UMaterial) gfxObj);
					break;
				case MESH:
					node.meshes.add((UMesh) gfxObj);
					break;
				case PROGRAM:
					node.programs.add((UShaderProgram) gfxObj);
					break;
				case SHADER:
					node.shaders.add((UShader) gfxObj);
					break;
				case TEXTURE:
					node.textures.add((UTexture) gfxObj);
					break;
			}
		} else if (obj instanceof UGfxScenegraphObject) {
			UGfxScenegraphObject sgObj = (UGfxScenegraphObject) obj;
			
			switch (sgObj.getType()) {
				case MODEL:
					node.models.add((UModel) sgObj);
					break;
			}
		}
	}

	@Override
	public Object nextObject() {
		ensureOutputList();
		if (outputListIter.hasNext()) {
			return outputListIter.next();
		} else {
			return null;
		}
	}
}
