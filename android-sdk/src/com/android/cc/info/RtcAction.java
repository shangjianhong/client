package com.android.cc.info;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.android.cc.info.util.DebugLog;
import com.android.cc.info.util.StringUtils;


public class RtcAction {
	
	private static final String TAG = "RtcAction";
	
	private static AtomicBoolean _isInited = new AtomicBoolean(false);
	
	public static final String KEY_UA_CHANNEL = "UACH";
	public static final String KEY_APP_KEY = "UA_APPKEY";
	
	public static int mPackageIconId;
	public static String mPackageName;
	public static String mApplicationName;
	public static Context mApplicationContext;
	
	public static String UA_CHANNEL;
	public static String UA_APP_KEY;

	public static int VERSION_NUM;
	public static String VERSION_NAME;
	
	public static boolean init(Context context) {
		if (_isInited.getAndSet(true)) return true;
		DebugLog.d(TAG, "action:init - uspush");
		
		ApplicationInfo appInfo = getAppInfo(context);
		if (null == appInfo) {
			DebugLog.e(TAG, "UAPush cannot be initialized compeletely due to NULL appInfo.");
			return false;
		}
		
		getVersionForApp(context);//get third-party user's App version code
		
		mApplicationContext = context.getApplicationContext();
		mPackageName = context.getPackageName();
		mPackageIconId = appInfo.icon;
		mApplicationName = context.getPackageManager().getApplicationLabel(appInfo).toString();
		
		Bundle metaData = null;
		try {
			ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			if (null != ai) metaData = ai.metaData;
			else DebugLog.d(TAG, "metadata: Can not get ApplicationInfo");
			
		} catch (NameNotFoundException e) {
			DebugLog.w(TAG, "Unexpected: failed to get current application info", e);
		}
		
		if (null != metaData) {
			UA_APP_KEY = metaData.getString(KEY_APP_KEY);
			if (StringUtils.isEmpty(UA_APP_KEY)) {
				DebugLog.d(TAG, "metadata: appKey - not defined in manifest");
			} else {
				DebugLog.d(TAG, "metadata: appKey - " + UA_APP_KEY);
			}
			UA_CHANNEL = metaData.getString(KEY_UA_CHANNEL);
			if (StringUtils.isEmpty(UA_CHANNEL)) {
				DebugLog.d(TAG, "metadata: channel - not defined in manifest");
			} else {
				DebugLog.d(TAG, "metadata: channel - " + UA_CHANNEL);
			}
		} else {
			DebugLog.d(TAG, "NO meta data defined in manifest.");
		}
		
		return true;
	}
	
	
	public static void getVersionForApp(Context context) {
		PackageInfo pinfo;
		try {
			pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			VERSION_NUM = pinfo.versionCode;
			VERSION_NAME = pinfo.versionName;
		} catch (NameNotFoundException e) {
			DebugLog.d(TAG, "NO versionCode or versionName defined in manifest.");
		}
		
	}
	
	public static ApplicationInfo getAppInfo(Context context) {
		try {
			return context.getPackageManager().getApplicationInfo(
					context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			DebugLog.e(TAG, "Unexpected: failed to get current application info", e);
		}
		return null;
	}
}
