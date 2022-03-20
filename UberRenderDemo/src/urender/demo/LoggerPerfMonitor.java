package urender.demo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import urender.demo.perf.IPerfMonitor;

public class LoggerPerfMonitor implements IPerfMonitor {

	private BufferedWriter writer;

	public LoggerPerfMonitor(File logFile) {
		try {
			writer = new BufferedWriter(new FileWriter(logFile));
		} catch (IOException ex) {
			Logger.getLogger(LoggerPerfMonitor.class.getName()).log(Level.SEVERE, null, ex);
		}
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				writer.close();
			} catch (IOException ex) {
			}
		}));
	}

	private long lastTimestamp = 0;
	private long[] frametimes = new long[60];
	private int frametimesIdx = 0;
	private boolean avgStartReached = false;

	@Override
	public void newFrame() {
		long timestamp = System.currentTimeMillis();

		int fps;
		if (lastTimestamp == 0) {
			fps = 0;
		} else {
			long diff = timestamp - lastTimestamp;
			frametimesIdx++;
			if (frametimesIdx == frametimes.length) {
				frametimesIdx = 0;
				avgStartReached = true;
			}
			frametimes[frametimesIdx] = diff;

			if (avgStartReached) {
				long sum = 0;
				for (int i = 0; i < frametimes.length; i++) {
					sum += frametimes[i];
				}

				fps = (int) (1000 / (sum / frametimes.length));
				
				try {
					writer.write(timestamp + "; " + fps);
					writer.newLine();
				} catch (IOException ex) {
					Logger.getLogger(LoggerPerfMonitor.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
		lastTimestamp = timestamp;
	}

	@Override
	public void setDisplayLoopTime(long time) {
	}
}
