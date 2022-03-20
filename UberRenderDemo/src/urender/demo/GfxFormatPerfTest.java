package urender.demo;

import java.io.File;
import urender.engine.UTexture;
import urender.g3dio.generic.OBJModelLoader;
import urender.g3dio.ugfx.UGfxResource;
import urender.scenegraph.io.USceneNodeGfxResourceAdapter;
import urender.scenegraph.USceneNode;
import urender.scenegraph.io.UScenegraphGfxResourceLoader;

public class GfxFormatPerfTest {

	public static void main(String[] args) {
		//testWavefront(10);
		testGfx(10);
	}

	private static void testWavefront(int nTests) {		
		File[] srcFiles = new File[]{
			new File("_internal_testdata/copter/SmHelicopter.obj"),
			new File("_internal_testdata/star/star.obj"),
			new File("_internal_testdata/ufc/UFC.obj"),
		};

		for (File srcFile : srcFiles) {

			OBJModelLoader.createOBJModelSceneNode(srcFile); //warm up the JIT

			long allBegin = System.currentTimeMillis();
			long begin = allBegin;
			long end = allBegin;

			for (int i = 0; i < nTests; i++) {
				OBJModelLoader.createOBJModelSceneNode(srcFile);

				end = System.currentTimeMillis();
				//System.out.println("Took " + (end - begin) + " ms");
				begin = end;
			}

			System.out.println("File " + srcFile + " took on average " + (end - allBegin) / nTests + " ms.");
		}
	}

	private static void testGfx(int nTests) {
		File[] srcFiles = new File[]{
			new File("Helicopter2.gfx"),
			new File("StarTest.gfx"),
			new File("UFC.gfx")
		};

		for (File srcFile : srcFiles) {
			USceneNode node = new USceneNode();
			
			USceneNodeGfxResourceAdapter adapter = new USceneNodeGfxResourceAdapter(node);

			UGfxResource.loadResourceFile(srcFile, UScenegraphGfxResourceLoader.getInstance(), adapter); //warm up the JIT

			long allBegin = System.currentTimeMillis();
			long begin = allBegin;
			long end = allBegin;

			for (int i = 0; i < nTests; i++) {
				UGfxResource.loadResourceFile(srcFile, UScenegraphGfxResourceLoader.getInstance(), adapter);

				end = System.currentTimeMillis();
				//System.out.println("Took " + (end - begin) + " ms");
				begin = end;
			}

			System.out.println("File " + srcFile + " took on average " + (end - allBegin) / nTests + " ms.");
			
			int pixelCount = 0;
			for (UTexture tex : node.textures) {
				pixelCount += tex.getWidth() * tex.getHeight();
			}
			System.out.println("Texture pixel count " + pixelCount);
		}
	}
}
