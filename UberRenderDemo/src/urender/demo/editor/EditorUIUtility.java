package urender.demo.editor;

import java.awt.Component;
import java.io.File;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class EditorUIUtility {

	public static void showInfoMessage(Component parent, String title, String msg) {
		JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.INFORMATION_MESSAGE);
	}

	public static String callNameInput(Component parent, String title, String msg) {
		return JOptionPane.showInputDialog(parent, msg, title, JOptionPane.PLAIN_MESSAGE);
	}

	public static File callFileSelect(Component parent) {
		return callFileSelect(parent, null, (String[]) null);
	}

	public static File callFileSelect(Component parent, String filterText, String... filterExt) {
		JFileChooser jfc = new JFileChooser();
		jfc.setCurrentDirectory(new File("."));
		if (filterText == null || filterExt == null) {
			jfc.setAcceptAllFileFilterUsed(true);
		} else {
			jfc.setFileFilter(new FileFilter() {
				@Override
				public boolean accept(File f) {
					if (f.isDirectory()) {
						return true;
					}
					String fname = f.getName();
					for (String ext : filterExt) {
						if (fname.endsWith(ext)) {
							return true;
						}
					}
					return false;
				}

				@Override
				public String getDescription() {
					return filterText;
				}

			});
			jfc.setAcceptAllFileFilterUsed(false);
		}

		Action details = jfc.getActionMap().get("viewTypeDetails");
		if (details != null) {
			details.actionPerformed(null);
		}
		jfc.showOpenDialog(parent);

		return jfc.getSelectedFile();
	}
}
