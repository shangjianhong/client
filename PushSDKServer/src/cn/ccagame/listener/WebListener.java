package cn.ccagame.listener;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ccagame.cache.Cache;
import cn.ccagame.task.TimerManager;
import cn.ccagame.util.SystemConfig;

import com.ccagame.protocal.server.Server;

public class WebListener implements ServletContextListener {
	private static Logger logger = LoggerFactory.getLogger(WebListener.class);

	public void contextDestroyed(ServletContextEvent arg0)
	{
		logger.info("stop WebListener");
		//lbs.saveUserLocationTmp();
	}

	public void contextInitialized(ServletContextEvent arg0)
	{	
		logger.info("start WebListener "+SystemConfig.getProperty("photo_size"));
		
		Cache.getInstance().loadCache();
		
		new TimerManager();
				
		Server.main(null);

	}

}
