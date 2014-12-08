package com.android.cc.info.protocol.resp;

import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

import com.android.cc.info.data.AdPush;
import com.android.cc.info.data.FetchData;
import com.android.cc.info.protocol.Command;
import com.android.cc.info.service.ServiceManager;
import com.android.cc.info.util.AndroidUtil;
import com.android.cc.info.util.Constants;
import com.android.cc.info.util.DebugLog;
import com.android.cc.info.util.MessageCode;
import com.android.cc.info.util.UserStepReportUtil;


public class ResponseCommandCallBack implements CommandCallBack{
	private static final String TAG = "ResponseCommandCallBack";
	private Context mContext;
	private Handler mCallBackHandler;
	
	public ResponseCommandCallBack(Context context,Handler handler){
		this.mContext = context;
		this.mCallBackHandler = handler;
	}
	
	public void onCallBack(ResponseCommand responseCommand){
		if(responseCommand==null)
			return;
		int command = responseCommand.mCommand;
		switch(command){
			case Command.CMD_REGISTER:
				processRegister((RegisterResponseCommand)responseCommand);
				break;
			case Command.CMD_AD_PSUH:
				processAdPush((AdPushResponseCommand)responseCommand);
				break;
			case Command.CMD_FETCH_DATA:
				processFetchData((FetchDataResponseCommand)responseCommand);
				break;
		}
	}
	
	private void processRegister(RegisterResponseCommand registerResponseCommand){
		Map<String,Object> respData = registerResponseCommand.mMapData;
		String registerId = (String)respData.get(Constants.REGISTER_ID);
		String password = (String)respData.get(Constants.PASSWORD);
        SharedPreferences sharedPrefs = mContext.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);

        Editor editor = sharedPrefs.edit();
        editor.putString(Constants.REGISTER_ID, registerId);
        editor.putString(Constants.PASSWORD, password);
        editor.commit();
        
		mCallBackHandler.sendMessage(mCallBackHandler.obtainMessage(MessageCode.CALL_BACK_REGISTER, respData));
		
        DebugLog.d(TAG, "registerId:"+registerId+",password:"+password);
	}
	
	private void processAdPush(AdPushResponseCommand adPushResponseCommand){
		if(adPushResponseCommand.mErrorCode==0){
			Map<String,Object> respData = adPushResponseCommand.mMapData;
			String pushInfo = (String)respData.get(Constants.PUSH_INFO);

			AdPush adPush = AdPush.parseAdPushJson(pushInfo);
			
			ServiceManager serviceManager = new ServiceManager(mContext);
			serviceManager.sendReceiver(AndroidUtil.getActionName(mContext,Constants.ACTION_SHOW_NOTIFICATION), pushInfo);
			
			UserStepReportUtil.reportStep(mContext, Integer.parseInt(adPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_RECEIVE);
			
	        DebugLog.d(TAG, "pushInfo:"+pushInfo+",apkSize:"+adPush.mCurrentAdInfo.apkSize+",adId:"+adPush.mCurrentAdInfo.adId);
		}else{
			DebugLog.ve(TAG, "error reason :"+adPushResponseCommand.mErrorReason);
		}
	}
	
	private void processFetchData(FetchDataResponseCommand fetchDataResponseCommand){
		if(fetchDataResponseCommand.mErrorCode==0){
			Map<String,Object> respData = fetchDataResponseCommand.mMapData;
			String responseData = (String)respData.get(Constants.RESPONSE_DATA);
			ServiceManager serviceManager = new ServiceManager(mContext);
			serviceManager.sendReceiver(AndroidUtil.getActionName(mContext,Constants.ACTION_UPDATE_OLD_AD_INFO), responseData);
		}else{
			DebugLog.ve(TAG, "error reason :"+fetchDataResponseCommand.mErrorReason);
		}
	}
}