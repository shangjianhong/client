package com.android.cc.info.protocol.req;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.json.JSONObject;

import com.android.cc.info.protocol.Command;

public class AdBannerRequestCommand extends RequestCommand {
	private final Map<String,Object> mData;
	
	public AdBannerRequestCommand(Map<String,Object> data) {
		super(Command.CMD_AD_BANNER);
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
