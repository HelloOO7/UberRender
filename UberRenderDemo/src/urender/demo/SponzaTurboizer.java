package urender.demo;

import java.io.File;

public class SponzaTurboizer {
	public static void main(String[] args) {
		for (File file : new File("_internal_testdata\\Sponza\\textures_png").listFiles()) {
			if (file.getName().contains("_diff")) {
				file.renameTo(new File(file.getPath().replace("_diff", "_Alb")));
			}
			else if (file.getName().contains("_ddn")) {
				file.renameTo(new File(file.getPath().replace("_ddn", "_Nrm")));
			}
		}
	}
}
