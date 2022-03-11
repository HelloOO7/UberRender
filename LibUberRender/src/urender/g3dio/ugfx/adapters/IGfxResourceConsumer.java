package urender.g3dio.ugfx.adapters;

/**
 * Interface for receiving resource data loaded from a UGfx stream.
 */
public interface IGfxResourceConsumer {
	/**
	 * Handle a deserialized object.
	 * @param obj 
	 */
	public void loadObject(Object obj);
}
