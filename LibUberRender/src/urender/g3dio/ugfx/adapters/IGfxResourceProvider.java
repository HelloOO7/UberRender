package urender.g3dio.ugfx.adapters;

public interface IGfxResourceProvider {

	/**
	 * Get the next serializable object.
	 *
	 * @return Next object ready for serialization, or null if no further objects should be serialized.
	 */
	public Object nextObject();
}
