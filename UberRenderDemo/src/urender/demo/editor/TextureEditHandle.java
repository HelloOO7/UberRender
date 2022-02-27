package urender.demo.editor;

import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import urender.api.UTextureFormat;
import urender.api.UTextureType;
import urender.engine.UTexture;
import urender.engine.UTexture2D;

public class TextureEditHandle implements IEditHandle<UTexture> {

	public final UTexture tex;

	public ImageIcon icon;

	public TextureEditHandle(UTexture texture) {
		this.tex = texture;

		if (texture.getTextureType() == UTextureType.TEX2D) {
			BufferedImage image = decodeTex((UTexture2D) tex);
			if (image != null) {
				icon = new ImageIcon(image);
			}
		}
	}

	private static BufferedImage decodeTex(UTexture2D tex) {
		if (tex.format != UTextureFormat.RGBA8) {
			return null;
		}
		int width = tex.getWidth();
		int height = tex.getHeight();
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		tex.data.rewind();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				img.setRGB(x, height - y - 1, convColor(tex.data.getInt()));
			}
		}
		return img;
	}

	private static int convColor(int color) {
		int a = (color >> 0) & 0xFF;
		int b = (color >> 8) & 0xFF;
		int g = (color >> 16) & 0xFF;
		int r = (color >> 24) & 0xFF;
		return (a << 24) | (r << 16) | (g << 8) | (b << 0);
	}

	@Override
	public String toString() {
		return tex.getName();
	}

	@Override
	public void save() {

	}

	@Override
	public UTexture getContent() {
		return tex;
	}
}
