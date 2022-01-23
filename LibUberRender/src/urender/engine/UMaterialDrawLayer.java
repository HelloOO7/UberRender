package urender.engine;

public class UMaterialDrawLayer implements Comparable<UMaterialDrawLayer> {
	public final ShadingMethod method;
	public final int priority;
	
	public UMaterialDrawLayer(ShadingMethod method, int priority) {
		this.method = method;
		this.priority = priority;
	}

	@Override
	public int compareTo(UMaterialDrawLayer o) {
		if (method == o.method) {
			return priority - o.priority;
		}
		return method.ordinal() - o.method.ordinal(); //the actual ordinal is irrelevant - we simply do this to conserve draw method switching
	}
	
	public static enum ShadingMethod {
		FORWARD,
		DEFERRED
	}
}
