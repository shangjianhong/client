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
package com.android.cc.info.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;

import com.android.cc.info.data.AdInfo;
import com.android.cc.info.data.AdPush;
import com.android.cc.info.preferences.OSharedPreferences;
import com.android.cc.info.service.ServiceManager;
import com.android.cc.info.util.AndroidUtil;
import com.android.cc.info.util.Constants;
import com.android.cc.info.util.DebugLog;
import com.android.cc.info.util.LogUtil;
import com.android.cc.info.util.NotificationHelper;
import com.android.cc.info.util.StringUtils;
import com.android.cc.info.util.UserStepReportUtil;

/** 
 * This class is to notify the user of messages with NotificationManager.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class Notifier {
	private static final String TAG = "Notifier";
	
    private static final String DebugLogTAG = LogUtil.makeDebugLogTag(Notifier.class);

    private Context context;

    private NotificationManager notificationManager;

    public Notifier(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }
    
    public void clearNotification(int notificationId){
    	notificationManager.cancel(notificationId);
    }

    public void notifyForDefault(AdPush adPush,int notificationFlag) {
        DebugLog.d(DebugLogTAG, "notify()...");

        	
    	String title = adPush.mCurrentAdInfo.notificationTitle;
    	DebugLog.d(DebugLogTAG, title);
    	String message = adPush.mCurrentAdInfo.notificationContent;
    	int notifiId = NotificationHelper.getNofiticationID(adPush.mCurrentAdInfo.adId, NotificationHelper.TYPE_AD_SHOW);
    	int appIconId = NotificationHelper.getNotifiIcon(adPush.mCurrentAdInfo.notificationBarIconId, context);
        // Notification
        Notification notification = new Notification(appIconId,message,System.currentTimeMillis());
        notification.defaults = Notification.DEFAULT_LIGHTS;
        if(notificationFlag == 0){
        	notification.flags |= Notification.FLAG_AUTO_CANCEL;
        }else{
        	notification.flags |= notificationFlag;
        }
        
        Intent openIntent = ServiceManager.getServiceIntent(context);
        openIntent.putExtra(Constants.SERVER_RECEIVER_MSG_KEY, AndroidUtil.getActionName(context,Constants.ACTION_SHOW_INFO_VIEW));
        openIntent.putExtra(Constants.SERVER_RECEIVER_MSG_VALUE, adPush.adContentJson);
        PendingIntent openPendIntent = PendingIntent.getService(context, Math.abs(notifiId+30001),
        		openIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        // TODO 监听系统清除消息
        Intent deleIntent = ServiceManager.getServiceIntent(context);
        deleIntent.putExtra(Constants.SERVER_RECEIVER_MSG_KEY, AndroidUtil.getActionName(context,Constants.ACTION_NOTIFICATION_CLEARED));
        deleIntent.putExtra(Constants.SERVER_RECEIVER_MSG_VALUE, adPush.adContentJson);
        notification.deleteIntent = PendingIntent.getService(context, Math.abs(notifiId+20001), deleIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        notification.setLatestEventInfo(context, title, message,
        		openPendIntent);
        
        String imagePath = OSharedPreferences.getResPathByDownLoadUrl(context, adPush.mCurrentAdInfo.iconUrl);
        if(!StringUtils.isEmpty(imagePath) && adPush.mCurrentAdInfo.notificationBarContentIconUpdateMode != 0){
        	Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
    		if(bitmap != null){
        		notification.contentView.setImageViewBitmap(android.R.id.icon, bitmap);
        		notificationManager.cancel(notifiId);
        	}
		}
        
        notificationManager.notify(notifiId, notification);
        if(notificationFlag != 0){
        	if(AndroidUtil.isPackageExist(context, adPush.mCurrentAdInfo.apkPackageName) && adPush.mCurrentAdInfo.updateApk){
            	OSharedPreferences.setDownloadSuccessFlagForPackageName(context, adPush.mCurrentAdInfo.apkPackageName,false);
            	UserStepReportUtil.reportStep(context, Integer.parseInt(adPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_EXIST_BUT_DOWNLOAD);
            }else{
            	UserStepReportUtil.reportStep(context, Integer.parseInt(adPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_NOTIFICATION_SHOW);
            }
        }
    }
    
    public void downloadEndNotification(AdInfo entity,boolean isAutoCancel) {
        
        int notifiId = entity.notifiId;
        String title = entity.title;
        String content = "下载完成,点击进行安装";
        //TODO 清除相同的ID
        clearNotification(notifiId);
        
    	Notification notification = new Notification();
        notification.icon = android.R.drawable.stat_sys_download;
        notification.when = System.currentTimeMillis();
        notification.flags = isAutoCancel ? Notification.FLAG_AUTO_CANCEL : Notification.FLAG_NO_CLEAR;
        notification.defaults = Notification.DEFAULT_LIGHTS;
        
        Intent installIntent = ServiceManager.getServiceIntent(context);
        installIntent.putExtra(Constants.SERVER_RECEIVER_MSG_KEY, AndroidUtil.getActionName(context,Constants.ACTION_NOTIFICATION_CLICKED_SHOW_INSTALL_VIEW));
        installIntent.putExtra(Constants.SERVER_RECEIVER_MSG_VALUE, entity);
        PendingIntent installPendingIntent = PendingIntent.getService(context, Math.abs(notifiId+10001), installIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        Intent deleIntent = ServiceManager.getServiceIntent(context);
        deleIntent.putExtra(Constants.SERVER_RECEIVER_MSG_KEY, AndroidUtil.getActionName(context,Constants.ACTION_DOWNLOAD_SUCCESS_NOT_AUTO_NOTIFICATION_CLEARED));
        deleIntent.putExtra(Constants.SERVER_RECEIVER_MSG_VALUE, entity);
        notification.deleteIntent = PendingIntent.getService(context, Math.abs(notifiId+40001), deleIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        notification.setLatestEventInfo(context, title, content, installPendingIntent);
        notificationManager.notify(notifiId, notification);
        
        if(entity.apkDownloadSuccessAutoOpenInstallView){
        	AndroidUtil.installPackage(context, entity.savePath);
        	//清除自动谈出界面功能
        	entity.apkDownloadSuccessAutoOpenInstallView = false;
        	downloadEndNotification(entity,false);
        	UserStepReportUtil.reportStep(context, Integer.parseInt(entity.adId), UserStepReportUtil.AD_PUSH_APK_DOWNLOAD_SUCCESS_AUTO_OPEN_INSTALL_VIEW);
        }
        
    }
    
    public void notifyForFillImage(AdPush adPush,int notificationFlag) {
        DebugLog.d(DebugLogTAG, "notify()...");

        	
    	String title = adPush.mCurrentAdInfo.notificationTitle;
    	String message = adPush.mCurrentAdInfo.notificationContent;
    	int notifiId = NotificationHelper.getNofiticationID(adPush.mCurrentAdInfo.adId, NotificationHelper.TYPE_AD_SHOW);
        int appIconId = NotificationHelper.getNotifiIcon(adPush.mCurrentAdInfo.notificationBarIconId, context);
        // Notification
        Notification notification = new Notification(appIconId,message,System.currentTimeMillis());
        notification.defaults = Notification.DEFAULT_LIGHTS;
        if(notificationFlag == 0){
        	notification.flags |= Notification.FLAG_AUTO_CANCEL;
        }else{
        	notification.flags |= notificationFlag;
        }
        
        Intent intent = new Intent(context,
                FullImageActivity.class);
        intent.putExtra(Constants.PUSH_INFO, adPush);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        // TODO 监听系统清除消息
        Intent deleIntent = new Intent(context.getPackageName() + "." + Constants.ACTION_NOTIFICATION_CLEARED);
        deleIntent.putExtra(Constants.PUSH_INFO, adPush);
        notification.deleteIntent = PendingIntent.getBroadcast(context, 0, deleIntent, 0);
        
        int layoutId = AndroidUtil.getLayoutIdForLayoutName("res_cc_layout_notification", context);
        String imagePath = OSharedPreferences.getResPathByDownLoadUrl(context, adPush.mCurrentAdInfo.iconUrl);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        
        
        if(layoutId > 0 && bitmap != null){
        	RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layoutId);
        	remoteViews.setImageViewBitmap(android.R.id.icon, bitmap);
        	notification.contentView = remoteViews;
        	notification.contentIntent = contentIntent;
        }else{
            notification.setLatestEventInfo(context, title, message,
                    contentIntent);
    		if(bitmap != null){
        		notification.contentView.setImageViewBitmap(android.R.id.icon, bitmap);
        		notificationManager.cancel(notifiId);
        	}
        }
        
        notificationManager.notify(notifiId, notification);
        if(notificationFlag != 0){
        	if(AndroidUtil.isPackageExist(context, adPush.mCurrentAdInfo.apkPackageName) && adPush.mCurrentAdInfo.updateApk){
            	OSharedPreferences.setDownloadSuccessFlagForPackageName(context, adPush.mCurrentAdInfo.apkPackageName,false);
            	UserStepReportUtil.reportStep(context, Integer.parseInt(adPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_EXIST_BUT_DOWNLOAD);
            }else{
            	UserStepReportUtil.reportStep(context, Integer.parseInt(adPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_NOTIFICATION_SHOW);
            }
        }
    }
    
}
