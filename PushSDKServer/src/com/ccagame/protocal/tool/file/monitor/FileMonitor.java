package com.ccagame.protocal.tool.file.monitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Monitor class for file changes.
 * 
 * @author Martin Liu
 */
public class FileMonitor {
	private static Logger logger = LoggerFactory.getLogger(FileMonitor.class);

	private static final FileMonitor instance = new FileMonitor();

	private Timer timer;

	private Map<String, FileMonitorTask> timerEntries;

	private FileMonitor() {
		this.timerEntries = new HashMap<String, FileMonitorTask>();
		this.timer = new Timer(true);
	}

	public static FileMonitor getInstance() {
		return instance;
	}

	/**
	 * Add a file to the monitor
	 * 
	 * @param listener The file listener
	 * @param filename The filename to watch
	 * @param period The watch interval.
	 */
	public void addFileChangeListener(FileChangeListener listener, String filename, long period) {
		this.removeFileChangeListener(filename);

		logger.debug("Watching " + filename);

		FileMonitorTask task = new FileMonitorTask(listener, filename);

		this.timerEntries.put(filename, task);
		this.timer.schedule(task, period, period);
	}

	/**
	 * Stop watching a file
	 * 
	 * @param listener The file listener
	 * @param filename The filename to keep watch
	 */
	public void removeFileChangeListener(String filename) {
		FileMonitorTask task = this.timerEntries.remove(filename);

		if (task != null) {
			task.cancel();
		}
	}
}
