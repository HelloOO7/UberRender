package urender.demo;

import java.io.File;
import urender.common.fs.FSUtil;
import urender.g3dio.generic.OBJModelLoader;
import urender.g3dio.ugfx.UGfxResource;
import urender.scenegraph.io.USceneNodeGfxResourceAdapter;
import urender.scenegraph.USceneNode;
import urender.scenegraph.io.UScenegraphGfxResourceLoader;

public class GfxConverterDemo {
	
	private static void doConvModel(String objName, String gfxName) {
		USceneNode inputNode = OBJModelLoader.createOBJModelSceneNode("urender/demo/model", objName);
		File gfxFile = new File(gfxName);
		File copyFile = new File("src/urender/demo/model/" + gfxName);
		USceneNode outputNode = new USceneNode();

		UGfxResource.writeResourceFile(gfxFile, UScenegraphGfxResourceLoader.getInstance(), new USceneNodeGfxResourceAdapter(inputNode));

		UGfxResource.loadResourceFile(gfxFile, UScenegraphGfxResourceLoader.getInstance(), new USceneNodeGfxResourceAdapter(outputNode));

		FSUtil.copy(gfxFile, copyFile);
	}

	public static void main(String[] args) {
		doConvModel("untitled_uv.obj", "Demo.gfx");
		doConvModel("FillScreenQuad.obj", "FillScreenQuad.gfx");
	}
}
