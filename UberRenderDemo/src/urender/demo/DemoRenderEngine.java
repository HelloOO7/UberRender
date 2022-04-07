package urender.demo;

import java.io.File;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import urender.api.UClearMode;
import urender.api.UFaceCulling;
import urender.api.UFramebufferAttachment;
import urender.api.UTestFunction;
import urender.api.UTextureFormat;
import urender.api.backend.RenderingBackend;
import urender.engine.UFramebuffer;
import urender.engine.URenderTarget;
import urender.engine.UShadingMethod;
import urender.engine.shader.UUniformList;
import urender.engine.shader.UUniformMatrix3;
import urender.engine.shader.UUniformMatrix4;
import urender.engine.shader.UUniformVector3;
import urender.g3dio.ugfx.UGfxResource;
import urender.scenegraph.UDrawSources;
import urender.scenegraph.UDrawState;
import urender.scenegraph.UGfxRenderer;
import urender.scenegraph.ULightAdapter;
import urender.scenegraph.URenderQueue;
import urender.scenegraph.URenderQueueMeshState;
import urender.scenegraph.UScene;
import urender.scenegraph.USceneNode;
import urender.scenegraph.io.USceneNodeGfxResourceAdapter;
import urender.scenegraph.io.UScenegraphGfxResourceLoader;

public class DemoRenderEngine extends UGfxRenderer {

	private final boolean LOAD_RESOURCES_FROM_CLASSPATH = false;

	private final USceneNode GBUFFER_COMPOSE_MODEL = new USceneNode();
	private final USceneNode FILL_SCREEN_MODEL = new USceneNode();

	//The g-buffer pass renders to these targets
	private final URenderTarget rtPosition = new URenderTarget(0, "PositionTexture", UFramebufferAttachment.COLOR, UTextureFormat.RGBA16F);
	private final URenderTarget rtNormal = new URenderTarget(1, "NormalTexture", UFramebufferAttachment.COLOR, UTextureFormat.RGBA16F);
	private final URenderTarget rtAlbedo = new URenderTarget(2, "AlbedoTexture", UFramebufferAttachment.COLOR, UTextureFormat.RGBA8);
	//We also have to store certain material properties in a G-buffer. For our shader, that will be specular and emission intensity.
	private final URenderTarget rtSpecular = new URenderTarget(3, "SpecularTexture", UFramebufferAttachment.COLOR, UTextureFormat.RGBA8); //allow modifying each color intensity separately
	private final URenderTarget rtEmission = new URenderTarget(4, "EmissionTexture", UFramebufferAttachment.COLOR, UTextureFormat.RGBA8);
	//Depth render target shared for both deferred and forward rendering
	private final URenderTarget rtSharedDepth = new URenderTarget(0, "DepthTexture", UFramebufferAttachment.DEPTH_STENCIL, UTextureFormat.DEPTH24_STENCIL8);

	private final UFramebuffer framebufferGbuffer = new UFramebuffer(rtPosition, rtSharedDepth, rtNormal, rtAlbedo, rtSpecular, rtEmission);

	//The forward-rendered pass renders directly to this target
	//The deferred-rendered pass should render to it prior with setting gl_FragDepth to the depth texture value
	private final URenderTarget rtForward = new URenderTarget(
			0,
			"ForwardSurface",
			UFramebufferAttachment.COLOR,
			UTextureFormat.RGBA8
	);
	private final UFramebuffer framebufferForward = new UFramebuffer(
			rtForward,
			rtSharedDepth
	); //shared depth buffer for both deferred and forward shading

	private TurboLightManager lightMgr = new TurboLightManager();

	private final Matrix4f modelMtx = new Matrix4f();
	private final Matrix4f viewMtx = new Matrix4f();
	private final Matrix4f projMtx = new Matrix4f();
	private final Matrix3f normMtx = new Matrix3f();

	private final UUniformMatrix4 worldMtxU = new UUniformMatrix4("UBR_ModelMatrix", modelMtx);
	private final UUniformMatrix4 viewMtxU = new UUniformMatrix4("UBR_ViewMatrix", viewMtx);
	private final UUniformMatrix4 projMtxU = new UUniformMatrix4("UBR_ProjectionMatrix", projMtx);
	private final UUniformMatrix3 normMtxU = new UUniformMatrix3("UBR_NormalMatrix", normMtx);

	private final Vector3f eye = new Vector3f();
	private final UUniformVector3 eyeU = new UUniformVector3("Eye", eye);

	private final UUniformList systemUniforms = new UUniformList();

	public DemoRenderEngine(RenderingBackend backend) {
		super(backend);
		prepareGfxResources();
		buildSystemUniforms();
	}

	private void loadResource(USceneNode dest, String fileName) {
		if (LOAD_RESOURCES_FROM_CLASSPATH) {
			UGfxResource.loadResourceClasspath("urender/demo/model/" + fileName, UScenegraphGfxResourceLoader.getInstance(), new USceneNodeGfxResourceAdapter(dest));
		} else {
			UGfxResource.loadResourceFile(new File(fileName), UScenegraphGfxResourceLoader.getInstance(), new USceneNodeGfxResourceAdapter(dest));
		}
	}

	private void buildSystemUniforms() {
		systemUniforms.add(worldMtxU);
		systemUniforms.add(viewMtxU);
		systemUniforms.add(projMtxU);
		systemUniforms.add(normMtxU);
		systemUniforms.add(eyeU);
	}

	private void prepareGfxResources() {
		try {
			loadResource(GBUFFER_COMPOSE_MODEL, "GBufferComposeQuad.gfx");
			loadResource(FILL_SCREEN_MODEL, "FillScreenQuad.gfx");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.err.println("Error while loading critical system resources!");
		}
	}

	private void setGlobalMatrices(URenderQueueMeshState state) {
		projMtx.set(state.nodeState.projectionMatrix);
		viewMtx.set(state.nodeState.viewMatrix);
		modelMtx.set(state.nodeState.modelMatrix);
		state.nodeState.modelMatrix.normalize3x3(normMtx);
		normMtx.normal();

		viewMtx.getTranslation(eye);
	}

	private void drawScenePass(URenderQueue queue, int priority) {
		for (URenderQueueMeshState state : queue.queue()) {
			if (priority == -1 || state.getDrawPriority() == priority) {
				setGlobalMatrices(state);

				state.draw(this);
			}
		}
	}

	@Override
	public void beginDraw() {
		changeShadingMethod(UShadingMethod.FORWARD);
		clearViewport(backend, false, false);
		initDrawState();
	}

	@Override
	public void drawScene(UScene scene) {
		beginScene(scene);

		URenderQueue queue = scene.calcRenderQueue();
		queue.sort();

		for (UDrawSources drawSources : queue.drawSources()) {
			drawSources.setup(this);
		}

		int deferredLayerMax = queue.getMaxRenderPriority(UShadingMethod.DEFERRED);
		for (int layer = 0; layer <= deferredLayerMax; layer++) {
			//Draw geometry to G-buffer layer
			changeShadingMethod(UShadingMethod.DEFERRED);
			disableAlphaBlend();
			clearViewport(backend, true, true); //clear G-buffer, but retain depth
			drawScenePass(queue, layer);	//draw G-buffer layer

			//Blit G-buffer layer
			changeShadingMethod(UShadingMethod.FORWARD);
			backend.renderStateDepthMaskSet(false);
			enableAlphaBlend();
			GBUFFER_COMPOSE_MODEL.drawHeadless(this); //blit deferred framebuffer to forward surface
			backend.renderStateDepthMaskSet(true);
		}

		changeShadingMethod(UShadingMethod.FORWARD);
		enableAlphaBlend();
		drawScenePass(queue, -1);
	}

	private static void clearViewport(RenderingBackend backend, boolean keepDepth, boolean transparentClearColor) {
		if (transparentClearColor) {
			backend.clearColorSet(0f, 0.0f, 0.0f, 0.0f);
		} else {
			backend.clearColorSet(0f, 0.5f, 0.9f, 1.0f);
		}
		backend.clearDepthSet(1.0f);

		backend.renderStateDepthMaskSet(true);
		backend.renderStateColorMaskSet(true, true, true, true);
		backend.renderStateDepthTestSet(true, UTestFunction.LEQUAL);
		backend.renderStateCullingSet(UFaceCulling.BACK);
		if (keepDepth) {
			backend.clear(UClearMode.COLOR);
		} else {
			backend.clear(UClearMode.COLOR, UClearMode.DEPTH);
		}
	}

	@Override
	public void blitScreen() {
		changeShadingMethod(null);
		clearViewport(backend, false, false);
		FILL_SCREEN_MODEL.drawHeadless(this);
	}

	@Override
	protected UFramebuffer getGBufferFB() {
		return framebufferGbuffer;
	}

	@Override
	protected UFramebuffer getForwardFB() {
		return framebufferForward;
	}

	@Override
	protected ULightAdapter getLightAdapter() {
		return lightMgr;
	}

	@Override
	protected UUniformList getSystemUniforms() {
		return systemUniforms;
	}
}
