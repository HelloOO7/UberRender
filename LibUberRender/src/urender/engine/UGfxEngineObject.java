package urender.engine;

/**
 * Base class for URender engine objects.
 */
public abstract class UGfxEngineObject extends UGfxObject {

	/**
	 * Gets the non-abstract type of the object.
	 *
	 * @return A UGfxEngineObjectType constant whose value guarantees safe type casting.
	 */
	public abstract UGfxEngineObjectType getType();
}
