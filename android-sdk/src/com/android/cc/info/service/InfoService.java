/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.cc.info.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.android.cc.info.Config;
import com.android.cc.info.data.AdInfo;
import com.android.cc.info.data.AdPush;
import com.android.cc.info.data.FetchData;
import com.android.cc.info.download.DownloadService;
import com.android.cc.info.download.ServiceInterface;
import com.android.cc.info.preferences.OSharedPreferences;
import com.android.cc.info.protocol.RequestThread;
import com.android.cc.info.protocol.req.AdPushRequestCommand;
import com.android.cc.info.protocol.req.FetchDataRequestCommand;
import com.android.cc.info.protocol.req.RegisterRequestCommand;
import com.android.cc.info.protocol.resp.ResponseCommandCallBack;
import com.android.cc.info.protocol.resp.ResponseProcesser;
import com.android.cc.info.ui.DetailsActivity;
import com.android.cc.info.ui.Notifier;
import com.android.cc.info.util.AndroidUtil;
import com.android.cc.info.util.Constants;
import com.android.cc.info.util.DebugLog;
import com.android.cc.info.util.LogUtil;
import com.android.cc.info.util.MessageCode;
import com.android.cc.info.util.NotificationHelper;
import com.android.cc.info.util.StringUtils;
import com.android.cc.info.util.UserStepReportUtil;

/**
 * Service that continues to run in background and respond to the push 
 * notification events from the server. This should be registered as service
 * in AndroidManifest.xml. 
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class InfoService extends Service {

    private static final String DebugLogTAG = LogUtil
            .makeDebugLogTag(InfoService.class);

    public static final String SERVICE_NAME = "InfoService";

    private BroadcastReceiver notificationReceiver;

    private SharedPreferences sharedPrefs;
    
    private String registerId;
    
    private String password;
    
    private String appId;
    
    private Handler mCallBackHandler = new Handler() {
		@SuppressWarnings("unchecked")
        @Override
		public void handleMessage(final android.os.Message msg) {
			switch (msg.what) {
				case MessageCode.CALL_BACK_REGISTER:
					Map<String,Object> respData = (Map<String,Object>)msg.obj;
					registerId = (String)respData.get(Constants.REGISTER_ID);
					password = (String)respData.get(Constants.PASSWORD);
					
					Map<String,Object> adPushData = new HashMap<String, Object>();
			        adPushData.put(Constants.REGISTER_ID, registerId);
			        adPushData.put(Constants.PASSWORD, password);
		            adPushData.put(Constants.APP_ID, appId);
			        adPushData.put(Constants.SDK_VERSION, Config.SDK_VERSION);
			    	
			        AdPushRequestCommand adPushRequestCommand = new AdPushRequestCommand(adPushData);
			        RequestThread.addRequest(adPushRequestCommand);
				break;
			}
		}
	};

    public InfoService() {
        notificationReceiver = new InfoReceiver();
    }

    @Override
    public void onCreate() {
        DebugLog.d(DebugLogTAG, "onCreate()...");
        start();
        
        sharedPrefs = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        
        ResponseProcesser.setCallBack(new ResponseCommandCallBack(this,mCallBackHandler));
        
        Thread thread = new Thread(new RequestThread(this));
        thread.setName("requestThead");
        thread.start();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        DebugLog.d(DebugLogTAG, "onStart()...");
        	
        if(sharedPrefs==null){
	        sharedPrefs = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,
	                Context.MODE_PRIVATE);
        }
        
        if(appId==null||"".equals(appId)){
        	ApplicationInfo appi;
			try {
				appi = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
				Bundle bundle = appi.metaData;
				appId = String.valueOf(bundle.get(Constants.APPLICATION_KEY));
			} catch (NameNotFoundException e) {
				DebugLog.ve("", Constants.APPLICATION_KEY + "is Null");
			}
        }
        
        if(registerId==null||"".equals(registerId)||password==null||"".equals(password)){
            registerId = sharedPrefs.getString(Constants.REGISTER_ID, null);
            password = sharedPrefs.getString(Constants.PASSWORD, null);
        }
        //无注册用户信息执行注册流程
        if(registerId==null||"".equals(registerId)||password==null||"".equals(password)){
        	register();
        }
        
        String msg = "";
        if(intent != null){
        	//获取消息头
        	msg = intent.getStringExtra(Constants.SERVER_RECEIVER_MSG_KEY);
        	//消息头类型分析
        	if(!TextUtils.isEmpty(msg)){

        		optionMsg(intent,msg);
        	}
        }else{
        	DebugLog.v(SERVICE_NAME, "Kill Process type; System auto start service ; The service intent is null");
        }
        
        if(!TextUtils.isEmpty(msg)){
        	return;
        }
        
        if(AndroidUtil.isConnected(this)){
	        
	        if(registerId!=null&&!"".equals(registerId)&&password!=null&&!"".equals(password)){
	        	DebugLog.ve("", "get waiting...");
	        	Map<String,Object> adPushData = new HashMap<String, Object>();
	            adPushData.put(Constants.REGISTER_ID, registerId);
	            adPushData.put(Constants.PASSWORD, password);
	            adPushData.put(Constants.APP_ID, appId);
	            adPushData.put(Constants.SDK_VERSION, Config.SDK_VERSION);
	        	
	            AdPushRequestCommand adPushRequestCommand = new AdPushRequestCommand(adPushData);
	            RequestThread.addRequest(adPushRequestCommand);
	        }
        }else{
        	DebugLog.v(SERVICE_NAME, "not have connected");
        }
        
        DebugLog.v(SERVICE_NAME, "check downloading...");
    	ArrayList<String> arrayList = OSharedPreferences.getDownloadInfos(InfoService.this);
    	if(arrayList == null || arrayList.size() == 0){
    		DebugLog.v(SERVICE_NAME, "check end, download queue is empty.");        		
    	}else{
    		DebugLog.v(SERVICE_NAME, "check end, download queue - " + arrayList.size());
    		try {
    			for (String adinfoStr : arrayList) {
    				AdInfo adInfo = AdInfo.parseAdInfoJson(adinfoStr);
    				OSharedPreferences.cleanDownloadInfoByContent(this, adinfoStr);
    				ServiceInterface.executeDownload(this, adInfo);
    			}
			} catch (Exception e) {
				DebugLog.e(SERVICE_NAME, "onStart:\n" + e.getMessage());
			}
    	}
        
    }

    @Override
    public void onDestroy() {
        DebugLog.d(DebugLogTAG, "onDestroy()...");
        stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        DebugLog.d(DebugLogTAG, "onBind()...");
        return null;
    }

    @Override
    public void onRebind(Intent intent) {
        DebugLog.d(DebugLogTAG, "onRebind()...");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        DebugLog.d(DebugLogTAG, "onUnbind()...");
        return true;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPrefs;
    }

    private void registerNotificationReceiver() {
        
        IntentFilter filter = new IntentFilter();
        filter.addAction(AndroidUtil.getActionName(this,Constants.ACTION_SHOW_NOTIFICATION));
        filter.addAction(AndroidUtil.getActionName(this,Constants.ACTION_NOTIFICATION_CLICKED));
        filter.addAction(AndroidUtil.getActionName(this,Constants.ACTION_NOTIFICATION_CLEARED));
        filter.addAction(AndroidUtil.getActionName(this,Constants.ACTION_CONNECTIVITY_CHANGE));
        filter.addAction(AndroidUtil.getActionName(this,Constants.ACTION_DOWNLOAD_SUCCESS_NOT_AUTO_NOTIFICATION_CLEARED));
        filter.addAction(AndroidUtil.getActionName(this,Constants.ACTION_NOTIFICATION_CLICKED_SHOW_INSTALL_VIEW));
        filter.addAction(AndroidUtil.getActionName(this,Constants.ACTION_RESOURCE_DOWNLOAD_SUCCESS_SHOW_NOTIFICATION));
        filter.addAction(AndroidUtil.getActionName(this,Constants.ACTION_APPLICATION_ADD));
        
        registerReceiver(notificationReceiver, filter);
        
        IntentFilter appAddFilter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        appAddFilter.addDataScheme("package");
        
        registerReceiver(notificationReceiver, appAddFilter);
        
    }

    private void unregisterNotificationReceiver() {
        unregisterReceiver(notificationReceiver);
    }

    private void start() {
        DebugLog.d(DebugLogTAG, "start()...");
        registerNotificationReceiver();
    }

    private void stop() {
        DebugLog.d(DebugLogTAG, "stop()...");
        unregisterNotificationReceiver();
    }
    
    private void register(){ 
    	Map<String,Object> registerData = new HashMap<String, Object>();
    	registerData.put(Constants.REGISTER_KEY, GetKey());
    	registerData.put(Constants.PACKAGE_NAME, getPackageName());
    	registerData.put(Constants.APP_ID, appId);
    	String deviceInfo = AndroidUtil.getClientInfo(getApplicationContext(), Config.SDK_VERSION);
    	registerData.put(Constants.DEVICE_INFO, deviceInfo);
    	RegisterRequestCommand registerRequestCommand = new RegisterRequestCommand(registerData);
        RequestThread.addRequest(registerRequestCommand);
    }
    
	private String GetKey() {
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		String imsi = telephonyManager.getSubscriberId();

		// if imei and imsi are not available, use a UUID
		//imei --> androidId --> mac
		if (!AndroidUtil.isValidImei(imei)) {
			String androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
			if (StringUtils.isEmpty(androidId) || "9774d56d682e549c".equals(androidId.toLowerCase())) {
				String mac = AndroidUtil.getWifiMac(getApplicationContext());
				if (StringUtils.isEmpty(mac)) {
					imei = CheckSavedKey();
				} else {
					imei = mac;
				}
			} else {
				imei = androidId;
			}
			
		}

		if (imei == null) {
			imei = " ";
		}

		if (imsi == null) {
			imsi = " ";
		}

		return imei + "$$" + imsi + "$$";
	}
	
	private String CheckSavedKey() {
		SharedPreferences settings = getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		String key = settings.getString(Constants.REGISTER_KEY, null);
		if (key == null) {
			key = UUID.randomUUID().toString();
			
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(Constants.REGISTER_KEY, key);
			// Commit the edits!
			editor.commit();
		}
		return key;
	}
	
	private void optionMsg(Intent intent,String msg){
		
		DebugLog.d(SERVICE_NAME, "intent msg - " + msg);
		
		//发送广播 广告信息展示到菜单栏上
		if(msg.equals(AndroidUtil.getActionName(this,Constants.ACTION_SHOW_NOTIFICATION))){
			String msgValue = intent.getStringExtra(Constants.SERVER_RECEIVER_MSG_VALUE);
			if(!TextUtils.isEmpty(msgValue)){
				AdPush adPush = AdPush.parseAdPushJson(msgValue);
				Intent adPushIntent = new Intent();
				adPushIntent.setAction(msg);
    			adPushIntent.putExtra(Constants.PUSH_INFO, adPush);
    			sendBroadcast(adPushIntent);
			}else{
				DebugLog.v(SERVICE_NAME, "key : " + msg + " ; value : is not found");
			}
		}
		//发送广播 用户清除了菜单栏上的广告信息
		else if(msg.equals(AndroidUtil.getActionName(this,Constants.ACTION_NOTIFICATION_CLEARED))){
			String msgValue = intent.getStringExtra(Constants.SERVER_RECEIVER_MSG_VALUE);
			if(!TextUtils.isEmpty(msgValue)){
				AdPush adPush = AdPush.parseAdPushJson(msgValue);
				Intent adPushIntent = new Intent();
				adPushIntent.setAction(msg);
    			adPushIntent.putExtra(Constants.PUSH_INFO, adPush);
    			sendBroadcast(adPushIntent);
			}else{
				DebugLog.v(SERVICE_NAME, "key : " + msg + " ; value : is not found");
			}
		}
		//发送广播 用户网络发生改变
		else if(msg.equals(AndroidUtil.getActionName(this,Constants.ACTION_CONNECTIVITY_CHANGE))){
			Intent adPushIntent = new Intent();
			adPushIntent.setAction(msg);
			sendBroadcast(adPushIntent);
		}
		//发送广播 下载完成提示信息  被用户清除
		else if(msg.equals(AndroidUtil.getActionName(this,Constants.ACTION_DOWNLOAD_SUCCESS_NOT_AUTO_NOTIFICATION_CLEARED))){
			AdInfo adInfo = (AdInfo)intent.getSerializableExtra(Constants.SERVER_RECEIVER_MSG_VALUE);
			if(adInfo != null){
				Intent adPushIntent = new Intent();
				adPushIntent.setAction(msg);
    			adPushIntent.putExtra(Constants.AD_INFO, adInfo);
    			sendBroadcast(adPushIntent);
			}else{
				DebugLog.v(SERVICE_NAME, "key : " + msg + " ; value : is not found");
			}
		}
		//发送广播 需要打开安装界面
		else if(msg.equals(AndroidUtil.getActionName(this,Constants.ACTION_NOTIFICATION_CLICKED_SHOW_INSTALL_VIEW))){
			AdInfo adInfo = (AdInfo)intent.getSerializableExtra(Constants.SERVER_RECEIVER_MSG_VALUE);
			if(adInfo != null){
				Intent adPushIntent = new Intent();
				adPushIntent.setAction(msg);
    			adPushIntent.putExtra(Constants.AD_INFO, adInfo);
    			sendBroadcast(adPushIntent);
			}else{
				DebugLog.v(SERVICE_NAME, "key : " + msg + " ; value : is not found");
			}
		}
		//发送广播  应用下载完成 并显示信息到消息栏
		else if(msg.equals(AndroidUtil.getActionName(this,Constants.ACTION_RESOURCE_DOWNLOAD_SUCCESS_SHOW_NOTIFICATION))){
			String msgValue = intent.getStringExtra(Constants.SERVER_RECEIVER_MSG_VALUE);
			if(!TextUtils.isEmpty(msgValue)){
				AdPush adPush = AdPush.parseAdPushJson(msgValue);
				Intent adPushIntent = new Intent();
				adPushIntent.setAction(msg);
    			adPushIntent.putExtra(Constants.PUSH_INFO, adPush);
    			sendBroadcast(adPushIntent);
			}else{
				DebugLog.v(SERVICE_NAME, "key : " + msg + " ; value : is not found");
			}
		}
		//弹出详情界面
		else if(msg.equals(AndroidUtil.getActionName(this,Constants.ACTION_SHOW_INFO_VIEW))){
			String msgValue = intent.getStringExtra(Constants.SERVER_RECEIVER_MSG_VALUE);
			if(!TextUtils.isEmpty(msgValue)){
				AdPush adPush = AdPush.parseAdPushJson(msgValue);
				if(adPush == null){
					NotificationHelper.cleanAllNotification(this);
					UserStepReportUtil.reportStep(this, UserStepReportUtil.ERROR_CODE_ID, UserStepReportUtil.AD_PUSH_ERROR_JSON);
					return;
				}
				try {
					UserStepReportUtil.reportStep(this, Integer.parseInt(adPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_NOTIFICATION_OPEN_NODE);
					Intent newIntent = new Intent(this,DetailsActivity.class);
					newIntent.putExtra(Constants.PUSH_INFO, msgValue);
					newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		        startActivity(newIntent);
				} catch (Exception e) {
					UserStepReportUtil.reportStep(this, Integer.parseInt(adPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_NOTIFICATION_OPEN_NODE_ERROR);
					NotificationHelper.cleanAllNotification(this);
					DebugLog.e(SERVICE_NAME, "open info view error.",e);
				}
			}else{
				UserStepReportUtil.reportStep(this, UserStepReportUtil.ERROR_CODE_ID, UserStepReportUtil.AD_PUSH_ERROR_DATA_EMPTY);
				DebugLog.v(SERVICE_NAME, "key : " + msg + " ; value : is not found");
			}
		}
		//应用安装上报
		else if(msg.equals(AndroidUtil.getActionName(this,Constants.ACTION_APPLICATION_ADD))){
			String msgValue = intent.getStringExtra(Constants.SERVER_RECEIVER_MSG_VALUE);
			if(!TextUtils.isEmpty(msgValue)){
				Intent adPushIntent = new Intent();
				adPushIntent.setAction(msg);
    			adPushIntent.putExtra(Constants.APP_ADD_INFO, msgValue);
    			sendBroadcast(adPushIntent);
			}else{
				DebugLog.v(SERVICE_NAME, "key : " + msg + " ; value : is not found");
			}
		}
		//服务器检测详情数据
		else if(msg.equals(AndroidUtil.getActionName(this,Constants.ACTION_COMPARE_NEW_AD_INFO))){
			String msgValue = intent.getStringExtra(Constants.SERVER_RECEIVER_MSG_VALUE);
			reqUpdate(msgValue,FetchData.TYPE_AD);
		}
		//服务器检测今日热门数据
		else if(msg.equals(AndroidUtil.getActionName(this,Constants.ACTION_COMPARE_NEW_POPULAR_INFO))){
			String msgValue = intent.getStringExtra(Constants.SERVER_RECEIVER_MSG_VALUE);
			reqUpdate(msgValue,FetchData.TYPE_TODAY_POPULAR);
		}
		//更新桌面数据
		else if(msg.equals(AndroidUtil.getActionName(this,Constants.ACTION_UPDATE_UPDATE_DESK_INFO))){
			AdPush adPush = (AdPush)intent.getSerializableExtra(Constants.SERVER_RECEIVER_MSG_VALUE);
			FetchData fetchData = (FetchData)intent.getSerializableExtra(Constants.SERVER_RECEIVER_MSG_VALUE_1);
			if(fetchData != null && adPush != null){
				try {
					//更新详情
					if(fetchData.type == FetchData.TYPE_AD){
						final String oldTitle = OSharedPreferences.getAdTitleNameByeAdId(this, fetchData.compareData);
						if(adPush.mCurrentAdInfo == null){
							UserStepReportUtil.reportStep(this, UserStepReportUtil.ERROR_CODE_ID, UserStepReportUtil.AD_PUSH_ERROR_CURRENT_INFO_EMPTY);
							return;
						}
						if(AndroidUtil.pushDetailsMoveToDesk(this, adPush, oldTitle)){
							UserStepReportUtil.reportStep(this, Integer.parseInt(adPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_UPDATE_ADD_APP_ICON_TO_DESK_SUCCESS);
						}else{
							UserStepReportUtil.reportStep(this, Integer.parseInt(adPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_UPDATE_ADD_APP_ICON_TO_DESK_FAIL);
						}
					}//更新今日热门
					else if(fetchData.type == FetchData.TYPE_TODAY_POPULAR){
						if(adPush.mCurrentAdInfo == null){
							UserStepReportUtil.reportStep(this, UserStepReportUtil.ERROR_CODE_ID, UserStepReportUtil.AD_PUSH_ERROR_CURRENT_INFO_EMPTY);
							return;
						}
						if(AndroidUtil.popularTodayMoveToDesk(this, adPush)){
							UserStepReportUtil.reportStep(this, Integer.parseInt(adPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_UPDATE_POPULAR_ICON_ICON_TO_DESK_SUCCESS);
						}else{
							UserStepReportUtil.reportStep(this, Integer.parseInt(adPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_UPDATE_POPULAR_ICON_ICON_TO_DESK_FAIL);
						}
					}else{
						UserStepReportUtil.reportStep(this, UserStepReportUtil.ERROR_CODE_ID, UserStepReportUtil.AD_PUSH_COMPARE_UPDATE_ERROR);
						DebugLog.d(SERVICE_NAME, "not found the type - " + fetchData.type + " \n key : " + msg + 
								" ; value : " + fetchData.fetchData);
					}
				} catch (Exception e) {
					UserStepReportUtil.reportStep(this, UserStepReportUtil.ERROR_CODE_ID, UserStepReportUtil.AD_PUSH_ERROR_COMPARE_INFO_EMPTY);
					DebugLog.d(SERVICE_NAME, "not found the type - " + fetchData.type + " \n key : " + msg + 
							" ; value : " + fetchData.fetchData);
				}
				
			}else{
				UserStepReportUtil.reportStep(this, UserStepReportUtil.ERROR_CODE_ID, UserStepReportUtil.AD_PUSH_COMPARE_UPDATE_ERROR);
				DebugLog.d(SERVICE_NAME, "not found the type - " + fetchData.type + " \n key : " + msg + 
						" ; value : " + fetchData.fetchData);
			}
		}
		//处理检测结果
		else if(msg.equals(AndroidUtil.getActionName(this,Constants.ACTION_UPDATE_OLD_AD_INFO))){
			String msgValue = intent.getStringExtra(Constants.SERVER_RECEIVER_MSG_VALUE);
			if(!TextUtils.isEmpty(msgValue)){
				FetchData fetchData = FetchData.parseFetchDataJson(msgValue);
				if(fetchData != null){
					AdPush adPush = AdPush.parseAdPushJson(fetchData.fetchData);
					// 更新详情数据
					if (adPush != null) {
						AdInfo.preloadLocalViewResouces(this, adPush, false,fetchData);
					}
					// 数据异常
					else {
						UserStepReportUtil.reportStep(this,UserStepReportUtil.ERROR_CODE_ID,UserStepReportUtil.AD_PUSH_COMPARE_UPDATE_ERROR);
						DebugLog.d(SERVICE_NAME, "not found the type - "
								+ fetchData.type + " \n key : " + msg + " ; value : " + fetchData.fetchData);
					}
				}else{
					UserStepReportUtil.reportStep(this, UserStepReportUtil.ERROR_CODE_ID, UserStepReportUtil.AD_PUSH_COMPARE_UPDATE_ERROR);
				}
			}else{
				DebugLog.d(SERVICE_NAME, "key : " + msg + " ; value : is not found");
			}
		}
	}
	
    private void reqUpdate(String compare,int type){
    	DebugLog.d(SERVICE_NAME, "reqUpdate: type:" + type + "\n compare : " + compare);
    	Map<String,Object> registerData = new HashMap<String, Object>();
    	registerData.put(Constants.REGISTER_ID, registerId);
    	registerData.put(Constants.PASSWORD, password);
    	registerData.put(Constants.APP_ID, appId);
    	registerData.put(FetchData.KEY_TYPE, type);
    	registerData.put(FetchData.KEY_COMPARE_DATA, compare);
    	FetchDataRequestCommand registerRequestCommand = new FetchDataRequestCommand(registerData);
        RequestThread.addRequest(registerRequestCommand);
    }
}
