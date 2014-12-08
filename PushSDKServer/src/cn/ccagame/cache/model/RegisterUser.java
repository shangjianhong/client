package cn.ccagame.cache.model;

import java.io.Serializable;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import cn.ccagame.cache.Cache;

public class RegisterUser implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6308924447022391945L;
	public static final String KEY_REGISTER_KEY = "REGISTER_KEY";
	public static final String KEY_REGISTER_ID = "REGISTER_ID";
	public static final String KEY_PASSWORD = "PASSWORD";
	public static final String KEY_PACKAGE_NAME = "PACKAGE_NAME";
	public static final String KEY_DEVICE_INFO = "DEVICE_INFO";
	public static final String KEY_CREATE_DATE = "CREATE_DATE";
	
	private String appId;
	private String registerKey;
	private String registerId;
	private String password;
	private String packageName;
	private String deviceInfo;
	private long createDate;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getRegisterKey(){
		return registerKey;
	}
	
	public void setRegisterKey(String registerKey){
		this.registerKey = registerKey;
	}
	
	public String getRegisterId(){
		return registerId;
	}
	
	public void setRegisterId(String registerId){
		this.registerId = registerId;
	}
	
	public String getPassword(){
		return password;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
	public String getPackageName(){
		return packageName;
	}
	
	public void setPackageName(String packageName){
		this.packageName = packageName;
	}
	
	public String getDeviceInfo(){
		return deviceInfo;
	}
	
	public void setDeviceInfo(String deviceInfo){
		this.deviceInfo = deviceInfo;
	}

	public long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}
	
	public boolean isPush(){
		int newUserPushTime = Cache.getInstance().getNewUserPushTime();
		if(newUserPushTime<=0){
			newUserPushTime = 8;
		}
		return System.currentTimeMillis()-createDate>newUserPushTime*60*1000;
	}
	
	public String toJsonString(){
		JSONObject json = new JSONObject();
		json.put(KEY_REGISTER_KEY, registerKey);
		json.put(KEY_REGISTER_ID, registerId);
		json.put(KEY_PASSWORD, password);
		json.put(AppList.KEY_APP_ID,appId);
		json.put(KEY_PACKAGE_NAME, packageName);
		json.put(KEY_DEVICE_INFO, deviceInfo);
		json.put(KEY_CREATE_DATE, createDate);
		return json.toJSONString();
	}
	
	public static RegisterUser toRegisterUser(String jsonStr){
		if(jsonStr==null){
			return null;
		}
		JSONObject json = (JSONObject) JSONValue.parse(jsonStr);
		RegisterUser registerUser=null;
		if(json!=null){
			registerUser = new RegisterUser();
			registerUser.setRegisterKey((String)json.get(KEY_REGISTER_KEY));
			registerUser.setRegisterId((String)json.get(KEY_REGISTER_ID));
			registerUser.setPassword((String)json.get(KEY_PASSWORD));
			registerUser.setAppId((String)json.get(AppList.KEY_APP_ID));
			registerUser.setPackageName((String)json.get(KEY_PACKAGE_NAME));
			registerUser.setDeviceInfo((String)json.get(KEY_DEVICE_INFO));
			registerUser.setCreateDate((Long)json.get(KEY_CREATE_DATE));
		}
		return registerUser;
	}
}
