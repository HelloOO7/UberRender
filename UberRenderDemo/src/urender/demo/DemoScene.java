package urender.demo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3f;
import urender.engine.shader.UUniform;
import urender.engine.shader.UUniformFloat;
import urender.g3dio.ugfx.UGfxResource;
import urender.scenegraph.UCameraLookAtOrbit;
import urender.scenegraph.UCameraViewpoint;
import urender.scenegraph.UDirectionalLight;
import urender.scenegraph.UPointLight;
import urender.scenegraph.UScene;
import urender.scenegraph.USceneNode;
import urender.scenegraph.USpotLight;
import urender.scenegraph.io.USceneNodeGfxResourceAdapter;
import urender.scenegraph.io.UScenegraphGfxResourceLoader;

public class DemoScene extends UScene {

	private static final boolean LOAD_RESOURCES_FROM_CLASSPATH = false;

	public final UCameraLookAtOrbit orbitCamera = new UCameraLookAtOrbit();
	public final UCameraViewpoint animationCamera = new UCameraViewpoint();

	private final UUniformFloat time = new UUniformFloat("time", 0f);

	USceneNode star;

	public DemoScene(File demoFilePath) {
		addGlobalUniform(time);

		camera = orbitCamera;
		animationCamera.zNear = 0.1f;
		animationCamera.zFar = 400f;

		//prepareDemoActor("Demo.gfx");
		//prepareDemoActor("TorusDeferred.gfx");
		//prepareDemoActor("Helicopter2.gfx").transform.getScale().set(3f);
		//prepareDemoActor("HeightmapTest.gfx");
		//star = prepareDemoActor("StarTest.gfx");
		prepareDemoActor(demoFilePath.getAbsolutePath());

		createMoon();
		createPointLights();

		System.out.println("Light count: " + lights.size());
	}

	private void createMoon() {
		UDirectionalLight moon = new UDirectionalLight();
		moon.setName("lt_dir_Moon");
		moon.direction = new Vector3f(0f, -1f, 1f);
		moon.colors.ambient.set(.6f, .6f, .6f);
		moon.colors.diffuse.set(1f, 1f, 1f);
		lights.add(moon);
	}

	private void createPointLights() {
		UPointLight point1 = new UPointLight();
		point1.setName("lt_point_StartGate");
		point1.position.set(2f, 10f, 0f);
		point1.constantAttn = 1f;
		point1.linearAttn = 0.7f;
		point1.quadraticAttn = 1.8f;
		point1.colors.ambient.set(1f);
		point1.colors.specular.set(1f);
		lights.add(point1);

		Vector3f[] PURPLE_ARROW_LIGHTS = new Vector3f[]{
			new Vector3f(-28.5f, 34.86f, 20.02f),
			new Vector3f(-28.92f, 36.86f, 18.72f),
			new Vector3f(-29.06f, 38.06f, 16.95f),
			new Vector3f(-28.95f, 38.62f, 14.71f),
			new Vector3f(-28.83f, 38.52f, 12.67f),
			new Vector3f(-28.43f, 37.76f, 10.59f),
			new Vector3f(-27.96f, 36.35f, 9.16f),
			new Vector3f(-27.39f, 34.45f, 8.39f)
		};

		int index = 1;
		for (Vector3f purple : PURPLE_ARROW_LIGHTS) {
			UPointLight purpleLight = new UPointLight();
			purpleLight.setName("lt_point_Purple" + index);
			purpleLight.position.set(purple);
			purpleLight.constantAttn = 1f;
			purpleLight.linearAttn = 0.35f;
			purpleLight.quadraticAttn = 0.44f;
			purpleLight.colors.ambient.set(0f);
			purpleLight.colors.diffuse.set(204 / 255f, 0, 204 / 255f);
			lights.add(purpleLight);
			index++;
		}

		Vector3f[] LAMP_SPOT_LIGHTS = new Vector3f[]{
			new Vector3f(-26.42f, 31.87f, 8.92f),
			new Vector3f(-27.45f, 35.56f, 9.37f),
			new Vector3f(-28.22f, 37.80f, 11.74f),
			new Vector3f(-28.59f, 37.93f, 16.06f),
			new Vector3f(-28.35f, 35.97f, 19.01f),
			new Vector3f(-27.62f, 32.21f, 19.99f),};
		Vector3f[] LAMP_SPOT_LIGHT_DIRECTIONS = new Vector3f[]{
			new Vector3f(-0.87f, -0.29f, 0.38f),
			new Vector3f(-0.86f, -0.47f, 0.16f),
			new Vector3f(-0.80f, -0.58f, 0.098f),
			new Vector3f(-0.76f, -0.64f, -0.145f),
			new Vector3f(-0.80f, -0.45f, -0.39f),
			new Vector3f(-0.78f, -0.25f, -0.578f),};

		for (int i = 0; i < LAMP_SPOT_LIGHTS.length; i++) {
			USpotLight spotLight = new USpotLight();
			spotLight.setName("lt_spot_lamp" + (i + 1));
			spotLight.position.set(LAMP_SPOT_LIGHTS[i]);
			spotLight.colors.ambient.set(0f);
			spotLight.colors.diffuse.set(118 / 255f, 164 / 255f, 217 / 255f);
			spotLight.spotDirection.set(LAMP_SPOT_LIGHT_DIRECTIONS[i]);
			spotLight.constantAttn = 1.0f;
			spotLight.linearAttn = 0.35f;
			spotLight.quadraticAttn = 0.3f;
			spotLight.cutoffAngleDeg = 75f;
			lights.add(spotLight);
		}
	}

	@Override
	public List<UUniform> getSceneUniforms() {
		return new ArrayList<>();
	}

	public void setTimeCounter(float time) {
		this.time.set(time);
		if (star != null) {
			star.transform.getRotation().y = (time % 10000f) / 10000f * 6.28f;
		}
	}

	private static void loadResource(USceneNode dest, String fileName) {
		if (LOAD_RESOURCES_FROM_CLASSPATH) {
			UGfxResource.loadResourceClasspath("urender/demo/model/" + fileName, UScenegraphGfxResourceLoader.getInstance(), new USceneNodeGfxResourceAdapter(dest));
		} else {
			UGfxResource.loadResourceFile(new File(fileName), UScenegraphGfxResourceLoader.getInstance(), new USceneNodeGfxResourceAdapter(dest));
		}
	}

	private USceneNode prepareDemoActor(String fileName) {
		USceneNode node = new USceneNode();
		try {
			loadResource(node, fileName);
		} catch (Exception ex) {
			return node;
		}
		addChild(node);
		return node;
	}

}
