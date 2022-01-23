package urender.demo.editor;

public interface IEditHandle<E> {
	public E getContent();
	
	public void save();
	
	public default void onRemoved() {
		
	}
	
	public static void saveAll(Iterable<? extends IEditHandle> it) {
		for (IEditHandle h : it) {
			h.save();
		}
	}
}
