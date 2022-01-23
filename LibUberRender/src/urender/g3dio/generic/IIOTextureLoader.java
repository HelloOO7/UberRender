package urender.g3dio.generic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import urender.api.UTextureFormat;
import urender.common.fs.FSUtil;
import urender.engine.UTexture;
import urender.engine.UTexture2D;

public class IIOTextureLoader {

	public static UTexture createIIOTexture(File file) {
		try (FileInputStream in = new FileInputStream(file)){
			return createIIOTexture(in, FSUtil.getFileNameWithoutExtension(file.getName()));
		} catch (IOException ex) {
			Logger.getLogger(IIOTextureLoader.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}
	
	public static UTexture createIIOTexture(InputStream in, String name) {
		try {
			BufferedImage img = ImageIO.read(in);

			ByteBuffer data = ByteBuffer.allocateDirect(img.getWidth() * img.getHeight() * 4); //bpp32
			convImageBufferRGBA(img, true, data);

			return new UTexture2D(name, img.getWidth(), img.getHeight(), UTextureFormat.RGBA8, data);
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
