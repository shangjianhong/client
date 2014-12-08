package cn.ccagame.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ccagame.process.UserStepProcess;



public class LogDataWriter
{
   private static Logger logger = LoggerFactory.getLogger(LogDataWriter.class);
   private final String LOG_DATA_DELIMITER = "|$$|";
   public final int LOG_FILE_SIZE = 200*1024*1024;
   public final String LOG_FILE_PREFIX = "UserStep";
   public final String LOG_FILE_SUFFIX = ".log";
   public String currentLogFileName;
   public FileWriter fileWrite;
   
   public LogDataWriter(){
	   if(isCreateNewFileWrite()){
		   createNewFileWrite();
	   }
   }
   
   private void createNewFileWrite(){
	   String logDirPath = SystemConfig.getProperty("log_path");
	   String logDataPath = logDirPath+"/"+currentLogFileName;;
	  
	   try {
		   if(fileWrite!=null){
			   fileWrite.close();
		   }
		   fileWrite = new FileWriter(logDataPath, true);
		} catch (IOException e) {
			e.printStackTrace();
		}  
   }
   
   public synchronized void writeUserStepData(String appId,String registerId,int adId,int step,long reportDate){
	   String logData = appId+LOG_DATA_DELIMITER+registerId+LOG_DATA_DELIMITER+adId+LOG_DATA_DELIMITER
			   +step+LOG_DATA_DELIMITER+reportDate+LOG_DATA_DELIMITER+System.currentTimeMillis()+"\r\n";
	   try {
		   if(fileWrite==null||isCreateNewFileWrite()){
			   createNewFileWrite();
		   }
		   fileWrite.append(logData);
		   fileWrite.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
   }
   
   private boolean isCreateNewFileWrite(){
	   if(currentLogFileName==null||"".equals(currentLogFileName)){
		   currentLogFileName = getLogFileName(0);
		   return true;
	   }else{
		   String logFileName = getLogFileName(0);
		   if(logFileName.equals(currentLogFileName)){
			   return false;
		   }else{
			   currentLogFileName = logFileName;
			   return true;
		   }
	   }
   }
   
/*   private String getLogFileName(int index){
	   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	   String logDirPath = SystemConfig.getProperty("log_path");
	   String fileName = LOG_FILE_PREFIX+"."+dateFormat.format(new Date())+LOG_FILE_SUFFIX+"."+index;
	   
	   String logFilePath = logDirPath+"/"+fileName;
	   File logFile = new File(logFilePath);
	   if(!logFile.exists()){
		   try {
			   logFile.createNewFile();
		   } catch (IOException e) {
			   e.printStackTrace();
			   logger.error("create log file exception");
		   }
		   return fileName;
	   }else{
		   if(logFile.length()>LOG_FILE_SIZE){
			   return getLogFileName(++index);
		   }else{
			   return fileName;
		   }
	   }
   }*/
   
   private String getLogFileName(int index){
	   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHH");
	   String logDirPath = SystemConfig.getProperty("log_path");
	   String fileName = LOG_FILE_PREFIX+"."+dateFormat.format(new Date())+LOG_FILE_SUFFIX;
	   
	   String logFilePath = logDirPath+"/"+fileName;
	   File logFile = new File(logFilePath);
	   if(!logFile.exists()){
		   try {
			   logFile.createNewFile();
		   } catch (IOException e) {
			   e.printStackTrace();
			   logger.error("create log file exception");
		   }
	   }
	   return fileName;
   }
}
