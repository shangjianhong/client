package com.android.cc.info.protocol.resp;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.cc.info.protocol.CustomException;
import com.android.cc.info.util.Constants;
import com.android.cc.info.util.DebugLog;
import com.android.cc.info.util.ProtocolUtil;



public class RegisterResponseCommand extends ResponseCommand {
	private static final String TAG = "RegisterResponseCommand";
	public Map<String,Object> mMapData;
	
	public RegisterResponseCommand(byte[] data) throws CustomException {
		super(data);
	}
	
	@Override
	protected void parseBody() {
		int startIndex = 0;
		int len = getIntData(this.mBody, startIndex, 2);
		startIndex += 2;
		
		
		String respStr = ProtocolUtil.getStringData(this.mBody, startIndex, len);
		
		DebugLog.d(TAG, "length:"+len+",respStr:"+respStr);
		
		if(respStr!=null&&!"".equals(respStr)){
			try {
				JSONObject respJson= new JSONObject(respStr);
				mMapData = new HashMap<String, Object>();
				String registerId = respJson.optString(Constants.REGISTER_ID);
				String password = respJson.optString(Constants.PASSWORD);
				
				mMapData.put(Constants.REGISTER_ID, registerId);
				mMapData.put(Constants.PASSWORD, password);
				
			} catch (JSONException e) {
				DebugLog.d(TAG, "parse register json error",e);
			}
		}
		
	}
}
