package cn.ccagame.task;

import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ccagame.cache.Cache;
import cn.ccagame.server.StartServer;

public class ReloadCacheTimerTask extends TimerTask {

	private static Logger logger = LoggerFactory.getLogger(ReloadCacheTimerTask.class);

	 @Override
	 public void run() {
		  try {
			  Cache.getInstance().reloadCache();
		  } catch (Exception e) {
			  logger.info("reloadCache()异常"+e.toString());
			  e.printStackTrace();
		  }
	 }
}
