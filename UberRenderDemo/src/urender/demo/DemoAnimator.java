package urender.demo;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import urender.scenegraph.UCameraViewpoint;

public class DemoAnimator {

	private static final KeyFrame[] KEYFRAMES = new KeyFrame[]{
		new KeyFrame(0, new Vector3f(.803f, 7.77f, 13.54f), new Vector3f(112, 0, -597)),
		new KeyFrame(4000, new Vector3f(-7.1f, -10.65f, 14.835f), new Vector3f(123, 0, -619)),
		new KeyFrame(4001, new Vector3f(3.2f, -10.19f, 10.85f), new Vector3f(81, 0, -376)),
		new KeyFrame(8000, new Vector3f(3.88f, -6.46f, 10.221f), new Vector3f(104, 0, -339)),
		new KeyFrame(8001, new Vector3f(-8.071f, -17.64f, 11.972f), new Vector3f(86, 0, -446)),
		new KeyFrame(12000, new Vector3f(-18.97f, -17.76f, 15.083f), new Vector3f(77, 0, -446)),
		new KeyFrame(12001, new Vector3f(66.087f, 14.028f, 43.381f), new Vector3f(74, 0, -253)),
		new KeyFrame(18000, new Vector3f(40.37f, -51.06f, 67.793f), new Vector3f(54, 0, -315)),
	};
	private static long END_TIME = 0L;

	static {
		for (KeyFrame kf : KEYFRAMES) {
			if (kf.time > END_TIME) {
				END_TIME = kf.time;
			}
		}
	}

	private final long begin = System.currentTimeMillis();

	public void apply(UCameraViewpoint cam) {
		long time = (System.currentTimeMillis() - begin);
		if (END_TIME != 0) {
			time %= END_TIME;
		}
		for (int i = 0; i < KEYFRAMES.length; i++) {
			if (KEYFRAMES[i].time > time) {
				KeyFrame right = KEYFRAMES[i];
				KeyFrame left;
				if (i > 0) {
					left = KEYFRAMES[i - 1];
				} else {
					left = KEYFRAMES[0];
				}

				long diff = (right.time - left.time);
				float weight = 0f;
				if (diff > 0) {
					weight = (time - left.time) / (float) diff;
				}

				left.camPosition.lerp(right.camPosition, weight, cam.position);
				
				Quaternionf quatL = new Quaternionf();
				quatL.rotationZYX(left.camRotation.z, left.camRotation.y, left.camRotation.x);
				Quaternionf quatR = new Quaternionf();
				quatR.rotationZYX(right.camRotation.z, right.camRotation.y, right.camRotation.x);
				quatL.slerp(quatR, weight);
				Matrix4f matrix = new Matrix4f();
				matrix.set(quatL);
				matrix.getEulerAnglesZYX(cam.rotation);

				break;
			}
		}
	}

	private static class KeyFrame {

		public final long time;
		public final Vector3f camPosition;
		public final Vector3f camRotation;

		public KeyFrame(long time, Vector3f pos, Vector3f rotDeg) {
			float temp;
			temp = pos.y;
			pos.y = pos.z;
			pos.z = -temp;
			this.time = time;
			this.camPosition = pos;
			this.camRotation = new Vector3f(rotDeg);
			float toRadians = 3.1415926f / 180f;
			Matrix4f mat = new Matrix4f();
			mat.rotateYXZ(rotDeg.z * toRadians, (rotDeg.x - 90f) * toRadians, rotDeg.y * toRadians);
			mat.getEulerAnglesZYX(camRotation);
		}
	}
}
