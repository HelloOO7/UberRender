package urender.demo;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class DemoUIUtility {

	public static void showInfoMessage(Component parent, String title, String msg) {
		JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.INFORMATION_MESSAGE);
	}

	public static void showErrorMessage(Component parent, String title, String msg) {
		JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.ERROR_MESSAGE);
	}

	public static String callNameInput(Component parent, String title, String msg) {
		return JOptionPane.showInputDialog(parent, msg, title, JOptionPane.PLAIN_MESSAGE);
	}

	public static File callGFXFileSelect(Component parent, boolean save) {
		return callFileSelect(parent, save, "UberRender Graphics Resource | *.gfx", ".gfx");
	}

	public static File callFileSelect(Component parent) {
		return callFileSelect(parent, false, null, (String[]) null);
	}

	public static File callFileSelect(Component parent, boolean save, String filterText, String... filterExt) {
		List<File> rsl = callFileSelect(parent, save, false, filterText, filterExt);
		return rsl.isEmpty() ? null : rsl.get(0);
	}

	public static List<File> callFileSelect(Component parent, boolean save, boolean multiselect, String filterText, String... filterExt) {
		JFileChooser jfc = new JFileChooser();
		jfc.setCurrentDirectory(new File("."));
		jfc.setMultiSelectionEnabled(multiselect);
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
		int dlgResult = -1;
		if (save) {
			dlgResult = jfc.showSaveDialog(parent);
		} else {
			dlgResult = jfc.showOpenDialog(parent);
		}

		List<File> result = new ArrayList<>();

		if (dlgResult == JFileChooser.APPROVE_OPTION) {
			if (!multiselect) {
				File f = jfc.getSelectedFile();
				if (f != null && save && filterExt != null) {
					String name = f.getName();
					boolean hasExt = false;
					for (String ext : filterExt) {
						if (name.endsWith(ext)) {
							hasExt = true;
							break;
						}
					}
					if (!hasExt) {
						if (filterExt.length > 0) {
							f = new File(f.getPath() + filterExt[0]);
						}
					}
				}
				result.add(f);
			} else {
				for (File f : jfc.getSelectedFiles()) {
					result.add(f);
				}
			}
		}

		return result;
	}
}
