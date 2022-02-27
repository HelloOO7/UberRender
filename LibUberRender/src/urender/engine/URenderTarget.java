package urender.engine;

import urender.api.UFramebufferAttachment;
import urender.api.UObjHandle;
import urender.api.UTextureFormat;
import urender.api.UTextureMagFilter;
import urender.api.UTextureMinFilter;
import urender.api.UTextureType;
import urender.api.UTextureWrap;
import urender.api.backend.RenderingBackend;

public class URenderTarget extends UTexture {

	final UObjHandle __drawBufferHandle = new UObjHandle();

	private final int index;

	private final UFramebufferAttachment attachment;

	private final UObjHandle widthHandle = new UObjHandle();
	private final UObjHandle heightHandle = new UObjHandle();

	public URenderTarget(int index, String textureName, UFramebufferAttachment attachment, UTextureFormat format) {
		super(textureName, 0, 0, format);
		this.index = index;
		this.attachment = attachment;
	}
	
	public int getDrawBufferIndex() {
		return index;
	}
	
	public UFramebufferAttachment getAttachment() {
		return attachment;
	}

	public void setResolution(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void setup(RenderingBackend rnd) {
		UObjHandle __textureHandle = __handle;
		
		if (!__textureHandle.isInitialized(rnd)) {
			rnd.texInit(__textureHandle, UTextureType.TEX2D);
			rnd.texSetParams(__textureHandle, UTextureType.TEX2D, UTextureWrap.CLAMP_TO_EDGE, UTextureWrap.CLAMP_TO_EDGE, UTextureMagFilter.LINEAR, UTextureMinFilter.LINEAR);
			__textureHandle.forceUpload(rnd);
		}
		if (!__drawBufferHandle.isInitialized(rnd)) {
			rnd.drawBufferInit(__drawBufferHandle, attachment, index);
		}
		
		int nowWidth = -1;
		int nowHeight = -1;
		if (widthHandle.isInitialized(rnd)) {
			nowWidth = widthHandle.getValue(rnd);
		}
		if (heightHandle.isInitialized(rnd)) {
			nowHeight = heightHandle.getValue(rnd);
		}
		if (nowHeight != height || nowWidth != width) {
			swizzleMask.setup(rnd, this);
			rnd.texUploadData2D(__textureHandle, width, height, format, null, null);
			widthHandle.initialize(rnd, width);
			heightHandle.initialize(rnd, height);
		}
	}

	public void bind(UObjHandle fbHandle, RenderingBackend rnd) {
		rnd.drawBufferTextureSet(fbHandle, __handle, __drawBufferHandle);
	}

	@Override
	public UTextureType getTextureType() {
		return UTextureType.TEX2D;
	}
}
