package urender.demo.editor;

import urender.engine.UGfxObject;

class DummyEditHandle implements IEditHandle<UGfxObject> {

	private UGfxObject obj;

	public DummyEditHandle(UGfxObject obj) {
		this.obj = obj;
	}

	@Override
	public UGfxObject getContent() {
		return obj;
	}

	@Override
	public void save() {
	}

	@Override
	public String toString() {
		return obj.getName();
	}

}
