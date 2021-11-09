package urender.api.backend;

import com.jogamp.opengl.GL4;
import urender.api.UBufferType;
import urender.api.UBufferUsageHint;
import urender.api.UDataType;
import urender.api.UPrimitiveType;
import urender.api.UShaderType;
import urender.api.UTextureFaceAssignment;
import urender.api.UTextureFormat;
import urender.api.UTextureType;

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
			case FLOAT32:
				return GL4.GL_FLOAT;
			case R8:
			case RG8:
			case RGB8:
			case RGBA8:
				return GL4.GL_UNSIGNED_BYTE;
		}
		return GL4.GL_INVALID_ENUM;
	}

	@Override
	public int getTextureFormatInternalFormat(UTextureFormat format) {
		switch (format) {
			case FLOAT32:
				return GL4.GL_R32F;
			case R8:
				return GL4.GL_R8;
			case RG8:
				return GL4.GL_RG8;
			case RGB8:
				return GL4.GL_RGB8;
			case RGBA8:
				return GL4.GL_RGBA8;
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
				return GL4.GL_RGBA;
		}
		return GL4.GL_INVALID_ENUM;
	}

	@Override
	public int getTextureType(UTextureType t) {
		return t == UTextureType.TEX2D ? GL4.GL_TEXTURE_2D : GL4.GL_TEXTURE_3D;
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
		switch (usage)  {
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
}
