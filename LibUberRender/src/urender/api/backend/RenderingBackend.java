package urender.api.backend;

import java.nio.Buffer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import urender.api.UBlendEquation;
import urender.api.UBlendFunction;
import urender.api.UBufferType;
import urender.api.UBufferUsageHint;
import urender.api.UClearMode;
import urender.api.UDataType;
import urender.api.UFaceCulling;
import urender.api.UFramebufferAttachment;
import urender.api.UObjHandle;
import urender.api.UPrimitiveType;
import urender.api.UShaderType;
import urender.api.UTestFunction;
import urender.api.UTextureSwizzleChannel;
import urender.api.UTextureFaceAssignment;
import urender.api.UTextureFormat;
import urender.api.UTextureMagFilter;
import urender.api.UTextureMinFilter;
import urender.api.UTextureType;
import urender.api.UTextureWrap;

/**
 * URender GPU interface core.
 *
 * All rendering backend implement this interface regardless of their own specifics and quirks.
 */
public interface RenderingBackend {

	/**
	 * Gets an arbitrary unique ID used for identifying within object handles.No two concurrent backends are
	 * allowed to have the same identity.
	 *
	 * @return A non-null identity object.
	 */
	public Object getIdent();

	/**
	 * Creates a screen-space viewport.
	 *
	 * @param x Horizontal origin coordinate.
	 * @param y Vertical origin coordinate.
	 * @param w Width of the viewport from the origin.
	 * @param h Height of the viewport from the origin.
	 */
	public void viewport(int x, int y, int w, int h);

	/**
	 * Sets the default clear depth that is written when clear(DEPTH) is called.
	 *
	 * @param clearDepth A depth value ranging from 0.0 to 1.0.
	 */
	public void clearDepthSet(float clearDepth);

	/**
	 * Sets the default clear color that is written when clear(COLOR) is called.
	 *
	 * @param r Red channel of the color (0.0 - 1.0).
	 * @param g Green channel of the color (0.0 - 1.0).
	 * @param b Blue channel of the color (0.0 - 1.0).
	 * @param a Alpha channel of the color (0.0 - 1.0).
	 */
	public void clearColorSet(float r, float g, float b, float a);

	/**
	 * Clears the specified components of the current framebuffer.
	 *
	 * @param modes An array of UClearMode flags that represents the components to be cleared.
	 */
	public void clear(UClearMode... modes);

	/**
	 * Sets the color for the CONSTANT_COLOR and CONSTANT_ALPHA blend factors.
	 *
	 * @param r Red channel of the color (0.0 - 1.0).
	 * @param g Green channel of the color (0.0 - 1.0).
	 * @param b Blue channel of the color (0.0 - 1.0).
	 * @param a Alpha channel of the color (0.0 - 1.0).
	 */
	public void renderStateBlendColorSet(float r, float g, float b, float a);

	/**
	 * Configures the blend render state for both the RGB and Alpha channels together.
	 *
	 * @param enabled Whether blending is enabled.
	 * @param eq The equation to process both RGB and Alpha blend factors.
	 * @param funcSrc The source blend factor for both RGB and Alpha.
	 * @param funcDst The destination blend factor for both RGB and Alpha.
	 */
	public void renderStateBlendSet(boolean enabled, UBlendEquation eq, UBlendFunction funcSrc, UBlendFunction funcDst);

	/**
	 * Configures the blend render state for RGB and Alpha channels separately.
	 *
	 * @param enabled Whether blending is enabled.
	 * @param eqRgb The equation to apply on the RGB blend factors.
	 * @param eqAlpha The equation to apply on the Alpha blend factors.
	 * @param funcSrcRgb The source blend factor for the RGB channels.
	 * @param funcDstRgb The destination blend factor for the RGB channels.
	 * @param funcSrcAlpha The source blend factor for the Alpha channel.
	 * @param funcDstAlpha The destination blend factor for the Alpha channel.
	 */
	public void renderStateBlendSet(boolean enabled, UBlendEquation eqRgb, UBlendEquation eqAlpha, UBlendFunction funcSrcRgb, UBlendFunction funcDstRgb, UBlendFunction funcSrcAlpha, UBlendFunction funcDstAlpha);

	/**
	 * Configures the depth buffer write mask.
	 *
	 * @param enabled True if subsequent draw calls should update the depth buffer.
	 */
	public void renderStateDepthMaskSet(boolean enabled);

	/**
	 * Configures the RGBA buffer write mask.
	 *
	 * @param r True if subsequent draw calls should update the Red component of the color framebuffer.
	 * @param g True if subsequent draw calls should update the Green component of the color framebuffer.
	 * @param b True if subsequent draw calls should update the Blue component of the color framebuffer.
	 * @param a True if subsequent draw calls should update the Alpha component of the color framebuffer.
	 */
	public void renderStateColorMaskSet(boolean r, boolean g, boolean b, boolean a);

	/**
	 * Configures primitive face culling.
	 *
	 * @param faceCulling
	 */
	public void renderStateCullingSet(UFaceCulling faceCulling);

	/**
	 * Configures early depth test.
	 *
	 * @param enabled True to enable early depth test.
	 * @param func Test function to use.
	 */
	public void renderStateDepthTestSet(boolean enabled, UTestFunction func);

	/**
	 * Creates a new texture object.
	 *
	 * @param tex Handle for the object.
	 * @param type Type of the new texture.
	 */
	public void texInit(UObjHandle tex, UTextureType type);

	/**
	 * Uploads texture data to VRAM.
	 *
	 * @param tex Texture handle.
	 * @param width Width of the image.
	 * @param height Height of the image.
	 * @param format Pixel format of the raw buffer.
	 * @param faceAsgn Face assignment for a cubemap, or null for a single-face texture.
	 * @param data Raw image data.
	 */
	public void texUploadData2D(UObjHandle tex, int width, int height, UTextureFormat format, UTextureFaceAssignment faceAsgn, Buffer data);

	/**
	 * Set texture wrap and filter params.
	 *
	 * @param texture Texture handle.
	 * @param type Type of the underlying texture.
	 * @param wrapU Horizontal coordinate wrapping.
	 * @param wrapV Vertical coordinate wrapping.
	 * @param magFilter Upscaling filter mode.
	 * @param minFilter Downscaling filter mode.
	 */
	public void texSetParams(UObjHandle texture, UTextureType type, UTextureWrap wrapU, UTextureWrap wrapV, UTextureMagFilter magFilter, UTextureMinFilter minFilter);

	/**
	 * Sets the RGBA channel swizzle mask for a texture.
	 *
	 * @param texture Texture handle.
	 * @param type Type of the underlying texture.
	 * @param r Swizzled Red channel.
	 * @param g Swizzled Green channel.
	 * @param b Swizzled Blue channel.
	 * @param a Swizzled Alpha channel.
	 */
	public void texSwizzleMask(UObjHandle texture, UTextureType type, UTextureSwizzleChannel r, UTextureSwizzleChannel g, UTextureSwizzleChannel b, UTextureSwizzleChannel a);

	/**
	 * Creates a new texture unit object.
	 *
	 * @param texUnit Handle for the object.
	 * @param unitIndex Index of the texture unit.
	 */
	public void texUnitInit(UObjHandle texUnit, int unitIndex);

	/**
	 * Binds a texture to a texture unit.
	 *
	 * @param texUnit Texture unit handle.
	 * @param type Type of the underlying texture.
	 * @param texture Handle of texture to be bound.
	 */
	public void texUnitSetTexture(UObjHandle texUnit, UTextureType type, UObjHandle texture);

	/**
	 * Creates a new renderbuffer object.
	 *
	 * @param rb Handle for the object.
	 */
	public void renderBufferInit(UObjHandle rb);

	/**
	 * Initializes renderbuffer storage data.
	 *
	 * @param rb Renderbuffer handle.
	 * @param format Format of the renderbuffer.
	 * @param width Width of the renderbuffer.
	 * @param height Height of the renderbuffer.
	 */
	public void renderBufferStorage(UObjHandle rb, UTextureFormat format, int width, int height);

	/**
	 * Creates a new framebuffer object.
	 *
	 * @param fb Handle for the object.
	 */
	public void framebufferInit(UObjHandle fb);

	/**
	 * Sets a framebuffer as current.
	 *
	 * @param fb The framebuffer to bind, or null to bind the default screen framebuffer.
	 */
	public void framebufferBind(UObjHandle fb);

	/**
	 * Sets the default screen framebuffer as the current framebuffer. This should be functionally equivalent
	 * to framebufferBind(null).
	 */
	public void framebufferResetScreen();

	/**
	 * Creates a new draw buffer object.
	 *
	 * @param drawBuffer Handle for the object.
	 * @param attachment Role of the draw buffer when used in a framebuffer.
	 * @param attachmentIndex Index of the draw buffer in a multi-member attachment.
	 */
	public void drawBufferInit(UObjHandle drawBuffer, UFramebufferAttachment attachment, int attachmentIndex);

	/**
	 * Attaches a texture as a framebuffer draw buffer.
	 *
	 * @param fb The framebuffer to attach to.
	 * @param fbTexture The texture to attach.
	 * @param drawBuffer The draw buffer to attach as.
	 */
	public void drawBufferTextureSet(UObjHandle fb, UObjHandle fbTexture, UObjHandle drawBuffer);

	/**
	 * Attaches a renderbuffer as a framebuffer draw buffer.
	 *
	 * @param fb The framebuffer to attach to.
	 * @param fbRenderbuffer The renderbuffer to attach.
	 * @param drawBuffer The draw buffer to attach as.
	 */
	public void drawBufferRenderbufferSet(UObjHandle fb, UObjHandle fbRenderbuffer, UObjHandle drawBuffer);

	/**
	 * Defines the draw buffers of a framebuffer.
	 *
	 * @param fb The framebuffer to target.
	 * @param drawBuffers Array of all draw buffer object handles.
	 */
	public void drawBuffersDefine(UObjHandle fb, UObjHandle... drawBuffers);

	/**
	 * Creates a new VRAM data buffer object.
	 *
	 * @param buffer Handle for the object.
	 */
	public void bufferInit(UObjHandle buffer);

	/**
	 * Uploads data to a VRAM data buffer.
	 *
	 * @param target Type of the buffer.
	 * @param buffer VRAM data buffer handle.
	 * @param usage Driver usage hint.
	 * @param data Raw buffer data.
	 * @param size Size of the raw buffer data in bytes.
	 */
	public void bufferUploadData(UBufferType target, UObjHandle buffer, UBufferUsageHint usage, Buffer data, int size);

	/**
	 * Binds a shader attribute to a location within a vertex buffer.
	 *
	 * @param vbo Handle of the vertex buffer.
	 * @param index Index of the attribute within the shader.
	 * @param elementCount Number of elements in one attribute.
	 * @param type Data type of the attribute elements.
	 * @param normalized Whether fixed-point data should be used as normalized or as its actual values.
	 * @param stride Number of bytes between two consecutive elements, including the size of this element, or
	 * 0 for tightly packed elements.
	 * @param offset
	 */
	public void bufferAttribPointer(UObjHandle vbo, UObjHandle index, int elementCount, UDataType type, boolean normalized, int stride, long offset);

	/**
	 * Disables access to a vertex attribute.
	 *
	 * @param vbo Handle of the vertex buffer.
	 * @param index Index of the VAO attribute.
	 */
	public void bufferAttribDisable(UObjHandle vbo, UObjHandle index);

	/**
	 * Draws an array of primitives from a plain vertex buffer.
	 *
	 * @param vbo Handle of the vertex buffer.
	 * @param primitiveType Primitive draw mode.
	 * @param count Number of vertices to draw.
	 */
	public void buffersDrawInline(UObjHandle vbo, UPrimitiveType primitiveType, int count);

	/**
	 * Draws an array of primitives from an indexed vertex buffer.
	 *
	 * @param vbo Handle of the vertex buffer.
	 * @param primitiveType Primitive draw mode.
	 * @param ibo Handle of the index buffer.
	 * @param iboFormat Data type of the index buffer.
	 * @param count Number of indices to draw.
	 */
	public void buffersDrawIndexed(UObjHandle vbo, UPrimitiveType primitiveType, UObjHandle ibo, UDataType iboFormat, int count);

	/**
	 * Creates a new shader object.
	 *
	 * @param shader Handle for the object.
	 * @param type Type of the shader.
	 */
	public void shaderInit(UObjHandle shader, UShaderType type);

	/**
	 * Compiles a shader from source code. The source format is backend-defined.
	 *
	 * @param shader Handle of the shader object.
	 * @param source Plain-text shader source.
	 */
	public void shaderCompileSource(UObjHandle shader, String source);

	/**
	 * Creates a new shader program.
	 *
	 * @param program Handle for the program.
	 */
	public void programInit(UObjHandle program);

	/**
	 * Attaches a vertex/fragment shader to a program.
	 *
	 * @param program Handle of the program to attach to.
	 * @param shader Handle of the shader to be attached.
	 */
	public void programAttachShader(UObjHandle program, UObjHandle shader);

	/**
	 * Links a program with attached shaders.
	 *
	 * @param program Handle of the program.
	 */
	public void programLink(UObjHandle program);

	/**
	 * Sets the active shader program.
	 *
	 * @param program Handle of the program.
	 */
	public void programUse(UObjHandle program);

	/**
	 * Creates a handle to a shader program uniform.
	 * 
	 * @param program Handle of the program containing the uniform.
	 * @param uniform Handle for the uniform.
	 * @param name Name of the uniform within the program.
	 */
	public void uniformLocationInit(UObjHandle program, UObjHandle uniform, String name);

	public void uniformInt(UObjHandle location, int value);

	public void uniformIntv(UObjHandle location, int... value);

	public void uniformFloat(UObjHandle location, float value);

	public void uniformFloatv(UObjHandle location, float... value);

	public void uniformVec2(UObjHandle location, Vector2f vector);

	public void uniformVec2v(UObjHandle location, Vector2f... vectors);

	public void uniformVec3(UObjHandle location, Vector3f vector);

	public void uniformVec3v(UObjHandle location, Vector3f... vectors);

	public void uniformVec4(UObjHandle location, Vector4f vector);

	public void uniformVec4v(UObjHandle location, Vector4f... vectors);

	public void uniformMat3(UObjHandle location, Matrix3f matrix);

	public void uniformMat3v(UObjHandle location, Matrix3f... matrices);

	public void uniformMat4(UObjHandle location, Matrix4f matrix);

	public void uniformMat4v(UObjHandle location, Matrix4f... matrices);

	public void uniformSampler(UObjHandle location, UObjHandle texUnit);

	/**
	 * Creates a handle to a shader program attribute.
	 * 
	 * @param program Handle of the program containing the attribute.
	 * @param attribute Handle for the attribute.
	 * @param name Name of the attribute within the program.
	 */
	public void attributeLocationInit(UObjHandle program, UObjHandle attribute, String name);

	/**
	 * Flushes all queued GPU commands remaining in the driver.
	 */
	public void flush();
}
