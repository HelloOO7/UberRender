package urender.demo.editor;

import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import urender.api.UTextureType;
import urender.engine.UTexture;
import urender.engine.UTexture2D;

public class TextureEditHandle implements IEditHandle<UTexture> {

	public final UTexture tex;

	public final ImageIcon icon;

	public TextureEditHandle(UTexture texture) {
		this.tex = texture;

		if (texture.getTextureType() == UTextureType.TEX2D) {
			BufferedImage image = decodeTex((UTexture2D) tex);
			icon = new ImageIcon(image);
		}
		else {
			icon = null;
		}
	}

	private static BufferedImage decodeTex(UTexture2D tex) {
		BufferedImage img = new BufferedImage(tex.width, tex.height, BufferedImage.TYPE_INT_ARGB);
		tex.data.rewind();
		for (int y = 0; y < tex.height; y++) {
			for (int x = 0; x < tex.width; x++) {
				img.setRGB(x, tex.height - y - 1, convColor(tex.data.getInt()));
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
