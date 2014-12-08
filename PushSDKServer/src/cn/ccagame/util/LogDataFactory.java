package cn.ccagame.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class LogDataFactory
{
   private static Logger logger = LoggerFactory.getLogger(LogDataFactory.class);

   public static LogDataWriter logDataWriter;
   
   public static LogDataWriter getLogDataWriter(){
	   if(logDataWriter==null){
		   logDataWriter = new LogDataWriter();
	   }
	   return logDataWriter;
   }
}
