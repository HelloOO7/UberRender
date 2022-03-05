package urender.demo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.joml.Vector3f;
import urender.engine.shader.UUniform;
import urender.engine.shader.UUniformFloat;
import urender.engine.shader.UUniformInt;
import urender.engine.shader.UUniformVector3;
import urender.scenegraph.UDirectionalLight;
import urender.scenegraph.ULight;
import urender.scenegraph.ULightAdapter;
import urender.scenegraph.ULightColors;
import urender.scenegraph.UPointLight;
import urender.scenegraph.USpotLight;

public class TurboLightManager implements ULightAdapter {

	private static final int TURBO_LIGHT_TYPE_DIR = 0;
	private static final int TURBO_LIGHT_TYPE_POINT = 1;
	private static final int TURBO_LIGHT_TYPE_SPOT = 2;
	private static final int TURBO_LIGHT_TYPE_MAX = 3;

	private static final int TURBO_LIGHT_MAX_DIR = 1;
	private static final int TURBO_LIGHT_MAX_POINT = 32;
	private static final int TURBO_LIGHT_MAX_SPOT = 32;

	private static final int[] LIGHT_MAX = new int[]{TURBO_LIGHT_MAX_DIR, TURBO_LIGHT_MAX_POINT, TURBO_LIGHT_MAX_SPOT};

	private final UUniformInt lightCounts = new UUniformInt("LightCounts", new int[3]);

	private final TurboDirLight[] DIR_LIGHTS = new TurboDirLight[TURBO_LIGHT_MAX_DIR];
	private final TurboPointLight[] POINT_LIGHTS = new TurboPointLight[TURBO_LIGHT_MAX_POINT];
	private final TurboSpotLight[] SPOT_LIGHTS = new TurboSpotLight[TURBO_LIGHT_MAX_SPOT];

	public TurboLightManager() {
		for (int i = 0; i < DIR_LIGHTS.length; i++) {
			DIR_LIGHTS[i] = new TurboDirLight(i);
		}
		for (int i = 0; i < POINT_LIGHTS.length; i++) {
			POINT_LIGHTS[i] = new TurboPointLight(i);
		}
		for (int i = 0; i < SPOT_LIGHTS.length; i++) {
			SPOT_LIGHTS[i] = new TurboSpotLight(i);
		}
	}

	private int getLightCount(int lightType) {
		return lightCounts.get(lightType);
	}

	private void setLightCount(int lightType, int count) {
		lightCounts.set(lightType, count);
	}

	@Override
	public void setLights(Collection<? extends ULight> lights) {
		for (int i = 0; i < TURBO_LIGHT_TYPE_MAX; i++) {
			setLightCount(i, 0);
		}
		for (ULight l : lights) {
			int turboLightType = -1;

			switch (l.getLightType()) {
				case DIRECTIONAL:
					turboLightType = TURBO_LIGHT_TYPE_DIR;
					break;
				case POINT:
					turboLightType = TURBO_LIGHT_TYPE_POINT;
					break;
				case SPOT:
					turboLightType = TURBO_LIGHT_TYPE_SPOT;
					break;
			}

			int nowCount = getLightCount(turboLightType);
			int maxCount = LIGHT_MAX[turboLightType];

			if (nowCount < maxCount) {
				switch (l.getLightType()) {
					case DIRECTIONAL:
						DIR_LIGHTS[nowCount].assign((UDirectionalLight) l);
						break;
					case POINT:
						POINT_LIGHTS[nowCount].assign((UPointLight) l);
						break;
					case SPOT:
						SPOT_LIGHTS[nowCount].assign((USpotLight) l);
						break;
				}

				setLightCount(turboLightType, nowCount + 1);
			}
		}
	}

	@Override
	public Collection<UUniform> getLightUniforms() {
		List<UUniform> list = new ArrayList<>();
		list.add(lightCounts);

		for (int i = 0; i < getLightCount(TURBO_LIGHT_TYPE_DIR); i++) {
			DIR_LIGHTS[i].add(list);
		}
		for (int i = 0; i < getLightCount(TURBO_LIGHT_TYPE_POINT); i++) {
			POINT_LIGHTS[i].add(list);
		}
		for (int i = 0; i < getLightCount(TURBO_LIGHT_TYPE_SPOT); i++) {
			SPOT_LIGHTS[i].add(list);
		}

		return list;
	}

	public static class TurboDirLight {

		public UUniformVector3 direction;
		public TurboLightColors colors;

		public TurboDirLight(int index) {
			String baseName = "DirLights[" + index + "]";

			direction = new UUniformVector3(baseName + ".Direction");
			colors = new TurboLightColors(baseName);
		}

		public void add(List<UUniform> list) {
			list.add(direction);
			colors.add(list);
		}

		public void assign(UDirectionalLight dirLight) {
			direction.set(dirLight.direction);
			colors.assign(dirLight.colors);
		}
	}

	public static class TurboPointLight {

		public UUniformVector3 position;
		public TurboLightColors colors;
		public UUniformVector3 attenuation;

		public TurboPointLight(int index) {
			String baseName = "PointLights[" + index + "]";

			position = new UUniformVector3(baseName + ".Position");
			colors = new TurboLightColors(baseName);
			attenuation = new UUniformVector3(baseName + ".Attn", new Vector3f());
		}

		public void add(List<UUniform> list) {
			list.add(position);
			list.add(attenuation);
			colors.add(list);
		}

		public void assign(UPointLight pointLight) {
			position.set(pointLight.position);
			colors.assign(pointLight.colors);
			attenuation.get().set(pointLight.constantAttn, pointLight.linearAttn, pointLight.quadraticAttn);
		}
	}

	public static class TurboSpotLight {

		public UUniformVector3 position;
		public UUniformVector3 spotDirection;
		public UUniformFloat cutoff;
		public TurboLightColors colors;
		public UUniformVector3 attenuation;

		public TurboSpotLight(int index) {
			String baseName = "SpotLights[" + index + "]";

			position = new UUniformVector3(baseName + ".Position");
			colors = new TurboLightColors(baseName);
			attenuation = new UUniformVector3(baseName + ".Attn", new Vector3f());
			spotDirection = new UUniformVector3(baseName + ".SpotDirection");
			cutoff = new UUniformFloat(baseName + ".Cutoff", 0f);
		}

		public void add(List<UUniform> list) {
			list.add(position);
			list.add(spotDirection);
			list.add(cutoff);
			colors.add(list);
			list.add(attenuation);
		}

		public void assign(USpotLight spotLight) {
			position.set(spotLight.position);
			spotDirection.set(spotLight.spotDirection);
			cutoff.set((float) Math.cos(Math.toRadians(90f - spotLight.cutoffAngleDeg * 0.5f)));
			colors.assign(spotLight.colors);
			attenuation.get().set(spotLight.constantAttn, spotLight.linearAttn, spotLight.quadraticAttn);
		}
	}

	public static class TurboLightColors {

		public UUniformVector3 colors;

		public TurboLightColors(String lightBaseName) {
			colors = new UUniformVector3(lightBaseName + ".Colors", new Vector3f[3]);
		}

		public void add(List<UUniform> list) {
			list.add(colors);
		}

		public void assign(ULightColors colorSet) {
			colors.set(0, colorSet.diffuse);
			colors.set(1, colorSet.ambient);
			colors.set(2, colorSet.specular);
		}
	}
}
