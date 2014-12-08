package cn.ccagame.cache.model;

import java.util.Date;

public class AppList {
	public static final String KEY_APP_ID = "APP_ID";
	private static final int STATUS_REVIEW = 0;
	private static final int STATUS_NORMAL = 1;
	
	private String appId;
	private String channelName;
	private String appName;
	private String packageName;
	private long createDate;
	private long lastUpdateDate;
	private int status;
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public long getCreateDate() {
		return createDate;
	}
	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}
	public long getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(long lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public boolean verifyAppId(){
		if(status==STATUS_REVIEW){
			return false;
		}else{
			return true;
		}
	}
}
