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

import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.android.cc.info.conn.DynamicServerHelper;
import com.android.cc.info.data.AdInfo;
import com.android.cc.info.data.AdPush;
import com.android.cc.info.download.DownloadService;
import com.android.cc.info.download.ServiceInterface;
import com.android.cc.info.preferences.OSharedPreferences;
import com.android.cc.info.protocol.RequestThread;
import com.android.cc.info.protocol.req.BatchUserStepRequestCommand;
import com.android.cc.info.ui.Notifier;
import com.android.cc.info.util.AndroidUtil;
import com.android.cc.info.util.Constants;
import com.android.cc.info.util.DebugLog;
import com.android.cc.info.util.LogUtil;
import com.android.cc.info.util.NotificationHelper;
import com.android.cc.info.util.StringUtils;
import com.android.cc.info.util.UserStepReportUtil;

/** 
 * Broadcast receiver that handles push notification messages from the server.
 * This should be registered as receiver in AndroidManifest.xml. 
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public final class InfoReceiver extends BroadcastReceiver {

    private static final String DebugLogTAG = LogUtil
            .makeDebugLogTag(InfoReceiver.class);

    @Override
    public void onReceive(Context context, Intent intent) {
        DebugLog.d(DebugLogTAG, "NotificationReceiver.onReceive()...");
        String action = intent.getAction();
        DebugLog.d(DebugLogTAG, "action=" + action);

        if (AndroidUtil.getActionName(context,Constants.ACTION_SHOW_NOTIFICATION).equals(action)) {
            AdPush adPush = (AdPush)intent.getSerializableExtra(Constants.PUSH_INFO);
            
            // TODO 增加判断是否可以展示
            // TODO 判断文件是否已经安装 并且是在非 强制更新的状态下
            if(AndroidUtil.isPackageExist(context, adPush.mCurrentAdInfo.apkPackageName) && !adPush.mCurrentAdInfo.updateApk){
            	DebugLog.d(DebugLogTAG, "file exist, report 102 to server,apk name - " + adPush.mCurrentAdInfo.apkPackageName);
            	UserStepReportUtil.reportStep(context, Integer.parseInt(adPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_EXIST);
            	return;
            }
            // TODO 加载资源
            AdInfo.preloadLocalViewResouces(context,adPush,true,null);
            
            OSharedPreferences.setLastPushTime(context, System.currentTimeMillis());
        
        } else if(action.equals(AndroidUtil.getActionName(context,Constants.ACTION_CONNECTIVITY_CHANGE))){
			DebugLog.v(DebugLogTAG, "the connectivity change now !!!!!");
        	if(AndroidUtil.isConnected(context)){
				DownloadService.startDownloadTasks(context);
				if(DynamicServerHelper.mDynamicServerList.size()<=0){
					DynamicServerHelper.resetServerAddressesWithThread();
				}
				String batchUserStep = OSharedPreferences.getRequestFailInfo(context);
				if(!StringUtils.isEmpty(batchUserStep)){
					Map<String,Object> batchStepMap = new HashMap<String, Object>();
					batchStepMap.put(Constants.BATCH_USER_STEP, batchUserStep);
					BatchUserStepRequestCommand batchUserStepRequestCommand = new BatchUserStepRequestCommand(batchStepMap);
					RequestThread.addRequest(batchUserStepRequestCommand);
				}
        	}
		} else if (action.equals(AndroidUtil.getActionName(context,Constants.ACTION_APPLICATION_ADD))) {
			String packageName = intent.getStringExtra(Constants.APP_ADD_INFO).replace("package:", "");
			String adId = OSharedPreferences.getNeedMonitoringForPackageName(context, packageName);
			if(!TextUtils.isEmpty(adId)){
				OSharedPreferences.setNeedMonitoringForPackageName(context, packageName);
				OSharedPreferences.setDownloadSuccessFlagForPackageName(context, packageName,true);
				int ad_Id = Integer.parseInt(adId);
				UserStepReportUtil.reportStep(context, ad_Id, UserStepReportUtil.AD_PUSH_APK_INSTALL_COMPLETE);
				Notifier notifier = new Notifier(context);
				notifier.clearNotification(AdInfo.getNofiticationID(adId));
			}
        } else if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
			String packageName = intent.getDataString().replace("package:", "");
			String adId = OSharedPreferences.getNeedMonitoringForPackageName(context, packageName);
			if(!TextUtils.isEmpty(adId)){
				OSharedPreferences.setNeedMonitoringForPackageName(context, packageName);
				OSharedPreferences.setDownloadSuccessFlagForPackageName(context, packageName,true);
				int ad_Id = Integer.parseInt(adId);
				UserStepReportUtil.reportStep(context, ad_Id, UserStepReportUtil.AD_PUSH_APK_INSTALL_COMPLETE);
				Notifier notifier = new Notifier(context);
				notifier.clearNotification(AdInfo.getNofiticationID(adId));
			}
        } else if(action.equals(AndroidUtil.getActionName(context,Constants.ACTION_NOTIFICATION_CLEARED))){
        	
        	AdPush adPush = (AdPush)intent.getSerializableExtra(Constants.PUSH_INFO);
        	UserStepReportUtil.reportStep(context, Integer.parseInt(adPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_CANCEL);
        	NotificationHelper.cancelNotification(context, adPush.mCurrentAdInfo.notifiId);
        	
        } else if(action.equals(AndroidUtil.getActionName(context,Constants.ACTION_DOWNLOAD_SUCCESS_NOT_AUTO_NOTIFICATION_CLEARED))){
        	
        	AdInfo adPush = (AdInfo)intent.getSerializableExtra(Constants.AD_INFO);
        	UserStepReportUtil.reportStep(context, Integer.parseInt(adPush.adId), UserStepReportUtil.AD_PUSH_DOWNLOAD_SUCCESS_CENCEL);
        	NotificationHelper.cancelNotification(context, adPush.notifiId);
        	
        } else if(action.equals(AndroidUtil.getActionName(context,Constants.ACTION_NOTIFICATION_CLICKED_SHOW_INSTALL_VIEW))){
        	
        	AdInfo adPush = (AdInfo)intent.getSerializableExtra(Constants.AD_INFO);
        	if(adPush != null){
        		AndroidUtil.installPackage(context, adPush.savePath);
        		UserStepReportUtil.reportStep(context, Integer.parseInt(adPush.adId), UserStepReportUtil.AD_PUSH_CLICK_SHOW_INSTALL_VIEW);
        		Notifier notifier = new Notifier(context);
        		notifier.downloadEndNotification(adPush, true);
        	}
        	
        } else if(action.equals(AndroidUtil.getActionName(context,Constants.ACTION_RESOURCE_DOWNLOAD_SUCCESS_SHOW_NOTIFICATION))){
        	
        	AdPush adPush = (AdPush)intent.getSerializableExtra(Constants.PUSH_INFO);
        	if(adPush != null){
        		AdInfo.checkDownloadModeAndAction(context, adPush);
        	}
        }
    }
}
