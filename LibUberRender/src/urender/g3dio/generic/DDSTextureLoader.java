package urender.g3dio.generic;

import java.io.DataInput;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import urender.api.UTextureFormat;
import urender.common.fs.FSUtil;
import urender.common.io.InvalidMagicException;
import urender.common.io.base.impl.ext.data.DataInStream;
import urender.common.io.util.StringIO;
import urender.engine.UTexture;
import urender.engine.UTexture2DBuilder;

/**
 * DirectDraw Surface texture resource loader.
 * 
 * This is VERY barebones and currently is only for some experimental heightmap textures.
 */
public class DDSTextureLoader {

	private static final String DDS_MAGIC = "DDS ";

	public static UTexture createDDSTexture(File file) {
		try (FileInputStream in = new FileInputStream(file)) {
			return createDDSTexture(in, FSUtil.getFileNameWithoutExtension(file.getName()));
		} catch (IOException ex) {
			Logger.getLogger(DDSTextureLoader.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public static UTexture createDDSTexture(InputStream in, String name) {
		try {
			DataInStream dis = new DataInStream(in);

			if (!StringIO.checkMagic(dis, DDS_MAGIC)) {
				throw new InvalidMagicException("DDS magic invalid.");
			}

			int headerSize = dis.readInt();
			int flags = dis.readInt();

			int height = dis.readInt();
			int width = dis.readInt();
			int pitchOrLinearSize = dis.readInt();
			int depth = dis.readInt();
			int mipCount = dis.readInt();
			dis.skipBytes(11 * Integer.BYTES); //reserved
			DDSPixelFormat format = new DDSPixelFormat(dis);
			int caps = dis.readInt();
			int caps2 = dis.readInt();
			int caps3 = dis.readInt();
			int caps4 = dis.readInt();
			int reserved2 = dis.readInt();

			dis.seekNext(headerSize + DDS_MAGIC.length());

			int rowStride = calcRowStride(width, format.rgbBitCount);
			int rowSize = (width * format.rgbBitCount) >> 3;
			
			int alignedSize = rowStride * height;
			
			if (alignedSize < pitchOrLinearSize) {
				throw new RuntimeException("Too much data!");
			}
			byte[] alignedData = new byte[alignedSize];
			for (int row = 0; row < height; row++) {
				dis.read(alignedData, row * rowStride, rowSize); //rest is padding
			}

			UTexture2DBuilder builder = new UTexture2DBuilder();
			builder
				.setData(ByteBuffer.wrap(alignedData))
				.setWidth(width)
				.setHeight(height)
				.setName(name)
				.setFormat(UTextureFormat.R16F);

			dis.close();

			return builder.build();
		} catch (IOException ex) {
			Logger.getLogger(DDSTextureLoader.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}
	
	public static int calcRowStride(int width, int bpp) {
		int bytes = (width * bpp) >> 3;
		int mod = bytes & 0x3;
		if (mod != 0) {
			bytes += 4 - mod;
		}
		return bytes;
	}

	private static class DDSPixelFormat {

		public int size;
		public int flags;
		public int fourCC;
		public int rgbBitCount;

		public int bitMaskR;
		public int bitMaskG;
		public int bitMaskB;
		public int bitMaskA;

		public DDSPixelFormat(DataInput in) throws IOException {
			size = in.readInt();
			flags = in.readInt();
			fourCC = in.readInt();
			rgbBitCount = in.readInt();
			bitMaskR = in.readInt();
			bitMaskG = in.readInt();
			bitMaskB = in.readInt();
			bitMaskA = in.readInt();
		}
	}
}
