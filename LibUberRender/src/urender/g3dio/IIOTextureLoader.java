package urender.g3dio;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import urender.api.UTextureFormat;
import urender.engine.UTexture;
import urender.engine.UTexture2D;

public class IIOTextureLoader {

	public static UTexture createIIOTexture(InputStream in, String name) {
		try {
			BufferedImage img = ImageIO.read(in);

			UTexture2D tex = new UTexture2D();

			tex.name = name;
			tex.width = img.getWidth();
			tex.height = img.getHeight();
			tex.format = UTextureFormat.RGBA8;
			tex.data = ByteBuffer.allocateDirect(tex.width * tex.height * 4); //bpp32
			convImageBufferRGBA(img, true, tex.data);

			return tex;
		} catch (IOException ex) {
			Logger.getLogger(IIOTextureLoader.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	private static void convImageBufferRGBA(BufferedImage r, boolean doFlip, ByteBuffer out) {
		int w = r.getWidth();
		int h = r.getHeight();
		int pixel;
		int alpha;
		if (!doFlip) {
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					pixel = r.getRGB(x, y);
					out.put((byte) ((pixel >> 16) & 0xFF));
					out.put((byte) ((pixel >> 8) & 0xFF));
					out.put((byte) ((pixel >> 0) & 0xFF));
					alpha = ((pixel >> 24) & 0xFF);
					out.put((byte) alpha);
				}
			}
		}
		else {
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					pixel = r.getRGB(x, y);
					out.position((h - y - 1) * w * 4 + x * 4);
					out.put((byte) ((pixel >> 16) & 0xFF));
					out.put((byte) ((pixel >> 8) & 0xFF));
					out.put((byte) ((pixel >> 0) & 0xFF));
					alpha = ((pixel >> 24) & 0xFF);
					out.put((byte) alpha);
				}
			}
		}
	}
}
