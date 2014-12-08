package cn.ccagame.task;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ccagame.cache.Cache;
import cn.ccagame.server.StartServer;

public class NFDFlightDataTimerTask extends TimerTask {

	private static Logger logger = LoggerFactory.getLogger(NFDFlightDataTimerTask.class);

	 @Override
	 public void run() {
		  try {
			  Cache.getInstance().clearPushCache();
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
	 }
}
