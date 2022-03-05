package urender.api.backend;

import com.jogamp.opengl.GL4;
import urender.api.UBlendEquation;
import urender.api.UBlendFunction;
import urender.api.UBufferType;
import urender.api.UBufferUsageHint;
import urender.api.UDataType;
import urender.api.UFaceCulling;
import urender.api.UFramebufferAttachment;
import urender.api.UPrimitiveType;
import urender.api.UShaderType;
import urender.api.UTestFunction;
import urender.api.UTextureFaceAssignment;
import urender.api.UTextureFormat;
import urender.api.UTextureMagFilter;
import urender.api.UTextureMinFilter;
import urender.api.UTextureSwizzleChannel;
import urender.api.UTextureType;
import urender.api.UTextureWrap;

public class GLAPITranslator implements APITranslator {

	@Override
	public int getDataType(UDataType type) {
		switch (type) {
			case FLOAT16:
				return GL4.GL_HALF_FLOAT;
			case FLOAT32:
				return GL4.GL_FLOAT;
			case FLOAT64:
				return GL4.GL_DOUBLE;
			case INT16:
				return GL4.GL_SHORT;
			case INT32:
				return GL4.GL_INT;
			case INT8:
				return GL4.GL_BYTE;
			case UINT16:
				return GL4.GL_UNSIGNED_SHORT;
			case UINT32:
				return GL4.GL_UNSIGNED_INT;
			case UINT8:
				return GL4.GL_UNSIGNED_BYTE;
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
			case R16F:
				return GL4.GL_HALF_FLOAT;
			case R8:
			case RG8:
			case RGB8:
			case RGBA8:
			case STENCIL_INDEX8:
				return GL4.GL_UNSIGNED_BYTE;
			case DEPTH24_STENCIL8:
				return GL4.GL_UNSIGNED_INT_24_8;
			case R16UI:
				return GL4.GL_UNSIGNED_SHORT;
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
			case R16F:
				return GL4.GL_R16F;
			case R16UI:
				return GL4.GL_R16UI;
		}
		return GL4.GL_INVALID_ENUM;
	}

	@Override
	public int getTextureFormatExternalFormat(UTextureFormat format) {
		switch (format) {
			case FLOAT32:
			case R8:
			case R16F:
				return GL4.GL_RED;
			case R16UI:
				return GL4.GL_RED_INTEGER;
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

	@Override
	public int getBlendEquation(UBlendEquation eq) {
		switch (eq) {
			case ADD:
				return GL4.GL_FUNC_ADD;
			case MAX:
				return GL4.GL_MAX;
			case MIN:
				return GL4.GL_MIN;
			case REVERSE_SUBTRACT:
				return GL4.GL_FUNC_REVERSE_SUBTRACT;
			case SUBTRACT:
				return GL4.GL_FUNC_SUBTRACT;
		}
		return GL4.GL_INVALID_ENUM;
	}

	@Override
	public int getBlendFunc(UBlendFunction func) {
		switch (func) {
			case CONSTANT_ALPHA:
				return GL4.GL_CONSTANT_ALPHA;
			case CONSTANT_COLOR:
				return GL4.GL_CONSTANT_COLOR;
			case DST_ALPHA:
				return GL4.GL_DST_ALPHA;
			case DST_COLOR:
				return GL4.GL_DST_COLOR;
			case ONE:
				return GL4.GL_ONE;
			case ONE_MINUS_CONSTANT_ALPHA:
				return GL4.GL_ONE_MINUS_CONSTANT_ALPHA;
			case ONE_MINUS_CONSTANT_COLOR:
				return GL4.GL_ONE_MINUS_CONSTANT_COLOR;
			case ONE_MINUS_DST_ALPHA:
				return GL4.GL_ONE_MINUS_DST_ALPHA;
			case ONE_MINUS_DST_COLOR:
				return GL4.GL_ONE_MINUS_DST_COLOR;
			case ONE_MINUS_SRC1_COLOR:
				return GL4.GL_ONE_MINUS_SRC1_COLOR;
			case ONE_MINUS_SRC1_ALPHA:
				return GL4.GL_ONE_MINUS_SRC1_ALPHA;
			case ONE_MINUS_SRC_ALPHA:
				return GL4.GL_ONE_MINUS_SRC_ALPHA;
			case ONE_MINUS_SRC_COLOR:
				return GL4.GL_ONE_MINUS_SRC_COLOR;
			case SRC1_ALPHA:
				return GL4.GL_SRC1_ALPHA;
			case SRC1_COLOR:
				return GL4.GL_SRC1_COLOR;
			case SRC_ALPHA:
				return GL4.GL_SRC_ALPHA;
			case SRC_ALPHA_SATURATE:
				return GL4.GL_SRC_ALPHA_SATURATE;
			case SRC_COLOR:
				return GL4.GL_SRC_COLOR;
			case ZERO:
				return GL4.GL_ZERO;
		}
		return GL4.GL_INVALID_ENUM;
	}

	@Override
	public int getFaceCulling(UFaceCulling faceCulling) {
		switch (faceCulling) {
			case BACK:
				return GL4.GL_BACK;
			case FRONT:
				return GL4.GL_FRONT;
			case FRONT_AND_BACK:
				return GL4.GL_FRONT_AND_BACK;
		}
		return GL4.GL_INVALID_ENUM;
	}

	@Override
	public int getTestFunc(UTestFunction func) {
		switch (func) {
			case ALWAYS:
				return GL4.GL_ALWAYS;
			case EQUAL:
				return GL4.GL_EQUAL;
			case GEQUAL:
				return GL4.GL_GEQUAL;
			case GREATER:
				return GL4.GL_GREATER;
			case LEQUAL:
				return GL4.GL_LEQUAL;
			case LESS:
				return GL4.GL_LESS;
			case NEVER:
				return GL4.GL_NEVER;
			case NOTEQUAL:
				return GL4.GL_NOTEQUAL;
		}
		return GL4.GL_INVALID_ENUM;
	}

	@Override
	public int getTextureSwizzleChannel(UTextureSwizzleChannel channel) {
		switch (channel) {
			case R:
				return GL4.GL_RED;
			case G:
				return GL4.GL_GREEN;
			case B:
				return GL4.GL_BLUE;
			case A:
				return GL4.GL_ALPHA;
			case ONE:
				return GL4.GL_ONE;
			case ZERO:
				return GL4.GL_ZERO;
		}
		return GL4.GL_INVALID_ENUM;
	}
}
