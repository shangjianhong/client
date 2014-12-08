package com.android.cc.info.data;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.cc.info.util.DebugLog;

public class FetchData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7208805974996149919L;
	private static final String TAG = "FetchData";
	
	public static final int TYPE_TODAY_POPULAR = 1;
	public static final int TYPE_AD = 2;
	
	public static final String KEY_FETCH_DATA = "FETCH_DATA";
	public static final String KEY_TYPE = "TYPE";
	public static final String KEY_COMPARE_DATA = "COMPARE_DATA";
	
	public int type;
	public String fetchData;
	public String compareData;
	
	
	public static FetchData parseFetchDataJson(String fetchDataJson){
		FetchData fetchData = null;
		try {
			JSONObject jsonObj = new JSONObject(fetchDataJson);
			fetchData = new FetchData();
			fetchData.fetchData = jsonObj.optString(KEY_FETCH_DATA);
			fetchData.type = jsonObj.optInt(KEY_TYPE);
			fetchData.compareData = jsonObj.optString(KEY_COMPARE_DATA);
			DebugLog.d(TAG, "===========================================");
			DebugLog.d(TAG, "type : " + fetchData.type);
			DebugLog.d(TAG, "fetchData : " + fetchData.fetchData);
			DebugLog.d(TAG, "compareData : " + fetchData.compareData);
			DebugLog.d(TAG, "===========================================");
		} catch (JSONException e) {
			DebugLog.d(TAG, "parse adPush json error",e);
		}
		
		return fetchData;
	}
}
