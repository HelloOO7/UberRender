package urender.engine;

import java.nio.ByteBuffer;

public class UTexture2DBuilder extends UTextureBuilder {

	private ByteBuffer data;
	
	public UTexture2DBuilder setData(ByteBuffer data) {
		this.data = data;
		return this;
	}
	
	@Override
	public UTexture build() {
		return new UTexture2D(name, width, height, format, data);
	}

}
