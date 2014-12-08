package com.android.cc.info.protocol.req;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.json.JSONObject;

import com.android.cc.info.protocol.Command;

public class RegisterRequestCommand extends RequestCommand {
	private final Map<String,Object> mData;
	
	public RegisterRequestCommand(Map<String,Object> data) {
		super(Command.CMD_REGISTER);
		this.mData = data;
	}
	
	@Override
	protected void buildBody(ByteArrayOutputStream baos) throws IOException {
		
		if(mData!=null&&mData.size()>0){
			JSONObject jsonData = new JSONObject(mData);
			writeTLV2(baos, jsonData.toString());
		}
	}
}
