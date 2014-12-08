package com.android.cc.info.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.cc.info.util.DebugLog;

public class AdPush implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7076715957579902565L;

	private static final String TAG = "AdPush";
	
	public static final String CURRENT_AD_INFO = "CURRENT_AD_INFO";
	
	public static final String TODAY_AD_POPULAR = "TODAY_AD_POPULAR";
	
	public String adContentJson;
	
	public AdInfo mCurrentAdInfo;
	
	public List<AdInfo> mTodayPopularList = new ArrayList<AdInfo>();
	
	public static AdPush parseAdPushJson(String adPushJson){
		AdPush adPush = null;
		try {
			JSONObject jsonObj = new JSONObject(adPushJson);
			adPush = new AdPush();
			
			adPush.adContentJson = adPushJson;
			
			String currentAdInfo = jsonObj.optString(CURRENT_AD_INFO);
			
			adPush.mCurrentAdInfo = AdInfo.parseAdInfoJson(currentAdInfo);
			
			JSONArray todayPopularJson = jsonObj.optJSONArray(TODAY_AD_POPULAR);
			
			if(todayPopularJson!=null&&todayPopularJson.length()>0){
				for(int i =0;i<todayPopularJson.length();i++){
					String adInfo = todayPopularJson.getString(i);
					adPush.mTodayPopularList.add(AdInfo.parseAdInfoJson(adInfo));
				}
			}
			
		} catch (Exception e) {
			DebugLog.d(TAG, "parse adPush json error",e);
		}
		
		return adPush;
	}
	
	public static void setCurrentjsonInfo(AdPush adPush,AdInfo currentAdInfo){
		adPush.mCurrentAdInfo = currentAdInfo;
		try{
			JSONObject jsonObject = new JSONObject(adPush.adContentJson);
			jsonObject.put(CURRENT_AD_INFO, currentAdInfo.adContentJson);
			adPush.adContentJson = jsonObject.toString();
		} catch (Exception e) {
			DebugLog.d(TAG, "parse adPush json error",e);
		}
	}
}
