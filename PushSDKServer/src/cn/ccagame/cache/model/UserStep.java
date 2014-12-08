package cn.ccagame.cache.model;



public class UserStep {
	public static final String KEY_USER_STEP = "USER_STEP";
	public static final String KEY_REPORT_DATE = "REPORT_DATE";
	public static final String KEY_CREATE_DATE = "CREATE_DATE";
	
	public static final String KEY_BATCH_USER_STEP = "BATCH_USER_STEP";
	
	public static final int AD_PUSH_EXIST = 102;
	public static final int AD_PUSH_NOTIFICATION_SHOW = 107;
	public static final int AD_PUSH_EXIST_BUT_DOWNLOAD = 116;
	public static final int AD_PUSH = 99;
	
	public static final String BATCH_DELIMITER = "\\$\\$";
	
	public int id;
	public String appId;
	public String registerId;
	public int adId;
	public int step;
	public long reportDate;
	public long createDate;
	
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public long getReportDate() {
		return reportDate;
	}
	public void setReportDate(long reportDate) {
		this.reportDate = reportDate;
	}
	public long getCreateDate() {
		return createDate;
	}
	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRegisterId() {
		return registerId;
	}
	public void setRegisterId(String registerId) {
		this.registerId = registerId;
	}
	public int getAdId() {
		return adId;
	}
	public void setAdId(int adId) {
		this.adId = adId;
	}
	public int getStep() {
		return step;
	}
	public void setStep(int step) {
		this.step = step;
	}
	
}
