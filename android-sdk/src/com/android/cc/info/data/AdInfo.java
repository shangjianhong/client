package com.android.cc.info.data;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.zip.Adler32;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.cc.info.download.ServiceInterface;
import com.android.cc.info.preferences.OSharedPreferences;
import com.android.cc.info.service.ServiceManager;
import com.android.cc.info.ui.Notifier;
import com.android.cc.info.util.AndroidUtil;
import com.android.cc.info.util.Constants;
import com.android.cc.info.util.DebugLog;
import com.android.cc.info.util.FileUtil;
import com.android.cc.info.util.HttpHelper;
import com.android.cc.info.util.StringUtils;
import com.android.cc.info.util.UADirectoryUtils;
import com.android.cc.info.util.UserStepReportUtil;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class AdInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7076715957579902565L;

	private static final String TAG = "AdInfo";
	
	
	// --------------------------------- AD Config
	
	protected static final int RESOURCE_RETRY_MORE = 4;
	protected static final int RESOURCE_RETRY_TIMES = 5;
	protected static final int RESOURCE_RETRY_INTERVAL = 1000 * 5;
	
	public boolean _isEverDownloadFailed = false;
	public boolean _isDownloadInterrupted = false;
	public boolean _isDownloadFinisehd = false;
	
	public int _downloadRetryTimes = DOWNLOAD_RETRY_TIMES_NOT_SET;
	public static final int DOWNLOAD_RETRY_TIMES_NOT_SET = -1;
	
	public int _downloadFileLengthIsErrorAndReturnTimes = 2;
	public static final int DOWNLOAD_FILELENGTH_ERROR_RETURN_TIMES = -1;
	
	public static final String AD_ID = "AD_ID";
	public static final String AD_BODY_CONTENT = "AD_BODY_CONTENT";
	public static final String AD_TYPE = "AD_TYPE";
	public static final String NOTIFICATION_ICON_URL = "NOTIFICATION_ICON_URL";
	public static final String NOTIFICATION_TITLE = "NOTIFICATION_TITLE";
	public static final String NOTIFICATION_CONTENT = "NOTIFICATION_CONTENT";
	public static final String NOTIFICATION_FLAG = "NOTIFICATION_FLAG";
	public static final String AD_CONTENT = "AD_CONTENT";
	public static final String APK_PACKAGE_NAME = "APK_PACKAGE_NAME";
	public static final String APK_DOWNLOAD_URL = "APK_DOWNLOAD_URL";
	public static final String APK_SHOW_INFO = "APK_SHOW_INFO";
	public static final String UPDATE_APK = "UPDATE_APK";  // 0:存在apk，不执行下载 1：执行强制下载
	public static final String DOWNLOAD_MODE = "DOWNLOAD_MODE"; // 当前应用下载模式
	public static final String TITLE = "TITLE";
	public static final String ICON_URL = "ICON_URL";
	public static final String VERSION = "VERSION";
	public static final String APK_SIZE = "APK_SIZE";
	public static final String DESC = "DESC";
	public static final String SCREEN_SHOT_URL = "SCREEN_SHOT_URL";
	public static final String APK_DOWNLOADS = "APK_DOWNLOADS";
	public static final String APK_STARS = "APK_STARS";
	public static final String FIRST_RECOMMEND = "FIRST_RECOMMEND";
	public static final String FIRST_RECOMMEND_IMAGE = "FIRST_RECOMMEND_IMAGE";
	public static final String BACK_BUTTON_TEXT = "BACK_TEXT";
	public static final String DOWNLOAD_BUTTON_TEXT = "DOWNLOAD_TEXT";
	public static final String POPULAR_TITLE_TEXT = "POPULAR_TITLE";
	public static final String NOTIFICATION_BAR_ICON_ID = "NOTIFICATION_BAR_ICON_ID";
	public static final String NOTIFICATION_CONTENT_ICON_UPDATE_MODE = "NOTIFICATION_CONTENT_ICON_UPDATE_MODE";
	public static final String APK_DOWNLOAD_SUCCESS_AUTO_OPEN_INSTALL_VIEW = "APK_DOWNLOAD_SUCCESS_AUTO_OPEN_INSTALL_VIEW";
	
	public String adId;
	public String adBodyContent;
	public int adType;
	
	public static final int ADTYPE_APK = 0;
	
	public String savePath;				//文件路径
	
	public String notificationIconUrl;
	
	public String notificationTitle;
	public String notificationContent;
	
	public String adContent;
	
	public String apkPackageName;
	
	public String apkDownloadUrl;
	
	public String apkShowInfo;
	
	public boolean updateApk = false;

	public String title;
	
	public String iconUrl;
	
	public String _iconFilePath;
	
	public String version;
	
	public String apkSize;
	
	public String desc;
	
	public String screenShotUrl;
	
	public String _screenShotFilePath;

	public int apkDownloads;
	
	public int apkStars;
	
	public int notifiId;
	
	public String fileName;
	
	public DownloadMode currentDownloadMode;
	
	public boolean currentSilenceMode = false;
	
	public boolean firstRecommend = false;
	
	public String firstRecommendImage ;
	
	public String _firstRecommendImageFilePath ;
	
	public String adContentJson;
	
	public String backButtonText;

	public String downloadButtonText;

	public String popularTitleText;
	
	public int notificationBarIconId;

	public int notificationBarContentIconUpdateMode;
	
	public boolean apkDownloadSuccessAutoOpenInstallView;
	/**
	 * 下载模式配置
	 * 0、正常模式不进行静默下载  1、wifi网络下静默下载 2、非wifi网络下静默下载 3、全网静默下载
	 */
	public enum DownloadMode{
		MODE_DEFAULT,
		MODE_WIFI_SILENCE,
		MODE_NOT_WIFI_SILENCE,
		MODE_ALL_SILENCE
	}
	
	public static AdInfo parseAdInfoJson(String adPushJson){
		AdInfo adInfo = null;
		try {
			JSONObject jsonObj = new JSONObject(adPushJson);
			adInfo = new AdInfo();
			adInfo.adContentJson = adPushJson;
			String adId = jsonObj.optString(AD_ID);
			String adBodyContent = jsonObj.optString(AD_BODY_CONTENT);
			int firstRecommend = jsonObj.optInt(FIRST_RECOMMEND);
			adInfo.firstRecommend = firstRecommend == 0 ? false : true;
			adInfo.adId = adId;
			adInfo.firstRecommendImage = jsonObj.optString(FIRST_RECOMMEND_IMAGE);
			adInfo.adBodyContent = adBodyContent;
			adInfo.backButtonText = jsonObj.optString(BACK_BUTTON_TEXT);
			adInfo.downloadButtonText = jsonObj.optString(DOWNLOAD_BUTTON_TEXT);
			adInfo.popularTitleText = jsonObj.optString(POPULAR_TITLE_TEXT);
			
			adInfo.notifiId = getNofiticationID(adId);
			
			parseAdBodyContentJson(adInfo);
			
			adInfo.fileName = adInfo.apkPackageName + ".apk";
		} catch (Exception e) {
			DebugLog.d(TAG, "parse adPush json error",e);
		}
		
		return adInfo;
	}
	
	private static void parseAdBodyContentJson(AdInfo adPush){
		try {
			JSONObject adBodyJson = new JSONObject(adPush.adBodyContent);
			int adType = adBodyJson.optInt(AD_TYPE);
			String notificationIconUrl = adBodyJson.optString(NOTIFICATION_ICON_URL);
			String notificationTitle = adBodyJson.optString(NOTIFICATION_TITLE);
			String notificationContent = adBodyJson.optString(NOTIFICATION_CONTENT);
			String adContent = adBodyJson.optString(AD_CONTENT);
			int notificationBarIconId = adBodyJson.optInt(NOTIFICATION_BAR_ICON_ID,0);
			int notificationBarContentIconUpdateMode = adBodyJson.optInt(NOTIFICATION_CONTENT_ICON_UPDATE_MODE,0);
			adPush.adType = adType;
			adPush.notificationIconUrl = notificationIconUrl;
			adPush.notificationTitle = notificationTitle;
			adPush.notificationContent = notificationContent;
			adPush.adContent = adContent;
			adPush.notificationBarIconId = notificationBarIconId;
			adPush.notificationBarContentIconUpdateMode = notificationBarContentIconUpdateMode;
			parseAdContentJson(adPush);
			
		} catch (Exception e) {
			DebugLog.d(TAG, "parse ad body content json error",e);
		}
	}
	
	private static void parseAdContentJson(AdInfo adPush){
		try {
			JSONObject adContentJson = new JSONObject(adPush.adContent);
			String apkPackageName = adContentJson.optString(APK_PACKAGE_NAME);
			String apkDownloadUrl = adContentJson.optString(APK_DOWNLOAD_URL);
			String apkShowInfo = adContentJson.optString(APK_SHOW_INFO);
			boolean apkUpdateApk = adContentJson.optInt(UPDATE_APK,0) == 0 ? false : true;
			int downloadMode = adContentJson.optInt(DOWNLOAD_MODE, 0);
			boolean apkDownloadSuccessAutoOpenInstallView = adContentJson.optInt(APK_DOWNLOAD_SUCCESS_AUTO_OPEN_INSTALL_VIEW,0) == 0 ? false : true;
			switch (downloadMode) {
				case 0:
					adPush.currentDownloadMode = DownloadMode.MODE_DEFAULT;
					adPush.currentSilenceMode = false;
					break;
				case 1:
					adPush.currentDownloadMode = DownloadMode.MODE_WIFI_SILENCE;
					adPush.currentSilenceMode = true;
					break;
				case 2:
					adPush.currentDownloadMode = DownloadMode.MODE_NOT_WIFI_SILENCE;
					adPush.currentSilenceMode = true;
					break;
				case 3:
					adPush.currentDownloadMode = DownloadMode.MODE_ALL_SILENCE;
					adPush.currentSilenceMode = true;
					break;
				default:
					adPush.currentDownloadMode = DownloadMode.MODE_DEFAULT;
					adPush.currentSilenceMode = false;
					break;
			}
			adPush.apkPackageName = apkPackageName;
			adPush.apkDownloadUrl = apkDownloadUrl;
			adPush.apkShowInfo = apkShowInfo;
			adPush.updateApk = apkUpdateApk;
			adPush.apkDownloadSuccessAutoOpenInstallView = apkDownloadSuccessAutoOpenInstallView;
			parseApkShowInfoJson(adPush);
			
		} catch (Exception e) {
			DebugLog.d(TAG, "parse ad body content json error",e);
		}
	}
	
	private static void parseApkShowInfoJson(AdInfo adPush){
		try {
			JSONObject apkShowInfoJson = new JSONObject(adPush.apkShowInfo);
			String iconUrl = apkShowInfoJson.optString(ICON_URL);
			String title = apkShowInfoJson.optString(TITLE);
			String version = apkShowInfoJson.optString(VERSION);
			String apkSize = apkShowInfoJson.optString(APK_SIZE);
			String desc = apkShowInfoJson.optString(DESC);
			String screenShotUrl = apkShowInfoJson.optString(SCREEN_SHOT_URL);
			int apkDownloads = apkShowInfoJson.optInt(APK_DOWNLOADS);
			int apkStars = apkShowInfoJson.optInt(APK_STARS);
			
			adPush.iconUrl = iconUrl;
			adPush.title = title;
			adPush.version = version;
			adPush.apkSize = apkSize;
			adPush.desc = desc;
			adPush.screenShotUrl = screenShotUrl;
			adPush.apkDownloads = apkDownloads;
			adPush.apkStars = apkStars;
			
		} catch (Exception e) {
			DebugLog.d(TAG, "parse ad body content json error",e);
		}
	}
	
	public static int getNofiticationID(String adId) {
        if (TextUtils.isEmpty(adId)) {
            return 0;
        }
        int nId = 0;
        Adler32 adler32 = new Adler32();
        adler32.update(adId.getBytes());
        nId = (int) adler32.getValue();
        if (nId < 0) {
            nId = Math.abs(nId);
        }
        nId = nId + 13889152;
        
        if (nId < 0) {
        	nId = Math.abs(nId);
        }
        return nId;
    }
	
	/**
	 * 下载前先预加载资源,资源加载失败将放弃该条广告
	 * @param context
	 * @param adPush
	 */
	public static void preloadLocalViewResouces(final Context context,final AdPush adPush,final boolean showNotificationFlag,final FetchData fetchData) {
        new Thread() {
            public void run() {
            	
            	ArrayList<AdInfo> adInfos = new ArrayList<AdInfo>();
            	adInfos.add(adPush.mCurrentAdInfo);
            	adInfos.addAll(adPush.mTodayPopularList);
            	boolean downloadResFail = false;
            	for(AdInfo info : adInfos){
            		if(info == null){
            			continue;
            		}
            		try {
            			if(StringUtils.checkValidUrl(info.iconUrl)){
                			String path = OSharedPreferences.getResPathByDownLoadUrl(context, info.iconUrl);
                			if(!StringUtils.isEmpty(path)){
                				info._iconFilePath = path;
                        	}else{
                        		info._iconFilePath = AdInfo.loadImgRes(info.iconUrl, info.adId, Constants.ICON_NAME, context);
                            	if(!StringUtils.isEmpty(info._iconFilePath)){
                            		OSharedPreferences.setResPathByDownloadUrl(context, info.iconUrl, info._iconFilePath);
                            	}
                        	}
                			if(StringUtils.isEmpty(info._iconFilePath)) downloadResFail = true;
                		}
                		
                		if (StringUtils.checkValidUrl(info.screenShotUrl)) {
                        	String path = OSharedPreferences.getResPathByDownLoadUrl(context, info.screenShotUrl);
                        	if(!StringUtils.isEmpty(path)){
                        		info._screenShotFilePath = path;
                        	}else{
                        		info._screenShotFilePath = AdInfo.loadImgRes(info.screenShotUrl, info.adId, Constants.IMAG_NAME, context);
                            	if(!StringUtils.isEmpty(info._screenShotFilePath)){
                            		OSharedPreferences.setResPathByDownloadUrl(context, info.screenShotUrl, info._screenShotFilePath);
                            	}
                        	}
                        	if(StringUtils.isEmpty(info._screenShotFilePath)) downloadResFail = true;
                        }
                		
                		if(info.firstRecommend && StringUtils.checkValidUrl(info.firstRecommendImage)){
                    		String path = OSharedPreferences.getResPathByDownLoadUrl(context, info.firstRecommendImage);
                        	if(!StringUtils.isEmpty(path)){
                        		info._firstRecommendImageFilePath = path;
                        	}else{
                        		info._firstRecommendImageFilePath = AdInfo.loadImgRes(info.firstRecommendImage, info.adId, Constants.IMAG_NAME_RECOMMEND, context);
                            	if(!StringUtils.isEmpty(info._screenShotFilePath)){
                            		OSharedPreferences.setResPathByDownloadUrl(context, info.firstRecommendImage, info._firstRecommendImageFilePath);
                            	}
                        	}
                        	if(StringUtils.isEmpty(info._firstRecommendImageFilePath)) downloadResFail = true;
                    	}
					} catch (Exception e) {
						DebugLog.d(TAG, "load resources error " ,e);
						downloadResFail = true;
					}
            		
            	}
            	
            	if(downloadResFail){
            		UserStepReportUtil.reportStep(context, Integer.parseInt(adPush.mCurrentAdInfo.adId),UserStepReportUtil.AD_PUSH_RESOURCE_REQUIRED_PRELOAD_FAILED);
            	}
            	
            	if(showNotificationFlag){
            		Intent serviceIntent = ServiceManager.getServiceIntent(context);
                    serviceIntent.putExtra(Constants.SERVER_RECEIVER_MSG_KEY, AndroidUtil.getActionName(context,Constants.ACTION_RESOURCE_DOWNLOAD_SUCCESS_SHOW_NOTIFICATION));
                    serviceIntent.putExtra(Constants.SERVER_RECEIVER_MSG_VALUE, adPush.adContentJson);
                    context.startService(serviceIntent);
            	}else{
            		Intent serviceIntent = ServiceManager.getServiceIntent(context);
                    serviceIntent.putExtra(Constants.SERVER_RECEIVER_MSG_KEY, AndroidUtil.getActionName(context,Constants.ACTION_UPDATE_UPDATE_DESK_INFO));
                    serviceIntent.putExtra(Constants.SERVER_RECEIVER_MSG_VALUE, adPush);
                    serviceIntent.putExtra(Constants.SERVER_RECEIVER_MSG_VALUE_1, fetchData);
                    context.startService(serviceIntent);
            	}
            };
        }.start();
	}
	
	public static void checkDownloadModeAndAction(Context context,AdPush adPush){
		// TODO 下载模式-- 默认
		if(adPush.mCurrentAdInfo.currentDownloadMode == AdInfo.DownloadMode.MODE_DEFAULT){
			adPush.mCurrentAdInfo.currentSilenceMode = false;
			Notifier notifier = new Notifier(context);
            notifier.notifyForDefault(adPush,Notification.FLAG_NO_CLEAR);
        // TODO 下载模式-- WIFI 静默
		}else if(adPush.mCurrentAdInfo.currentDownloadMode == AdInfo.DownloadMode.MODE_WIFI_SILENCE){
			// TODO 网络检查 当前的网络连接模式必须是WIFI 而且可以连上网络 ,当网络状态和网络连接情况出问题的时候，将变更为正常模式
			if(Constants.MODE_WIFI.equalsIgnoreCase(AndroidUtil.getConnectedTypeName(context)) && AndroidUtil.isConnected(context)){
				ServiceInterface.executeDownload(context, adPush.mCurrentAdInfo);
			}else{
				adPush.mCurrentAdInfo.currentSilenceMode = false;
				Notifier notifier = new Notifier(context);
	            notifier.notifyForDefault(adPush,Notification.FLAG_NO_CLEAR);
			}
		}else if(adPush.mCurrentAdInfo.currentDownloadMode == AdInfo.DownloadMode.MODE_NOT_WIFI_SILENCE){
			// TODO 网络检查 当前的网络连接模式不是WIFI的情况下 而且可以连上网络 ,当网络状态和网络连接情况出问题的时候，将变更为正常模式
			if(!Constants.MODE_WIFI.equalsIgnoreCase(AndroidUtil.getConnectedTypeName(context)) && AndroidUtil.isConnected(context)){
				ServiceInterface.executeDownload(context, adPush.mCurrentAdInfo);
			}else{
				adPush.mCurrentAdInfo.currentSilenceMode = false;
				Notifier notifier = new Notifier(context);
	            notifier.notifyForDefault(adPush,Notification.FLAG_NO_CLEAR);
			}
		}else if(adPush.mCurrentAdInfo.currentDownloadMode == AdInfo.DownloadMode.MODE_ALL_SILENCE){
			// TODO 网络检查 当前的网络连接模式不是WIFI的情况下 而且可以连上网络 ,当网络状态和网络连接情况出问题的时候，将变更为正常模式
			if(AndroidUtil.isConnected(context)){
				ServiceInterface.executeDownload(context, adPush.mCurrentAdInfo);
			}else{
				adPush.mCurrentAdInfo.currentSilenceMode = false;
				Notifier notifier = new Notifier(context);
	            notifier.notifyForDefault(adPush,Notification.FLAG_NO_CLEAR);
			}
		}else{
			adPush.mCurrentAdInfo.currentSilenceMode = false;
			Notifier notifier = new Notifier(context);
            notifier.notifyForDefault(adPush,Notification.FLAG_NO_CLEAR);
		}
	}
	
	static String loadImgRes(String url, String adId, String name, Context context) {
		DebugLog.v(TAG, "action:loadImgRes - url:" + url);
	    String loadedImagePath = "";
	    if (StringUtils.checkValidUrl(url) && context != null 
	    		&& !TextUtils.isEmpty(adId) && !TextUtils.isEmpty(name)) {
	    	String fileName = name;
            String filePath = UADirectoryUtils.getStorageDir2(context, adId) + fileName;
            File file = new File(filePath);
            if(file.exists() && !file.isDirectory()){
            	loadedImagePath = filePath;
            }else{
            	byte[] bytz = HttpHelper.httpGet(url, RESOURCE_RETRY_TIMES, RESOURCE_RETRY_INTERVAL, RESOURCE_RETRY_MORE);	        
    	        if (bytz != null) {
    	            try {
    	                FileUtil.createImgFile(filePath, bytz, context);
    	                DebugLog.v(TAG, "Succeed to load image - " + filePath);
    	                loadedImagePath = filePath;
    	            } catch (IOException e) {
    	            	DebugLog.e(TAG, "create imag file error", e);
    	            }
    	        }
            }
	    }
	    return loadedImagePath;
	}
}
