package urender.demo;

import java.io.File;
import urender.g3dio.generic.OBJModelLoader;
import urender.g3dio.ugfx.UGfxResource;
import urender.scenegraph.io.USceneNodeGfxResourceAdapter;
import urender.scenegraph.USceneNode;
import urender.scenegraph.io.UScenegraphGfxResourceLoader;

public class GfxConverterDemo {

	public static void main(String[] args) {
		USceneNode inputNode = OBJModelLoader.createOBJModelSceneNode("urender/demo/model", "untitled_uv.obj");
		File gfxFile = new File("src/urender/demo/model/Demo.gfx");
		USceneNode outputNode = new USceneNode();

		UGfxResource.writeResourceFile(gfxFile, UScenegraphGfxResourceLoader.getInstance(), new USceneNodeGfxResourceAdapter(inputNode));
		
		UGfxResource.loadResourceFile(gfxFile, UScenegraphGfxResourceLoader.getInstance(), new USceneNodeGfxResourceAdapter(outputNode));
	}
}
