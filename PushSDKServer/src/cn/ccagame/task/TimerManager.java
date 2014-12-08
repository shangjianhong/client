package cn.ccagame.task;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.ccagame.cache.Cache;

public class TimerManager {

	 private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;
	 
	 //private static final long RELOAD_CACHE_PERIOD = 1000*60*5;
	 private static final long RELOAD_CACHE_PERIOD = 1000*1*1;
	 
	 public TimerManager() {
		  Calendar calendar = Calendar.getInstance();  
	
		  calendar.set(Calendar.HOUR_OF_DAY, 0);
		  calendar.set(Calendar.MINUTE, 2);
		  calendar.set(Calendar.SECOND, 0);
		  
		  Date date=calendar.getTime(); 
		  
		  if (date.before(new Date())) {
		      date = this.addDay(date, 1);
		  }
		  
		  long delay = date.getTime()-new Date().getTime();
		  
		  //Timer dayTimer = new Timer();
		  ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
		  
		  NFDFlightDataTimerTask DayTask = new NFDFlightDataTimerTask();
		  //dayTimer.schedule(DayTask,date,PERIOD_DAY);
		  executorService.scheduleAtFixedRate(DayTask, delay, PERIOD_DAY, TimeUnit.MILLISECONDS);//毫秒
		  
		  //Timer realoadCacheTimer = new Timer();
		  ScheduledExecutorService realoadCacheExecutorService = Executors.newScheduledThreadPool(4);
		  ReloadCacheTimerTask realoadCacheTask = new ReloadCacheTimerTask();
		  //realoadCacheTimer.schedule(realoadCacheTask, RELOAD_CACHE_PERIOD, RELOAD_CACHE_PERIOD);
		  realoadCacheExecutorService.scheduleAtFixedRate(realoadCacheTask, RELOAD_CACHE_PERIOD, RELOAD_CACHE_PERIOD, TimeUnit.MILLISECONDS);
	 }
	 
	 
	 public static void main(String[] s){
		 Calendar calendar1 = Calendar.getInstance();  
			
		  calendar1.set(Calendar.HOUR_OF_DAY, 0);
		  calendar1.set(Calendar.MINUTE, 2);
		  calendar1.set(Calendar.SECOND, 0);
		  
		  Date date1=calendar1.getTime();
		  
		  Calendar calendar2 = Calendar.getInstance();  
			
		  calendar2.set(Calendar.HOUR_OF_DAY, 0);
		  calendar2.set(Calendar.MINUTE, 5);
		  calendar2.set(Calendar.SECOND, 0);
		  
		  Date date2=calendar2.getTime();
		  
		  if(date1.before(date2)){
			  System.out.println("进来了");
		  }
			  
	 }

	 public Date addDay(Date date, int num) {
		  Calendar startDT = Calendar.getInstance();
		  startDT.setTime(date);
		  startDT.add(Calendar.DAY_OF_MONTH, num);
		  return startDT.getTime();
	 }
}
