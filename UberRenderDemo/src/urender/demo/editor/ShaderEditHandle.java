package urender.demo.editor;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import urender.engine.shader.UShader;

public class ShaderEditHandle implements IEditHandle<UShader> {
	public final UShader shader;
	public final Document document;
	
	public ShaderEditHandle(UShader shader) {
		this.shader = shader;
		this.document = new PlainDocument();
		try {
			document.insertString(0, shader.getShaderData(), null);
		} catch (BadLocationException ex) {
			Logger.getLogger(ShaderEditHandle.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	@Override
	public String toString() {
		return shader.getName();
	}

	@Override
	public void save() {
		try {
			shader.setShaderData(document.getText(0, document.getLength()));
		} catch (BadLocationException ex) {
			Logger.getLogger(ShaderEditHandle.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public UShader getContent() {
		return shader;
	}
}
