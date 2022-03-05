package urender.engine;

/**
 * Render layer/priority description of a material.
 */
public class UMaterialDrawLayer implements Comparable<UMaterialDrawLayer> {

	/**
	 * The primary shading layer.
	 */
	public final UShadingMethod method;
	/**
	 * Priority within the shading layer.
	 */
	public final int priority;

	public UMaterialDrawLayer(UShadingMethod method, int priority) {
		this.method = method;
		this.priority = priority;
	}

	/**
	 * Compares two draw layers by priority.
	 *
	 * @param o The draw layer to compare to.
	 * @return
	 */
	@Override
	public int compareTo(UMaterialDrawLayer o) {
		if (method == o.method) {
			return priority - o.priority;
		}
		return (method.ordinal() << 28) - (o.method.ordinal() << 28); //the actual ordinal is irrelevant - we simply do this to conserve draw method switching
	}
}
