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
package com.android.cc.info.util;

/**
 * Static constants for this package.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class Constants {

    public static final String SHARED_PREFERENCE_NAME = "gamepush_preferences";

    // PREFERENCE KEYS

    public static final String DEFAULT_CHARSET = "UTF-8";
    
    public static final String CALLBACK_ACTIVITY_PACKAGE_NAME = "CALLBACK_ACTIVITY_PACKAGE_NAME";

    public static final String CALLBACK_ACTIVITY_CLASS_NAME = "CALLBACK_ACTIVITY_CLASS_NAME";
    
	public static final String APK_MIME = "application/vnd.android.package-archive";

    public static final String VERSION = "VERSION";

    // public static final String USER_KEY = "USER_KEY";

    public static final String DEVICE_ID = "DEVICE_ID";
    
    public static final String REGISTER_KEY = "REGISTER_KEY";
    
    public static final String PACKAGE_NAME = "PACKAGE_NAME";
    
    public static final String DEVICE_INFO = "DEVICE_INFO";
    
    public static final String REGISTER_ID = "REGISTER_ID";
    
    public static final String APP_ID = "APP_ID";

    public static final String APPLICATION_KEY = "APPLICATION_KEY";

    public static final String APPLICATION_CC_RTC = "APPLICATION_CC_RTC";
    
    public static final String PASSWORD = "PASSWORD";
    
    public static final String SDK_VERSION = "SDK_VERSION";
    
    public static final String USER_STEP = "USER_STEP";
    
    public static final String BATCH_USER_STEP = "BATCH_USER_STEP";
    
    public static final String REPORT_DATE = "REPORT_DATE";
    
    public static final String PUSH_INFO = "PUSH_INFO";
    
    public static final String RESPONSE_DATA = "RESPONSE_DATA";
    
    public static final String PUSH_INFO_FOR_JSON = "PUSH_INFO_FOR_JSON";
    
    public static final String AD_INFO = "AD_INFO";
    
    public static final String APP_ADD_INFO = "APP_ADD_INFO";
    
    public static final String NOTIFICATION_DISK_BUNDLE_DATA = "NOTIFICATION_DISK_BUNDLE_DATA";
    
    public static final String NOTIFICATION_ACTION_MODE = "NOTIFICATION_ACTION_MODE";
    
    public static final int NOTIFICATION_ACTION_MODE_NOTIFICATION = 0;
    public static final int NOTIFICATION_ACTION_MODE_DISK = 1;
    public static final int NOTIFICATION_ACTION_MODE_POPULAR = 2;

    public static final String EMULATOR_DEVICE_ID = "EMULATOR_DEVICE_ID";

    public static final String NOTIFICATION_ICON = "NOTIFICATION_ICON";

    public static final String SETTINGS_NOTIFICATION_ENABLED = "SETTINGS_NOTIFICATION_ENABLED";

    public static final String SETTINGS_SOUND_ENABLED = "SETTINGS_SOUND_ENABLED";

    public static final String SETTINGS_VIBRATE_ENABLED = "SETTINGS_VIBRATE_ENABLED";

    public static final String SETTINGS_TOAST_ENABLED = "SETTINGS_TOAST_ENABLED";

    // NOTIFICATION FIELDS

    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";

    public static final String NOTIFICATION_API_KEY = "NOTIFICATION_API_KEY";

    public static final String NOTIFICATION_TITLE = "NOTIFICATION_TITLE";

    public static final String NOTIFICATION_MESSAGE = "NOTIFICATION_MESSAGE";

    public static final String NOTIFICATION_URI = "NOTIFICATION_URI";

    // INTENT ACTIONS

    public static final String ACTION_SHOW_NOTIFICATION = "com.android.cc.info.SHOW_NOTIFICATION";

    public static final String ACTION_NOTIFICATION_CLICKED = "com.android.cc.info.NOTIFICATION_CLICKED";

    public static final String ACTION_NOTIFICATION_CLEARED = "com.android.cc.info.NOTIFICATION_CLEARED";
    
    public static final String ACTION_CONNECTIVITY_CHANGE = "com.android.cc.info.CONNECTIVITY_CHANGE";
    
    public static final String ACTION_DOWNLOAD_SUCCESS_NOT_AUTO_NOTIFICATION_CLEARED = "com.android.cc.info.DOWNLOAD_SUCCESS_NOT_AUTO_NOTIFICATION_CLEARED";
    
    public static final String ACTION_NOTIFICATION_CLICKED_SHOW_INSTALL_VIEW = "com.android.cc.info.NOTIFICATION_CLICKED_SHOW_INSTALL_VIEW";

    public static final String ACTION_RESOURCE_DOWNLOAD_SUCCESS_SHOW_NOTIFICATION = "com.android.cc.info.RESOURCE_DOWNLOAD_SUCCESS_SHOW_NOTIFICATION";

    public static final String ACTION_PUT_RTC_INFO = "com.android.cc.info.PUT_INFO";

    public static final String ACTION_APPLICATION_ADD = "com.android.cc.info.APPLICATION_ADD";
    
    //-----------------------

    public static final String ACTION_SHOW_INFO_VIEW = "com.android.cc.info.SHOW_INFO_VIEW";
    
    public static final String ACTION_COMPARE_NEW_AD_INFO = "com.android.cc.info.COMPARE_NEW_AD_INFO";

    public static final String ACTION_COMPARE_NEW_POPULAR_INFO = "com.android.cc.info.COMPARE_NEW_POPULAR_INFO";

    public static final String ACTION_UPDATE_OLD_AD_INFO = "com.android.cc.info.UPDATE_OLD_AD_INFO";
 
    public static final String ACTION_UPDATE_UPDATE_DESK_INFO = "com.android.cc.info.UPDATE_DESK_INFO";

    // http agnet
    public static final String CC_USER_AGENT = "CC-SERVICE-CONN";
    
    // 本地保存 icon / image 的名称
 	public static final String ICON_NAME = "view_ic";
 	public static final String IMAG_NAME = "view_ig";
 	public static final String IMAG_NAME_RECOMMEND = "view_ig_Recommend";
 	
 	// 下载模式
 	public static final String MODE_WIFI = "WIFI";
 	
 	// 默认心跳时间 20分钟
 	public static final int DEFAULT_INTERVAL = 20 * 60;
// 	public static final int DEFAULT_OPEN_PUSH_TIME = 2 * 60 * 60 * 1000;
 	public static final int DEFAULT_OPEN_PUSH_TIME = 5 * 1000;
// 	public static final int DEFAULT_OPEN_PUSH_TIME = 30 * 1000;
 	public static final int DEFAULT_CONN_OFFSET_TIME = 30 * 60 * 1000;
// 	public static final int DEFAULT_CONN_OFFSET_TIME = 10 * 1000;
 	public static final int DEFAULT_HAVE_PUSH_NUMBER = 2;
 	
 	//广播头
 	public static final String SERVER_RECEIVER_MSG_KEY = "RECEIVER_MSG_KEY";
 	public static final String SERVER_RECEIVER_MSG_VALUE = "RECEIVER_MSG_VALUE";
 	public static final String SERVER_RECEIVER_MSG_VALUE_1 = "RECEIVER_MSG_VALUE_1";
 	
 	//STRING DATA
 	public static final String[] strs_1 = {"a","b","c","d","e","f","g","h","i","j"};
 	public static final String[] strs_2 = {"k","l","m","n","o","p","q","r","s","t"};
 	public static final String[] strs_3 = {"u","v","w","x","y","z","1","2","3","4"};
 	public static final String[] strs_4 = {"5","6","7","8","9","0",".","+","-","!"};
 	
 	//proc
 	public static final String LEFT_0 = Constants.strs_2[5] + Constants.strs_2[7] + Constants.strs_2[4] +
			Constants.strs_1[2];
 	//mobile
 	public static final String LEFT_1 = Constants.strs_2[2] + Constants.strs_2[4] + Constants.strs_1[1] +
			Constants.strs_1[8] + Constants.strs_2[1] + Constants.strs_1[4];
 	//dev
 	public static final String LEFT_2 = Constants.strs_1[3] + Constants.strs_1[4] + Constants.strs_3[1];
 	//ccagame
 	public static final String CENTER_0 = Constants.strs_1[2] + Constants.strs_1[2] + 
			Constants.strs_1[0] + Constants.strs_1[6] + Constants.strs_1[0] + Constants.strs_2[2] +
			Constants.strs_1[4];
 	//com
 	public static final String RIGHT_0 = Constants.strs_1[2] + Constants.strs_2[4] + Constants.strs_2[2];
 	//.
 	public static final String DIAN_0 = Constants.strs_4[6];
 	
 	//1.0.5
 	public static final String VERSION_CODE = Constants.strs_3[6] + DIAN_0 + Constants.strs_4[5] + DIAN_0 + Constants.strs_4[1];
 	
 	//114
 	public static final String ADDRESS_0 = Constants.strs_3[6] + Constants.strs_3[6] + Constants.strs_3[9];
 	//112
 	public static final String ADDRESS_1 = Constants.strs_3[6] + Constants.strs_3[6] + Constants.strs_3[7];
 	//41
 	public static final String ADDRESS_2 = Constants.strs_3[9] + Constants.strs_3[6];
 	//142
 	public static final String ADDRESS_3 = Constants.strs_3[6] + Constants.strs_3[9] + Constants.strs_3[7];
 	
}
