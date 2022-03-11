package urender.common;

public interface IBuilder<O> {
	public O build();
	public void reset();
}
