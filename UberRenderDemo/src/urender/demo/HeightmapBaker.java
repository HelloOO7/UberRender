package urender.demo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import urender.common.StringEx;
import urender.common.math.HalfFloat;
import urender.engine.UGfxObject;
import urender.engine.UTexture;
import urender.engine.UTexture2D;
import urender.g3dio.generic.DDSTextureLoader;
import urender.g3dio.ugfx.UGfxResource;
import urender.scenegraph.USceneNode;
import urender.scenegraph.io.USceneNodeGfxResourceAdapter;
import urender.scenegraph.io.UScenegraphGfxResourceLoader;

public class HeightmapBaker {

	public static void main(String[] args) {
		try {
			File inFile = new File("C:\\Users\\Čeněk\\eclipse-workspace\\UberRender\\UberRenderDemo\\Plane.obj");
			File textureGfxRsc = new File("C:\\Users\\Čeněk\\eclipse-workspace\\UberRender\\UberRenderDemo\\HeightmapTest.gfx");
			String textureGfxName = "terrain_1_1_heightmap2";
			File outFile = new File("C:\\Users\\Čeněk\\eclipse-workspace\\UberRender\\UberRenderDemo\\PlaneBaked2.obj");

			USceneNode node = new USceneNode();
			UGfxResource.loadResourceFile(textureGfxRsc, UScenegraphGfxResourceLoader.getInstance(), new USceneNodeGfxResourceAdapter(node));
			UTexture tex = UGfxObject.find(node.textures, textureGfxName);
			if (tex == null) {
				throw new NullPointerException("Texture not found!");
			}
			UTexture2D tex2D = (UTexture2D) tex;
			ByteBuffer data = tex2D.data;
			data.order(ByteOrder.LITTLE_ENDIAN);
			int width = tex.getWidth();
			int height = tex.getHeight();
			int bpp = 2;
			int stride = DDSTextureLoader.calcRowStride(width, bpp << 3);

			float planeMin = -100f;
			float planeMax = 100f;

			float planeDim = (planeMax - planeMin) + 1;
			float invPlaneDim = 1f / planeDim;

			Scanner scanner = new Scanner(inFile);
			BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));

			String line;

			while (scanner.hasNextLine()) {
				line = scanner.nextLine();

				if (line.startsWith("v ")) {
					String[] lineData = StringEx.splitOnecharFastNoBlank(line, ' ');
					float x = Float.parseFloat(lineData[1]);
					float z = Float.parseFloat(lineData[3]);
					if (x > planeMax || z > planeMax) {
						System.out.println("XZ range error x " + x + " z " + z + " max " + planeMax);
					}

					x = (x - planeMin) * invPlaneDim;
					z = (z - planeMin) * invPlaneDim;

					float xIdxF = x * width;
					float yIdxF = z * height;

					float y = sampleY(data, xIdxF, yIdxF, stride, bpp);
					//System.out.println("xz " + x + " x " + z);

					lineData[2] = Float.toString(y);

					line = String.join(" ", lineData);
				}

				writer.write(line);
				writer.newLine();
			}

			scanner.close();
		} catch (IOException ex) {
			Logger.getLogger(HeightmapBaker.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private static float sampleY(ByteBuffer data, int x, int y, int stride, int bpp) {
		int bufPos = y * stride + x * bpp;
		data.position(bufPos);

		float val = HalfFloat.toFloat(data.getShort() & 0xFFFF);
		return val;
	}

	private static float sampleY(ByteBuffer buf, float xIdxF, float yIdxF, int stride, int bpp) {
		float topLeft = sampleY(buf, (int) xIdxF, (int) yIdxF, stride, bpp);
		float topRight = sampleY(buf, (int) Math.ceil(xIdxF), (int) yIdxF, stride, bpp);
		float botLeft = sampleY(buf, (int) xIdxF, (int) Math.ceil(yIdxF), stride, bpp);
		float botRight = sampleY(buf, (int) Math.ceil(xIdxF), (int) Math.ceil(yIdxF), stride, bpp);

		return lerp(lerp(topLeft, topRight, xIdxF - (int) xIdxF), lerp(botLeft, botRight, xIdxF - (int) xIdxF), yIdxF - (int) yIdxF);
	}

	private static float lerp(float val1, float val2, float weight) {
		return val1 + (val2 - val1) * weight;
	}
}
