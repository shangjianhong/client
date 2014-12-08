package com.android.cc.info.service;

import com.android.cc.info.util.AndroidUtil;
import com.android.cc.info.util.Constants;
import com.android.cc.info.util.DebugLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MsgReceiver extends BroadcastReceiver {
	private static final String TAG = "AlarmReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(action.equals(Intent.ACTION_PACKAGE_ADDED)){
			final String dataName = intent.getDataString();
			DebugLog.d(TAG, "add app. - app name : " + dataName);
			Intent serverIntent = ServiceManager.getServiceIntent(context);
			serverIntent.putExtra(Constants.SERVER_RECEIVER_MSG_KEY, AndroidUtil.getActionName(context,Constants.ACTION_APPLICATION_ADD));
			serverIntent.putExtra(Constants.SERVER_RECEIVER_MSG_VALUE, intent.getDataString());
			context.startService(serverIntent);
		}
	}
}
