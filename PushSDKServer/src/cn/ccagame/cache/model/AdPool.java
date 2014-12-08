package cn.ccagame.cache.model;


public class AdPool {
	public static final String KEY_SDK_VERSION = "SDK_VERSION";
	
	private AdInfo adInfo;
	private int arup;
	private int limitNum;
	

	public int getArup() {
		return arup;
	}

	public void setArup(int arup) {
		this.arup = arup;
	}

	public int getLimitNum() {
		return limitNum;
	}

	public void setLimitNum(int limitNum) {
		this.limitNum = limitNum;
	}

	public AdInfo getAdInfo() {
		return adInfo;
	}

	public void setAdInfo(AdInfo adInfo) {
		this.adInfo = adInfo;
	}
}
