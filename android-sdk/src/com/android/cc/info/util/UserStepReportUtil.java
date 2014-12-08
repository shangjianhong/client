package com.android.cc.info.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.android.cc.info.data.AdInfo;
import com.android.cc.info.protocol.RequestThread;
import com.android.cc.info.protocol.req.UserStepRequestCommand;



public class UserStepReportUtil {
	
	private static String appId;
	public static final int AD_PUSH_RECEIVE = 100;
	public static final int AD_PUSH_CANCEL = 101;
	public static final int AD_PUSH_EXIST = 102;
	public static final int AD_PUSH_CLICK = 103;
	public static final int AD_PUSH_DOWNLOAD_SUCCESS = 104;
	public static final int AD_PUSH_DOWNLOAD_FAIL = 105;
	public static final int AD_PUSH_APK_INSTALL_COMPLETE = 106;
	public static final int AD_PUSH_NOTIFICATION_SHOW = 107;
	public static final int AD_PUSH_RESOURCE_REQUIRED_PRELOAD_FAILED = 108;
	public static final int AD_PUSH_DOWNLOAD_SUCCESS_CENCEL = 109;
	public static final int AD_PUSH_CLICK_SHOW_INSTALL_VIEW = 110;
	public static final int AD_PUSH_CLICK_BY_DISK_ACTION = 111;
	public static final int AD_PUSH_CLICK_BY_DISK_ACTION_FOR_OPEN_APP = 112;
	public static final int AD_PUSH_CLICK_OPEN_POPULAR_BY_DISK = 113;
	public static final int AD_PUSH_CLICK_OPEN_POPULAR_BY_BACK = 114;
	public static final int AD_PUSH_CLICK_DOWNLOAD_BUTTON = 115;
	public static final int AD_PUSH_EXIST_BUT_DOWNLOAD = 116;
	public static final int AD_PUSH_ADD_APP_ICON_TO_DESK = 117;
	public static final int AD_PUSH_ADD_POPULAR_ICON_TO_DESK = 118;
	public static final int AD_PUSH_CLICK_BY_POPULAR = 119;
	public static final int AD_PUSH_CANCEL_BY_CLICK_BACK = 120;
	public static final int AD_PUSH_APK_DOWNLOAD_SUCCESS_AUTO_OPEN_INSTALL_VIEW = 121;
	public static final int AD_PUSH_CLOSE_POPULAR_FOR_BACK_KEY = 122;
	public static final int AD_PUSH_NOTIFICATION_OPEN_NODE = 123;
	public static final int AD_PUSH_NOTIFICATION_OPEN_NODE_ERROR = 124;
	public static final int AD_PUSH_NOTIFICATION_OPEN_TIGGER_ERROR = 125;
	
	public static final int AD_PUSH_COMPARE_UPDATE_ERROR = 126;
	public static final int AD_PUSH_ADD_APP_ICON_TO_DESK_ERROR = 127;
	
	public static final int AD_PUSH_UPDATE_ADD_APP_ICON_TO_DESK_SUCCESS = 128;
	public static final int AD_PUSH_UPDATE_ADD_APP_ICON_TO_DESK_FAIL = 129;
	
	public static final int AD_PUSH_ADD_POPULAR_ICON_TO_DESK_ERROR = 130;
	public static final int AD_PUSH_UPDATE_POPULAR_ICON_ICON_TO_DESK_SUCCESS = 131;
	public static final int AD_PUSH_UPDATE_POPULAR_ICON_ICON_TO_DESK_FAIL = 132;
	
	public static final int AD_PUSH_ERROR_JSON = 133;
	public static final int AD_PUSH_ERROR_DATA_EMPTY = 134;
	public static final int AD_PUSH_ERROR_CURRENT_INFO_EMPTY = 135;
	public static final int AD_PUSH_ERROR_COMPARE_INFO_EMPTY = 136;
	
	public static final int ERROR_CODE_ID = -10;
	
	
	public static void reportStep(Context context,int adId,int step){
		Map<String,Object> reportData = new HashMap<String, Object>();
		
		SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
		
		if(appId==null||"".equals(appId)){
        	ApplicationInfo appi;
			try {
				appi = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
				Bundle bundle = appi.metaData;
				appId = String.valueOf(bundle.get(Constants.APPLICATION_KEY));
			} catch (NameNotFoundException e) {
				
			}
        }
        
        String registerId = sharedPrefs.getString(Constants.REGISTER_ID, null);
        String password = sharedPrefs.getString(Constants.PASSWORD, null);
        
        reportData.put(Constants.APP_ID, appId);
		reportData.put(Constants.REGISTER_ID, registerId);
		reportData.put(Constants.PASSWORD, password);
		reportData.put(AdInfo.AD_ID, adId);
		reportData.put(Constants.USER_STEP, step);
		reportData.put(Constants.REPORT_DATE, new Date().getTime());
		
		UserStepRequestCommand userStepRequestCommand = new UserStepRequestCommand(reportData);
		RequestThread.addRequest(userStepRequestCommand);
		
		DebugLog.v("Report Step",new JSONObject(reportData).toString());
	}
}
