package urender.g3dio.generic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import urender.api.UTextureFormat;
import urender.api.UTextureSwizzleChannel;
import urender.common.fs.FSUtil;
import urender.engine.UTexture;
import urender.engine.UTexture2D;
import urender.engine.UTextureSwizzleMask;

public class IIOTextureLoader {

	public static UTexture createIIOTexture(File file) {
		try (FileInputStream in = new FileInputStream(file)) {
			return createIIOTexture(in, FSUtil.getFileNameWithoutExtension(file.getName()));
		} catch (IOException ex) {
			Logger.getLogger(IIOTextureLoader.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	private static UTextureFormat getFormatByMaxSwizzle(UTextureSwizzleChannel... channels) {
		UTextureSwizzleChannel max = UTextureSwizzleChannel.R;
		for (UTextureSwizzleChannel c : channels) {
			if (isWriteSwizzle(c)) {
				boolean pass = false;
				switch (c) {
					case A:
						pass = true;
						break;
					case B:
						pass = max != UTextureSwizzleChannel.A;
						break;
					case G:
						pass = max == UTextureSwizzleChannel.R;
						break;
					case R:
						pass = false;
						break;
				}
				if (pass) {
					max = c;
				}
			}
		}

		switch (max) {
			case A:
				return UTextureFormat.RGBA8;
			case B:
				return UTextureFormat.RGB8;
			case G:
				return UTextureFormat.RG8;
			case R:
				return UTextureFormat.R8;
		}

		throw new RuntimeException();
	}

	public static UTexture createIIOTexture(InputStream in, String name) {
		try {
			BufferedImage img = ImageIO.read(in);

			ConvOutput output = convImageBufferRGBA(img);
			UTextureSwizzleMask swizzleMask = output.swizzleMask;

			return new UTexture2D(name, img.getWidth(), img.getHeight(), getFormatByMaxSwizzle(swizzleMask.r, swizzleMask.g, swizzleMask.b, swizzleMask.a), output.data, swizzleMask);
		} catch (IOException ex) {
			Logger.getLogger(IIOTextureLoader.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	private static UTextureSwizzleChannel getLastValidSwizzle(UTextureSwizzleChannel... swizzles) {
		for (UTextureSwizzleChannel channel : swizzles) {
			if (channel != UTextureSwizzleChannel.ZERO && channel != UTextureSwizzleChannel.ONE) {
				return channel;
			}
		}
		return null;
	}

	private static UTextureSwizzleChannel getSwizzle(int constVal, UTextureSwizzleChannel lastValidSwizzle) {
		if (constVal == 0) {
			return UTextureSwizzleChannel.ZERO;
		} else if (constVal == 255) {
			return UTextureSwizzleChannel.ONE;
		}

		if (lastValidSwizzle == null) {
			return UTextureSwizzleChannel.R;
		} else {
			switch (lastValidSwizzle) {
				case A:
					throw new RuntimeException();
				case B:
					return UTextureSwizzleChannel.A;
				case G:
					return UTextureSwizzleChannel.B;
				case R:
					return UTextureSwizzleChannel.G;
			}
		}
		return null;
	}

	private static boolean isWriteSwizzle(UTextureSwizzleChannel chan) {
		return chan != UTextureSwizzleChannel.ZERO && chan != UTextureSwizzleChannel.ONE;
	}

	private static int calcBytesPerPixel(UTextureSwizzleChannel... swizzles) {
		HashSet<UTextureSwizzleChannel> uniqueSwizzles = new HashSet<>();
		for (UTextureSwizzleChannel c : swizzles) {
			if (!uniqueSwizzles.contains(c)) {
				uniqueSwizzles.add(c);
			}
		}
		int bpp = 0;
		for (UTextureSwizzleChannel c : uniqueSwizzles) {
			if (isWriteSwizzle(c)) {
				bpp++;
			}
		}
		return bpp;
	}

	public static int calcRowStride(int width, int Bpp) {
		int bytes = width * Bpp;
		int mod = bytes & 0x3;
		if (mod != 0) {
			bytes += 4 - mod;
		}
		return bytes;
	}

	private static int checkUpdateConst(int nowConstVal, int newVal) {
		if (newVal != nowConstVal) {
			if (nowConstVal == -1) {
				return newVal;
			}
		}
		else {
			return nowConstVal;
		}
		return -2;
	}

	private static ConvOutput convImageBufferRGBA(BufferedImage img) {
		int w = img.getWidth();
		int h = img.getHeight();
		int pixel;

		int constR = -1;
		int constG = -1;
		int constB = -1;
		int constA = -1;

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				pixel = img.getRGB(x, y);
				int r = (pixel >> 16) & 0xFF;
				int g = (pixel >> 8) & 0xFF;
				int b = (pixel >> 0) & 0xFF;
				int a = (pixel >> 24) & 0xFF;

				constR = checkUpdateConst(constR, r);
				constG = checkUpdateConst(constG, g);
				constB = checkUpdateConst(constB, b);
				constA = checkUpdateConst(constA, a);
			}
		}

		UTextureSwizzleChannel swizzleR = getSwizzle(constR, null);
		UTextureSwizzleChannel swizzleG = getSwizzle(constG, getLastValidSwizzle(swizzleR));
		UTextureSwizzleChannel swizzleB = getSwizzle(constB, getLastValidSwizzle(swizzleG, swizzleR));
		UTextureSwizzleChannel swizzleA = getSwizzle(constA, getLastValidSwizzle(swizzleB, swizzleG, swizzleR));
		if (getLastValidSwizzle(swizzleR, swizzleG, swizzleB, swizzleA) == null) {
			swizzleR = UTextureSwizzleChannel.R; //zero-channel texture does not exist
		}

		int Bpp = calcBytesPerPixel(swizzleR, swizzleG, swizzleB, swizzleA);
		int stride = calcRowStride(w, Bpp);
		//System.out.println("swizzle " + swizzleR + " | " + swizzleG + " | " + swizzleB + " | " + swizzleA);

		ByteBuffer data = ByteBuffer.allocate(stride * h);
		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				pixel = img.getRGB(x, y);
				data.position((h - y - 1) * stride + x * Bpp);
				if (isWriteSwizzle(swizzleR)) {
					data.put((byte) ((pixel >> 16) & 0xFF));
				}
				if (isWriteSwizzle(swizzleG)) {
					data.put((byte) ((pixel >> 8) & 0xFF));
				}
				if (isWriteSwizzle(swizzleB)) {
					data.put((byte) ((pixel >> 0) & 0xFF));
				}
				if (isWriteSwizzle(swizzleA)) {
					data.put((byte) ((pixel >> 24) & 0xFF));
				}
			}
		}

		ConvOutput output = new ConvOutput();
		output.data = data;
		output.swizzleMask = new UTextureSwizzleMask(swizzleR, swizzleG, swizzleB, swizzleA);
		return output;
	}
	
	private static class ConvOutput {
		public UTextureSwizzleMask swizzleMask;
		public ByteBuffer data;
	}
}
