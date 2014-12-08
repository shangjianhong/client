package cn.ccagame.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ccagame.cache.Cache;
import cn.ccagame.cache.model.AdInfo;
import cn.ccagame.cache.model.AppList;
import cn.ccagame.cache.model.RegisterUser;
import cn.ccagame.cache.model.UserStep;
import cn.ccagame.database.DBAccessor;
import cn.ccagame.database.JdbcHelper;
import cn.ccagame.database.mapper.DBResultSetProcessor;
import cn.ccagame.tool.ErrorCode;
import cn.ccagame.util.LogDataFactory;
import cn.ccagame.util.LogDataWriter;

import com.ccagame.protocal.Protocalable;
import com.ccagame.protocal.model.RequestData;
import com.ccagame.protocal.model.ResponseData;

public class UserStepProcess implements Protocalable {
	private static Logger logger = LoggerFactory.getLogger(UserStepProcess.class);

	public ResponseData procssRequest(RequestData reqd) throws Exception {
		int responseCode = 0;
		ResponseData rdata = new ResponseData();
		int cmd = reqd.getCommand();

		logger.debug("call sendComment map=" + reqd.getBody());
		Map<String, Object> map = reqd.getBody();
		Map<String, Object> resMap = new HashMap<String, Object>();

		try {
			JSONObject dataSet = (JSONObject) JSONValue.parse(map.get("jsonStr").toString());
			String registerId = (String)dataSet.get(RegisterUser.KEY_REGISTER_ID);
			String password = (String)dataSet.get(RegisterUser.KEY_PASSWORD);
			long adId = (Long)dataSet.get(AdInfo.KEY_AD_ID);
			long step = (Long)dataSet.get(UserStep.KEY_USER_STEP);
			long reportDate = (Long)dataSet.get(UserStep.KEY_REPORT_DATE);
			String appId = (String)dataSet.get(AppList.KEY_APP_ID);
			
			map = new HashMap<String, Object>();
			if(registerId!=null&&!"".equals(registerId)&&password!=null&&!"".equals(password)){	
				LogDataWriter logDataWriter = LogDataFactory.getLogDataWriter();
				   
				logDataWriter.writeUserStepData(appId,registerId, (int)adId, (int)step, reportDate);
			   
			    if(step==UserStep.AD_PUSH_EXIST){
			    	RegisterUser registerUser = Cache.getInstance().validRegisterIdCache(registerId, password);
				    Cache.getInstance().updateFailAdPushRecord(registerUser, (int)adId);
			    }
			}else{
				responseCode = ErrorCode.CODE_INVALID_REGISTERID;
				resMap.put("errorReason", "registerId is invalid");
			}	
		} catch (Exception e) {
			e.printStackTrace();
			responseCode = ErrorCode.CODE_INVALID_REGISTERID;
			resMap.put("errorReason", "registerId is invalid");
		}
		logger.debug("responseCode Data:" + responseCode);
		rdata.setData(resMap);
		rdata.setResponseCode(responseCode);
		return rdata;
	}
}