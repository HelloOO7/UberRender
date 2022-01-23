package urender.api.backend;

import com.jogamp.opengl.GL4;
import urender.api.UBufferType;
import urender.api.UBufferUsageHint;
import urender.api.UDataType;
import urender.api.UFramebufferAttachment;
import urender.api.UPrimitiveType;
import urender.api.UShaderType;
import urender.api.UTextureFaceAssignment;
import urender.api.UTextureFormat;
import urender.api.UTextureMagFilter;
import urender.api.UTextureMinFilter;
import urender.api.UTextureType;
import urender.api.UTextureWrap;

public class GLAPITranslator implements APITranslator {

	@Override
	public int getDataType(UDataType type, boolean unsigned) {
		switch (type) {
			case FLOAT16:
				return GL4.GL_HALF_FLOAT;
			case FLOAT32:
				return GL4.GL_FLOAT;
			case FLOAT64:
				return GL4.GL_DOUBLE;
			case INT16:
				return unsigned ? GL4.GL_UNSIGNED_SHORT : GL4.GL_SHORT;
			case INT32:
				return unsigned ? GL4.GL_UNSIGNED_INT : GL4.GL_INT;
			case INT8:
				return unsigned ? GL4.GL_UNSIGNED_BYTE : GL4.GL_BYTE;
		}
		return GL4.GL_INVALID_ENUM;
	}

	@Override
	public int getPrimitiveType(UPrimitiveType type) {
		switch (type) {
			case TRIS:
				return GL4.GL_TRIANGLES;
			case LINES:
				return GL4.GL_LINES;
			case POINTS:
				return GL4.GL_POINTS;
		}
		return GL4.GL_INVALID_ENUM;
	}

	@Override
	public int getTextureFormatDataType(UTextureFormat format) {
		switch (format) {
			case DEPTH_COMPONENT24:
			case FLOAT32:
				return GL4.GL_FLOAT;
			case RGBA16F:
				return GL4.GL_HALF_FLOAT;
			case R8:
			case RG8:
			case RGB8:
			case RGBA8:
			case STENCIL_INDEX8:
				return GL4.GL_UNSIGNED_BYTE;
			case DEPTH24_STENCIL8:
				return GL4.GL_UNSIGNED_INT_24_8;
		}
		return GL4.GL_INVALID_ENUM;
	}

	@Override
	public int getTextureFormatInternalFormat(UTextureFormat format) {
		switch (format) {
			case FLOAT32:
				return GL4.GL_R32F;
			case RGBA16F:
				return GL4.GL_RGBA16F;
			case R8:
				return GL4.GL_R8;
			case RG8:
				return GL4.GL_RG8;
			case RGB8:
				return GL4.GL_RGB8;
			case RGBA8:
				return GL4.GL_RGBA8;
			case DEPTH24_STENCIL8:
				return GL4.GL_DEPTH24_STENCIL8;
			case DEPTH_COMPONENT24:
				return GL4.GL_DEPTH_COMPONENT24;
			case STENCIL_INDEX8:
				return GL4.GL_STENCIL_INDEX8;
		}
		return GL4.GL_INVALID_ENUM;
	}

	@Override
	public int getTextureFormatExternalFormat(UTextureFormat format) {
		switch (format) {
			case FLOAT32:
			case R8:
				return GL4.GL_RED;
			case RG8:
				return GL4.GL_RG;
			case RGB8:
				return GL4.GL_RGB;
			case RGBA8:
			case RGBA16F:
				return GL4.GL_RGBA;
			case DEPTH24_STENCIL8:
				return GL4.GL_DEPTH_STENCIL;
			case DEPTH_COMPONENT24:
				return GL4.GL_DEPTH_COMPONENT;
			case STENCIL_INDEX8:
				return GL4.GL_STENCIL_INDEX;
		}
		return GL4.GL_INVALID_ENUM;
	}

	@Override
	public int getTextureType(UTextureType t) {
		switch (t) {
			case TEX2D:
				return GL4.GL_TEXTURE_2D;
			case TEX2D_CUBEMAP:
				return GL4.GL_TEXTURE_CUBE_MAP;
			case TEX3D:
				return GL4.GL_TEXTURE_3D;
		}
		return GL4.GL_INVALID_ENUM;
	}

	@Override
	public int getTextureFaceAssignment(UTextureFaceAssignment asgn) {
		if (asgn != null) {
			switch (asgn) {
				case X_NEG:
					return GL4.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
				case Y_NEG:
					return GL4.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
				case Z_NEG:
					return GL4.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
				case X_POS:
					return GL4.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
				case Y_POS:
					return GL4.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
				case Z_POS:
					return GL4.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
			}
		}
		return GL4.GL_TEXTURE_2D;
	}

	@Override
	public int getBufferTargetType(UBufferType bufType) {
		return bufType == UBufferType.VBO ? GL4.GL_ARRAY_BUFFER : GL4.GL_ELEMENT_ARRAY_BUFFER;
	}

	@Override
	public int getBufferUsage(UBufferUsageHint usage) {
		switch (usage) {
			case STATIC:
				return GL4.GL_STATIC_DRAW;
			case DYNAMIC:
				return GL4.GL_DYNAMIC_DRAW;
			case STREAM:
				return GL4.GL_STREAM_DRAW;
		}
		return GL4.GL_INVALID_ENUM;
	}

	@Override
	public int getShaderType(UShaderType type) {
		return type == UShaderType.FRAGMENT ? GL4.GL_FRAGMENT_SHADER : GL4.GL_VERTEX_SHADER;
	}

	@Override
	public int getTextureWrap(UTextureWrap wrap) {
		switch (wrap) {
			case CLAMP_TO_BORDER:
				return GL4.GL_CLAMP_TO_BORDER;
			case CLAMP_TO_EDGE:
				return GL4.GL_CLAMP_TO_EDGE;
			case REPEAT:
				return GL4.GL_REPEAT;
			case MIRRORED_REPEAT:
				return GL4.GL_MIRRORED_REPEAT;
		}
		return GL4.GL_INVALID_ENUM;
	}

	@Override
	public int getTextureMagFilter(UTextureMagFilter f) {
		switch (f) {
			case LINEAR:
				return GL4.GL_LINEAR;
			case NEAREST_NEIGHBOR:
				return GL4.GL_NEAREST;
		}
		return GL4.GL_INVALID_ENUM;
	}

	@Override
	public int getTextureMinFilter(UTextureMinFilter f) {
		switch (f) {
			case LINEAR_MIPMAP_LINEAR:
			case LINEAR_MIPMAP_NEAREST:
			case LINEAR:
				return GL4.GL_LINEAR;
			case NEAREST_MIPMAP_LINEAR:
			case NEAREST_MIPMAP_NEAREST:
			case NEAREST_NEIGHBOR:
				return GL4.GL_NEAREST;
		}
		return GL4.GL_INVALID_ENUM;
	}

	@Override
	public int getFramebufferAttachment(UFramebufferAttachment attachment, int index) {
		if (attachment == UFramebufferAttachment.COLOR) {
			return GL4.GL_COLOR_ATTACHMENT0 + index;
		} else if (index == 0) {
			//Only one depth/stencil buffer is supported
			switch (attachment) {
				case DEPTH:
					return GL4.GL_DEPTH_ATTACHMENT;
				case DEPTH_STENCIL:
					return GL4.GL_DEPTH_STENCIL_ATTACHMENT;
				case STENCIL:
					return GL4.GL_STENCIL_ATTACHMENT;
			}
		}

		return GL4.GL_INVALID_ENUM;
	}
}
