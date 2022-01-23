package urender.g3dio.ugfx;

import urender.common.io.base.iface.ReadableStream;
import urender.common.io.base.impl.ext.data.DataInStream;

public class UGfxDataInput extends DataInStream {

	private int version;

	public UGfxDataInput(ReadableStream in) {
		super(in);
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public boolean versionOver(int version) {
		return this.version >= version;
	}
}
