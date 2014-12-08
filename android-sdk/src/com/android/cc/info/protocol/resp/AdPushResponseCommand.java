package com.android.cc.info.protocol.resp;

import java.util.HashMap;
import java.util.Map;

import com.android.cc.info.protocol.CustomException;
import com.android.cc.info.util.Constants;
import com.android.cc.info.util.DebugLog;
import com.android.cc.info.util.ProtocolUtil;


public class AdPushResponseCommand extends ResponseCommand {
	private static final String TAG = "AdPushResponseCommand";
	public Map<String,Object> mMapData;
	
	public AdPushResponseCommand(byte[] data) throws CustomException {
		super(data);
	}
	
	@Override
	protected void parseBody() {
		int startIndex = 0;
		if(mErrorCode==0){
			int len = getIntData(this.mBody, startIndex, 2);
			startIndex += 2;
			
			String respStr = ProtocolUtil.getStringData(this.mBody, startIndex, len);
			
			DebugLog.d(TAG, "length:"+len+",respStr:"+respStr);
			
			if(respStr!=null&&!"".equals(respStr)){
				mMapData = new HashMap<String, Object>();
				
				mMapData.put(Constants.PUSH_INFO, respStr);
			}
		}
	}
}
