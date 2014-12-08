package cn.ccagame.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ccagame.cache.Cache;
import cn.ccagame.task.TimerManager;
import cn.ccagame.util.SystemConfig;

import com.ccagame.protocal.server.Server;

public class StartServer {
	private static Logger logger = LoggerFactory.getLogger(StartServer.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		logger.debug("start WebListener "+SystemConfig.getProperty("photo_size"));
		
		Cache.getInstance().loadCache();
		
		new TimerManager();
		
		Server.main(null); 
	}
}
