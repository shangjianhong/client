package cn.ccagame.cache.model;

import java.util.Calendar;
import java.util.logging.Logger;

import cn.ccagame.cache.Cache;

public class AdConfigInfo {
	
	private int adCount;
	private int adInterval;
	private int adStart;
	private int adEnd;
	private int newUserPushTime;
	private int limitedCount;
	private long limitedRate;
	private int serverCount;//应用服务器数量
	
	public int getServerCount() {
		return serverCount;
	}
	public void setServerCount(int serverCount) {
		this.serverCount = serverCount;
	}
	public int getAdCount() {
		return adCount;
	}
	public void setAdCount(int adCount) {
		this.adCount = adCount;
	}
	public int getAdInterval() {
		return adInterval;
	}
	public void setAdInterval(int adInterval) {
		this.adInterval = adInterval;
	}
	public int getAdStart() {
		return adStart;
	}
	public void setAdStart(int adStart) {
		this.adStart = adStart;
	}
	public int getAdEnd() {
		return adEnd;
	}
	public void setAdEnd(int adEnd) {
		this.adEnd = adEnd;
	}
	
	public int getNewUserPushTime() {
		return newUserPushTime;
	}
	public void setNewUserPushTime(int newUserPushTime) {
		this.newUserPushTime = newUserPushTime;
	}
	
	public int getLimitedCount() {
		return limitedCount;
	}
	public void setLimitedCount(int limitedCount) {
		this.limitedCount = limitedCount;
	}
	public long getLimitedRate() {
		return limitedRate;
	}
	public void setLimitedRate(long limitedRate) {
		this.limitedRate = limitedRate;
	}
	public boolean isPush(){
		Calendar calendar = Calendar.getInstance(); 
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		if(hour>=adStart&&hour<=adEnd){
			return true;
		}else{
			return false;
		}
	}
	
	public static void main(String[] str){
		Calendar calendar = Calendar.getInstance(); 
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		System.out.println(hour);
	}
}
