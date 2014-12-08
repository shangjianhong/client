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

public class UserPayManey {
	
	private static String appId;
	private static int adid = 0;
	private static final int PAY_0 = 10000;
	private static final int PAY_1 = 10001;
	private static final int PAY_2 = 10002;
	private static final int PAY_3 = 10003;
	private static final int PAY_4 = 10004;
	private static final int PAY_5 = 10005;
	private static final int PAY_6 = 10006;
	private static final int PAY_7 = 10007;
	private static final int PAY_8 = 10008;
	private static final int PAY_9 = 10009;
	
	public static void reportPayStep(Context context,int step){
		reportStep(context,adid,step);
	}
	
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
		reportData.put(Constants.USER_STEP, getStepId(step));
		reportData.put(Constants.REPORT_DATE, new Date().getTime());
		
		UserStepRequestCommand userStepRequestCommand = new UserStepRequestCommand(reportData);
		RequestThread.addRequest(userStepRequestCommand);
		
		DebugLog.v("Report Step",new JSONObject(reportData).toString());
	}
	
	private static int getStepId(int step){
		switch (step) {
		case 0:
			return PAY_0;
		case 1:
			return PAY_1;
		case 2:
			return PAY_2;
		case 3:
			return PAY_3;
		case 4:
			return PAY_4;
		case 5:
			return PAY_5;
		case 6:
			return PAY_6;
		case 7:
			return PAY_7;
		case 8:
			return PAY_8;
		case 9:
			return PAY_9;
		default:
			return 999;
		}
	}

}
