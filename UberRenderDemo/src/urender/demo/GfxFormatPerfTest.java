package urender.demo;

import java.io.File;
import urender.g3dio.generic.OBJModelLoader;
import urender.g3dio.ugfx.UGfxResource;
import urender.scenegraph.io.USceneNodeGfxResourceAdapter;
import urender.scenegraph.USceneNode;
import urender.scenegraph.io.UScenegraphGfxResourceLoader;

public class GfxFormatPerfTest {

	public static void main(String[] args) {
		testWavefront(10);
		testGfx(10);
	}

	private static void testWavefront(int nTests) {
		File srcFile = new File("src\\urender\\demo\\model\\untitled_uv.obj");

		OBJModelLoader.createOBJModelSceneNode(srcFile); //warm up the JIT
		
		long allBegin = System.currentTimeMillis();
		long begin = allBegin;
		long end = allBegin;
		
		for (int i = 0; i < nTests; i++) {
			OBJModelLoader.createOBJModelSceneNode(srcFile);
			
			end = System.currentTimeMillis();
			System.out.println("Took " + (end - begin) + " ms");
			begin = end;
		}
		
		System.out.println("Took on average " + (end - allBegin) / nTests + " ms.");
	}
	
	private static void testGfx(int nTests) {
		File srcFile = new File("src\\urender\\demo\\model\\Demo.gfx");
		
		USceneNodeGfxResourceAdapter adapter = new USceneNodeGfxResourceAdapter(new USceneNode());

		UGfxResource.loadResourceFile(srcFile, UScenegraphGfxResourceLoader.getInstance(), adapter); //warm up the JIT
		
		long allBegin = System.currentTimeMillis();
		long begin = allBegin;
		long end = allBegin;
		
		for (int i = 0; i < nTests; i++) {
			UGfxResource.loadResourceFile(srcFile, UScenegraphGfxResourceLoader.getInstance(), adapter);
			
			end = System.currentTimeMillis();
			System.out.println("Took " + (end - begin) + " ms");
			begin = end;
		}
		
		System.out.println("Took on average " + (end - allBegin) / nTests + " ms.");
	}
}
