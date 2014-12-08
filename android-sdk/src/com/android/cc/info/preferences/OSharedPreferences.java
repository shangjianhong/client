package com.android.cc.info.preferences;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.android.cc.info.data.AdInfo;
import com.android.cc.info.util.Constants;
import com.android.cc.info.util.DebugLog;

public class OSharedPreferences {
	
	private static final String TAG = "OSharedPreferences";
	
	private static SharedPreferences getSharedPreferences(Context context){
		return context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, 0);
	}
	
	private static Editor getEdit(Context context){
		return getSharedPreferences(context).edit();
	}
	
	
	private static final String REQUEST_FAIL_INFO = "REQUEST_FAIL_INFO";
	public static void setRequestFailInfo(Context context,String requestInfo)
	{
		if(TextUtils.isEmpty(requestInfo))
			return;
		
		String contentInfo = getRequestFailInfo(context);
		
		if(TextUtils.isEmpty(contentInfo)){
			getEdit(context).putString(REQUEST_FAIL_INFO, requestInfo).commit();
		}else{
			contentInfo += "$$" + requestInfo;
			getEdit(context).putString(REQUEST_FAIL_INFO, contentInfo).commit();
		}
	}
	
	public static String getRequestFailInfo(Context context)
	{
		String contentInfo = getSharedPreferences(context).getString(REQUEST_FAIL_INFO, "");
		if(!TextUtils.isEmpty(contentInfo))
		{
			getEdit(context).putString(REQUEST_FAIL_INFO, "").commit();
		}
		DebugLog.i("RequestFailInfo", contentInfo);
		return contentInfo;
	}
	
	public static void setNeedMonitoringForPackageName(Context context,AdInfo adInfo)
	{
		getEdit(context).putString(adInfo.apkPackageName, adInfo.adId).commit();
	}
	
	public static void setNeedMonitoringForPackageName(Context context,String apkPackageName)
	{
		getEdit(context).putString(apkPackageName, "").commit();
	}
	
	public static String getNeedMonitoringForPackageName(Context context,String name)
	{
		return getSharedPreferences(context).getString(name, "");
	}
	
	public static boolean getDownloadSuccessFlagForPackageName(Context context,String name){
		return getSharedPreferences(context).getBoolean(name + "@SUCCESS", false);
	}
	
	public static void setDownloadSuccessFlagForPackageName(Context context,String name,boolean flag){
		getEdit(context).putBoolean(name + "@SUCCESS", flag).commit();
	}
	
	public static void setResPathByDownloadUrl(Context context,String downloadUrl,String filePath){
		getEdit(context).putString(downloadUrl, filePath).commit();
	}
	
	public static String getResPathByDownLoadUrl(Context context,String downloadUrl){
		return getSharedPreferences(context).getString(downloadUrl, "");
	}
	
	private static final String RTC_TYPE = "RTC_TYPE";
	public static void setRtcOldTime(Context context,Long time){
		getEdit(context).putLong(RTC_TYPE, time).commit();
	}
	
	public static Long getRtcOldTime(Context context){
		return getSharedPreferences(context).getLong(RTC_TYPE, 0);
	}
	
	private static final String LAST_PUSH_TIME = "LAST_PUSH_TIME";
	public static void setLastPushTime(Context context,Long time){
		getEdit(context).putLong(LAST_PUSH_TIME, time).commit();
	}
	
	public static Long getLastPushTime(Context context){
		return getSharedPreferences(context).getLong(LAST_PUSH_TIME, 0);
	}
	
	private static final String PUSH_OFFSET_TIME = "PUSH_OFFSET_TIME";
	public static void setPushOffsetTime(Context context,Long time){
		getEdit(context).putLong(PUSH_OFFSET_TIME, time).commit();
	}
	
	public static Long getPushOffsetTime(Context context){
		return getSharedPreferences(context).getLong(PUSH_OFFSET_TIME, Constants.DEFAULT_OPEN_PUSH_TIME);
	}
	
	private static final String LAST_CONN_UPDATE_TIME = "LAST_CONN_UPDATE_TIME";
	public static void setLastConnUpdateTime(Context context,Long time){
		getEdit(context).putLong(LAST_CONN_UPDATE_TIME, time).commit();
	}
	
	public static Long getLastConnUpdateTime(Context context){
		return getSharedPreferences(context).getLong(LAST_CONN_UPDATE_TIME, 0);
	}
	
	private static final String ALAR_CODE = "ALAR_CODE";
	public static int getAlarmCode(Context context){
		Long code = getSharedPreferences(context).getLong(ALAR_CODE, 0);
		if(code == 0){
			Long tmp = System.currentTimeMillis();
			getEdit(context).putLong(ALAR_CODE, tmp).commit();
		}
		return code.intValue();
	}
	
	private static final String DOWNLOAD_INFOS = "DOWNLOAD_INFOS";
	private static final String DOWNLOAD_ARRAY_KEY = "str_arry";
	//保存单条纪录
	public static void setDownloadInfo(Context context,String content){
		try {
			if(TextUtils.isEmpty(content)){
				DebugLog.d(TAG, "setDownloadInfo: save option error,the content isn't have data.");
				return;
			}
			ArrayList<String> infos = getDownloadInfos(context);
			//下载队列数据为空
			if(infos == null || infos.size() == 0){
				if(infos == null) infos = new ArrayList<String>();
				infos.add(content);
			}else{
				if(infos.contains(content)){
					DebugLog.d(TAG, "setDownloadInfo: save option error,the content is exist.");
					return;
				}else{
					infos.add(content);
				}
			}
			final String contents = getStringForArrayString(infos);
			setDownloadInfos(context,contents);
		} catch (Exception e) {
			DebugLog.e(TAG, "setDownloadInfo:" + e.getMessage());
		}
		
	}
	public static void setDownloadInfos(Context context,String contents){
		DebugLog.d(TAG, "setDownloadInfos: the contents is - \n" + contents);
		getEdit(context).putString(DOWNLOAD_INFOS, contents).commit();
	}
	//下载队列
	public static ArrayList<String> getDownloadInfos(Context context){
		try {
			final String contentString = getSharedPreferences(context).getString(DOWNLOAD_INFOS, "");
			if(TextUtils.isEmpty(contentString)){
				return null;
			}
			
			JSONObject jsonObject = new JSONObject(contentString);
			JSONArray jsonArray = jsonObject.optJSONArray(DOWNLOAD_ARRAY_KEY);
			if(jsonArray == null || jsonArray.length() == 0){
				return null;
			}
			
			ArrayList<String> infos = new ArrayList<String>();
			for (int i = 0; i < jsonArray.length(); i++) {
				final String info = jsonArray.optString(i);
				if(!TextUtils.isEmpty(info)){
					infos.add(info);
				}
			}
			if(infos.size() == 0){
				return null;
			}
			return infos;
		} catch (Exception e) {
			DebugLog.e(TAG, "getDownloadInfos:" + e.getMessage());
			return null;
		}
	}
	//清除队列数据
	public static void cleanAllDownloadInfos(Context context){
		setDownloadInfos(context,"");
	}
	// 清除单条纪录
	public static void cleanDownloadInfoByContent(Context context,String content){
		try {
			ArrayList<String> infos = getDownloadInfos(context);
			DebugLog.d(TAG, "clean option,infos size:" + infos.size());
			DebugLog.d(TAG, "clean option,clean tigger content:" + content);
			if(infos != null && infos.size() > 0){
				boolean optionFlag = false;
				for (int i = 0; i < infos.size(); i++) {
					if(infos.get(i).equals(content)){
						infos.remove(content);
						optionFlag = true;
					}
				}
				if(optionFlag){
					String contents = getStringForArrayString(infos);
					DebugLog.d(TAG, "clean result size:" + infos.size());
					setDownloadInfos(context,contents);
				}else{
					DebugLog.d(TAG, "don't have clean tigger");
				}
			}else{
				DebugLog.d(TAG, "the download infos not have data");
			}
		} catch (Exception e) {
			DebugLog.e(TAG, "cleanDownloadInfoByContent:" + e.getMessage());
		}
	}
	//将列表数据转换为json数据
	private static String getStringForArrayString(ArrayList<String> infos){
		try {
			JSONObject jsonObject = new JSONObject();
			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < infos.size(); i++) {
				final String info = infos.get(i);
				if(!TextUtils.isEmpty(info)){
					jsonArray.put(info);
				}
			}
			jsonObject.put(DOWNLOAD_ARRAY_KEY, jsonArray);
			return jsonObject.toString();
		} catch (Exception e) {
			DebugLog.e(TAG, "getStringForArrayString:" + e.getMessage());
		}
		return "";
	}
	
	public static void setAdTitleNameForAdId(Context context,String adid,String title){
		getEdit(context).putString(adid, title).commit();
	}
	
	public static String getAdTitleNameByeAdId(Context context,String adid){
		return getSharedPreferences(context).getString(adid, "");
	}
}
