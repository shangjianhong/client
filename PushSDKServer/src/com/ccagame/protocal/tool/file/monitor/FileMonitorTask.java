package com.ccagame.protocal.tool.file.monitor;

import java.io.File;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * file monitor task class
 * @author Martin Liu
 * @version $Id: FileMonitorTask.java,v 1.2 2009/12/04 06:48:26 martinliu Exp $ 
 */
public class FileMonitorTask extends TimerTask {
	private FileChangeListener listener;

	private static Logger logger = LoggerFactory.getLogger(FileMonitorTask.class.getName());

	private String filename;

	private File monitoredFile;

	private long lastModified;

	public FileMonitorTask(FileChangeListener listener, String filename) {
		this.listener = listener;
		this.filename = filename;

		this.monitoredFile = new File(filename);
		if (!monitoredFile.exists()) {
			return;
		}

		lastModified = monitoredFile.lastModified();
	}

	public void run() {
		try {
			long latestChange = this.monitoredFile.lastModified();
			if (lastModified != latestChange) {
				lastModified = latestChange;
				logger.debug("reload the file" + filename);
				if (listener != null) {
					listener.fileChanged(filename);
				}
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}
}