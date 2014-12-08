package com.android.cc.info;

import com.android.cc.info.service.AlarmReceiver;
import com.android.cc.info.service.ServiceManager;

import android.content.Context;

public class SDKManager {
	
	/**
	 * SDK初始化接口
	 * @param context
	 */
	public static void init(Context context){
		// 发起接收服务
		ServiceManager serviceManager = new ServiceManager(context);
		serviceManager.startService();
		
		// 开启心跳闹钟
		AlarmReceiver.startRtc(context);
	}
}
