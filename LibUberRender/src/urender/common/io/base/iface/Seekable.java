package urender.common.io.base.iface;

import java.io.IOException;

public interface Seekable extends Positioned {
    public void seek(int position) throws IOException;
	public void setLength(int length) throws IOException;
}
