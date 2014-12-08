package com.android.cc.info.protocol.resp;

import com.android.cc.info.protocol.Command;
import com.android.cc.info.protocol.CustomException;
import com.android.cc.info.util.DebugLog;


public class ResponseProcesser {
	private static final String TAG = "ResponseProcesser";
	private static CommandCallBack mCommandCallBack;
	public static void process(byte[] data){
		ResponseCommand responseCommand = null;
		try {
			int command = Command.getCommand(data);
			DebugLog.d(TAG, "response command :"+command);
			switch(command){
				case Command.CMD_REGISTER:
					responseCommand = new RegisterResponseCommand(data);
					break;
				case Command.CMD_AD_PSUH:
					responseCommand = new AdPushResponseCommand(data);
					break;
				case Command.CMD_USER_STEP:
					responseCommand = new UserStepResponseCommand(data);
					break;
				case Command.CMD_BATCH_USER_STEP:
					responseCommand = new BatchUserStepResponseCommand(data);
					break;
				case Command.CMD_FETCH_DATA:
					responseCommand  = new FetchDataResponseCommand(data);
					break;
				default:
					break;
			}
		} catch (CustomException e) {
			DebugLog.d(TAG, "parse command error",e);
		}
		if(mCommandCallBack!=null){
			mCommandCallBack.onCallBack(responseCommand);
		}
	}
	
	public static void setCallBack(CommandCallBack callBack){
		mCommandCallBack = callBack;
	}
}
