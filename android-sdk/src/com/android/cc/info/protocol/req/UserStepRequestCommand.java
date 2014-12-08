package com.android.cc.info.protocol.req;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.json.JSONObject;

import com.android.cc.info.protocol.Command;
import com.android.cc.info.util.DebugLog;


public class UserStepRequestCommand extends RequestCommand {
	private final Map<String,Object> mData;

	
	public UserStepRequestCommand(Map<String,Object> data) {
		super(Command.CMD_USER_STEP);
		this.mData = data;
	}
	
	@Override
	protected void buildBody(ByteArrayOutputStream baos) throws IOException {
		
		if(mData!=null&&mData.size()>0){
			JSONObject jsonData = new JSONObject(mData);
			writeTLV2(baos, jsonData.toString());
		}
	}
	
	public String getStringData()
	{
		String tmpData = "";
 		if(mData != null)
		{
			JSONObject jsonObject = new JSONObject(mData);
			tmpData = jsonObject.toString();
			DebugLog.v("UserStepRequestCommand", tmpData);
		}
 		return tmpData;
	}
}
