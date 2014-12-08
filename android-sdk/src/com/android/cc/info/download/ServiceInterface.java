package com.android.cc.info.download;

import android.content.Context;
import android.content.Intent;

import com.android.cc.info.data.AdInfo;
import com.android.cc.info.preferences.OSharedPreferences;
import com.android.cc.info.util.Constants;

public class ServiceInterface {

	public static void executeDownload(Context context, AdInfo adInfo) {
		Intent intent = new Intent(context,DownloadService.class);
		intent.putExtra(Constants.AD_INFO, adInfo);
		context.startService(intent);
		saveDownloadInfo(context,adInfo.adContentJson);
	}
	
	private static void saveDownloadInfo(Context context,String info){
		OSharedPreferences.setDownloadInfo(context, info);
	}
	
	public static void cleanDownloadInfo(Context context,String info){
		OSharedPreferences.cleanDownloadInfoByContent(context, info);
	}

}
