package cn.ccagame.cache.model;

import java.io.Serializable;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class AdPushRecord implements Serializable{

	private static final long serialVersionUID = 6176626751074210144L;
	/**
	 * 
	 */
	public final static String KEY_AD_ID = "AD_ID";
	public final static String KEY_PUSH_TIME = "PUSH_TIME";
	public final static String KEY_STATUS = "STATUS";
	
	private int adId;
	private long pushTime;
	private int status;
	public int getAdId() {
		return adId;
	}
	public void setAdId(int adId) {
		this.adId = adId;
	}
	public long getPushTime() {
		return pushTime;
	}
	public void setPushTime(long pushTime) {
		this.pushTime = pushTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public String toJsonString(){
		JSONObject json = new JSONObject();
		json.put(KEY_AD_ID, adId);
		json.put(KEY_PUSH_TIME, pushTime);
		json.put(KEY_STATUS, status);
		return json.toJSONString();
	}
	
	public static AdPushRecord toAdPushRecord(String jsonStr){
		JSONObject json = (JSONObject) JSONValue.parse(jsonStr);
		AdPushRecord adPushRecord=null;
		if(json!=null){
			adPushRecord = new AdPushRecord();
			adPushRecord.setAdId(((Long)json.get(KEY_AD_ID)).intValue());
			adPushRecord.setPushTime((Long)json.get(KEY_PUSH_TIME));
			adPushRecord.setStatus(((Long)json.get(KEY_STATUS)).intValue());
		}
		return adPushRecord;
	}
}
