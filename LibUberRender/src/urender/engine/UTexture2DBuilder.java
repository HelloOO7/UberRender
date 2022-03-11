package urender.engine;

import java.nio.ByteBuffer;

/**
 * Single-face 2D texture resource builder.
 */
public class UTexture2DBuilder extends UTextureBuilder {

	private ByteBuffer data;

	/**
	 * Sets the 2D texture data buffer.
	 *
	 * @param data
	 * @return this
	 */
	public UTexture2DBuilder setData(ByteBuffer data) {
		this.data = data;
		return this;
	}

	@Override
	public UTexture build() {
		return new UTexture2D(name, width, height, format, data, swizzleMask);
	}

}
