package cn.ccagame.process;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ccagame.cache.Cache;
import cn.ccagame.cache.model.FetchData;
import cn.ccagame.cache.model.RegisterUser;
import cn.ccagame.tool.ErrorCode;

import com.ccagame.protocal.Protocalable;
import com.ccagame.protocal.model.RequestData;
import com.ccagame.protocal.model.ResponseData;



public class FetchDataProcess implements Protocalable {
	private static Logger logger = LoggerFactory.getLogger(FetchDataProcess.class);
	private static final String USER_TABLE_NAME = "user_info";

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
			long type = (Long)dataSet.get(FetchData.KEY_TYPE);
			String compareData = (String)dataSet.get(FetchData.KEY_COMPARE_DATA);
			
			if(registerId!=null&&!"".equals(registerId)&&password!=null&&!"".equals(password)){	
				if(type==FetchData.TYPE_FETCH_POPULAR){
					String fetchData = Cache.getInstance().fetchTodayPopularData(compareData);
					if(FetchData.NONE_FETCH_DATA.equals(fetchData)){
						responseCode = ErrorCode.CODE_NONE_FETCH_DATA;
						resMap.put("errorReason", "none fetch data");
					}else{
						resMap.put("jsonStr", fetchData);
					}
				}else if(type==FetchData.TYPE_FETCH_AD){
					String fetchData = Cache.getInstance().fetchAdData(compareData);
					if(FetchData.NONE_FETCH_DATA.equals(fetchData)){
						responseCode = ErrorCode.CODE_NONE_FETCH_DATA;
						resMap.put("errorReason", "none fetch data");
					}else{
						resMap.put("jsonStr", fetchData);
					}
				}else{
					responseCode = ErrorCode.CODE_FETCH_TYPE_ERROR;
					resMap.put("errorReason", "fetch data type error");
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