package com.android.cc.info.service;

import java.util.Calendar;
import java.util.Random;
import java.util.zip.Adler32;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.android.cc.info.preferences.OSharedPreferences;
import com.android.cc.info.util.AndroidUtil;
import com.android.cc.info.util.Constants;
import com.android.cc.info.util.DebugLog;


public class AlarmReceiver extends BroadcastReceiver {
	private static final String TAG = "AlarmReceiver";
	
	private static int mInterval = Constants.DEFAULT_INTERVAL;    // 秒

	@Override
	public void onReceive(Context context, Intent intent) {
		DebugLog.d(TAG, "onReceive");
		String action = intent.getAction();
		if(action.equals("android.net.conn.CONNECTIVITY_CHANGE")){
			stopRtc(context);
			final ServiceManager serviceManager = new ServiceManager(context);
			if(AndroidUtil.isConnected(context) && AndroidUtil.getOpenConnPushTime(context)){
				DebugLog.v(TAG, "CONNECTIVITY_CHANGE GET ADPUSH");
				final Context mContext = context;
				new Thread(){
					public void run() {
						try {
							int tme = getTime(mContext);
							DebugLog.d(TAG, mContext.getPackageName() + " -- tme0 " + tme);
							Thread.sleep(tme);
						} catch (Exception e) {
						}
						serviceManager.startService();
					};
				}.start();
		    	OSharedPreferences.setLastConnUpdateTime(context, System.currentTimeMillis());
			}else{
				serviceManager.sendReceiver(AndroidUtil.getActionName(context,Constants.ACTION_CONNECTIVITY_CHANGE), "");
				DebugLog.v(TAG, "CONNECTIVITY_CHANGE time out : " + OSharedPreferences.getLastConnUpdateTime(context));
			}
			startRtc(context);
		}else if(action.equals(Constants.ACTION_PUT_RTC_INFO)){
			if(AndroidUtil.isConnected(context)){
				//时间间隔检测
		        if(AndroidUtil.getOpenPushTime(OSharedPreferences.getLastPushTime(context),context)){
		        	final ServiceManager serviceManager = new ServiceManager(context);
		        	final Context mContext = context;
		        	new Thread(){
						public void run() {
							try {
								int tme = getTime(mContext);
								DebugLog.d(TAG, mContext.getPackageName() + " -- tme1 " + tme);
								Thread.sleep(tme);
							} catch (Exception e) {
							}
				        	serviceManager.startService();
						};
					}.start();
		        	
		            OSharedPreferences.setLastPushTime(context, System.currentTimeMillis());
		        }else{
		        	DebugLog.v(TAG, "time out : " + OSharedPreferences.getLastPushTime(context));
		        }
			}
			startRtc(context);
		}
		
	}
	
	public int getTime(Context context) {
		try {
			String appName = context.getPackageName();
	    	int tmpNum = 4;
	    	if(appName.length() >= 6){
	    		tmpNum += 3 + new Random().nextInt(appName.length());
	    	}
	    	String tmpId = "";
	    	for (int i = 0; i < tmpNum; i++) {
	    		int index = new Random().nextInt(appName.length() - 2);
	    		tmpId += appName.substring(index, index + 1);
			}
	        if (TextUtils.isEmpty(tmpId)) {
	            return 0;
	        }
	        String tmp = "";
	        int tmpIntId = 0;
	        ApplicationInfo appi;
	        try {
	        	appi = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
				Bundle bundle = appi.metaData;
				tmp = String.valueOf(bundle.get(Constants.APPLICATION_KEY));
				tmp = tmp.substring(tmp.length() - 3, tmp.length());
				Adler32 adler323 = new Adler32();
		        adler323.update(tmpId.getBytes());
		        tmpIntId = (int) adler323.getValue();
		        if (tmpIntId < 0) {
		        	tmpIntId = Math.abs(tmpIntId);
		        }
			} catch (Exception e) {
				DebugLog.e(TAG,"not found application key .\n",e);
				tmp = "";
			}
			
	        int nId = 0;
	        Adler32 adler32 = new Adler32();
	        adler32.update(tmpId.getBytes());
	        nId = (int) adler32.getValue();
	        if (nId < 0) {
	            nId = Math.abs(nId);
	        }
	        
	        nId += tmpIntId;
	        
	        if (nId < 0) {
	        	nId = Math.abs(nId);
	        }
	        nId %= 10000;
	        return nId;
		} catch (Exception e) {
			DebugLog.e(TAG,"",e);
			return 0;
		}
        
        
    }
	
	public static void startRtc(Context context) {
		ApplicationInfo appi;
		try {
			appi = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			Bundle bundle = appi.metaData;
			mInterval = Integer.parseInt(String.valueOf(bundle.get(Constants.APPLICATION_CC_RTC)));
		} catch (Exception e) {
			mInterval = Constants.DEFAULT_INTERVAL;
		}
		startRtc(context.getApplicationContext(),mInterval);
	}
	
	private static void startRtc(Context context,int interval) {
		DebugLog.d(TAG, "starRtc");
		
		Long oldTime = OSharedPreferences.getRtcOldTime(context);
		Long newTime = System.currentTimeMillis();
		DebugLog.d(TAG, "offset time : " + Math.abs((newTime - oldTime)));
		if(oldTime == 0 || Math.abs((newTime - oldTime)) >= interval * 1000){
			OSharedPreferences.setRtcOldTime(context, newTime);
		}else{
			return;
		}
		
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.setAction(Constants.ACTION_PUT_RTC_INFO);
		PendingIntent sender = PendingIntent.getBroadcast(context, OSharedPreferences.getAlarmCode(context), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.SECOND, interval);
		
		// Schedule the alarm!
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
		DebugLog.i(TAG, String.format("Alarm started with interval: %ds", interval));
	}
	
	public static void stopRtc(Context context) {
		DebugLog.d(TAG, "action:stopRtc");
		
		OSharedPreferences.setRtcOldTime(context, 0L);
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.setAction(Constants.ACTION_PUT_RTC_INFO);
		PendingIntent sender = PendingIntent.getBroadcast(context, OSharedPreferences.getAlarmCode(context), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(sender);
	}
}
