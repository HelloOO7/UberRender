package urender.demo.editor;

import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import urender.api.UTextureFormat;
import urender.api.UTextureSwizzleChannel;
import urender.api.UTextureType;
import urender.engine.UTexture;
import urender.engine.UTexture2D;
import urender.g3dio.generic.IIOTextureLoader;

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
		int bpp = 0;
		switch (tex.format) {
			case R8:
				bpp = 1;
				break;
			case RG8:
				bpp = 2;
				break;
			case RGB8:
				bpp = 3;
				break;
			case RGBA8:
				bpp = 4;
				break;
			default:
				return null;
		}
		int width = tex.getWidth();
		int height = tex.getHeight();
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int stride = IIOTextureLoader.calcRowStride(width, bpp);
		
		byte[] temp = new byte[bpp];
		
		for (int y = 0; y < height; y++) {
			tex.data.position(y * stride);
			for (int x = 0; x < width; x++) {
				tex.data.get(temp);
				
				int r = getUnswizzledComponent(temp, tex.swizzleMask.r);
				int g = getUnswizzledComponent(temp, tex.swizzleMask.g);
				int b = getUnswizzledComponent(temp, tex.swizzleMask.b);
				int a = getUnswizzledComponent(temp, tex.swizzleMask.a);
				img.setRGB(x, height - y - 1, (a << 24) | (r << 16) | (g << 8) | (b << 0));
			}
		}
		return img;
	}
	
	private static int getUnswizzledComponent(byte[] rawData, UTextureSwizzleChannel swizzle) {
		switch (swizzle) {
			case ONE:
				return 255;
			case ZERO:
				return 0;
			case R:
				return rawData[0] & 255;
			case G:
				return rawData.length > 1 ? rawData[1] & 255 : 0;
			case B:
				return rawData.length > 2 ? rawData[2] & 255 : 0;
			case A:
				return rawData.length > 3 ? rawData[3] & 255 : 0;
		}
		return 0;
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
