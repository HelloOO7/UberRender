package urender.engine;

import urender.api.UFramebufferAttachment;
import urender.api.UObjHandle;
import urender.api.UTextureFormat;
import urender.api.UTextureMagFilter;
import urender.api.UTextureMinFilter;
import urender.api.UTextureType;
import urender.api.UTextureWrap;
import urender.api.backend.RenderingBackend;

public class URenderTarget extends UGfxObject {

	final UObjHandle __textureHandle = new UObjHandle();
	final UObjHandle __drawBufferHandle = new UObjHandle();

	private final int index;

	private final UFramebufferAttachment attachment;
	private final UTextureFormat format;

	private int width;
	private int height;

	private final UObjHandle widthHandle = new UObjHandle();
	private final UObjHandle heightHandle = new UObjHandle();

	public URenderTarget(int index, String textureName, UFramebufferAttachment attachment, UTextureFormat format) {
		this.name = textureName;
		this.index = index;
		this.attachment = attachment;
		this.format = format;
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

	public void setup(UGfxRenderer rnd) {
		RenderingBackend core = rnd.getCore();

		if (!__textureHandle.isInitialized(core)) {
			core.texInit(__textureHandle, UTextureType.TEX2D);
			core.texSetParams(__textureHandle, UTextureType.TEX2D, UTextureWrap.CLAMP_TO_EDGE, UTextureWrap.CLAMP_TO_EDGE, UTextureMagFilter.LINEAR, UTextureMinFilter.LINEAR);
			__textureHandle.forceUpload(core);
		}
		if (!__drawBufferHandle.isInitialized(core)) {
			core.drawBufferInit(__drawBufferHandle, attachment, index);
		}
		
		int nowWidth = -1;
		int nowHeight = -1;
		if (widthHandle.isInitialized(core)) {
			nowWidth = widthHandle.getValue(core);
		}
		if (heightHandle.isInitialized(core)) {
			nowHeight = heightHandle.getValue(core);
		}
		if (nowHeight != height || nowWidth != width) {
			core.texUploadData2D(__textureHandle, width, height, format, null, null);
			widthHandle.initialize(core, width);
			heightHandle.initialize(core, height);
		}
	}

	public void bind(UObjHandle fbHandle, UGfxRenderer rnd) {
		rnd.getCore().drawBufferTextureSet(fbHandle, __textureHandle, __drawBufferHandle);
	}
}
