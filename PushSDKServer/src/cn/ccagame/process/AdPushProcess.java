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
import cn.ccagame.cache.model.AdPool;
import cn.ccagame.cache.model.AdPopular;
import cn.ccagame.cache.model.AppList;
import cn.ccagame.cache.model.RegisterUser;
import cn.ccagame.cache.model.UserStep;
import cn.ccagame.database.DBAccessor;
import cn.ccagame.database.JdbcHelper;
import cn.ccagame.database.mapper.DBResultSetProcessor;
import cn.ccagame.tool.ErrorCode;
import cn.ccagame.util.LogDataFactory;
import cn.ccagame.util.LogDataWriter;
import cn.ccagame.util.TestDataUtil;

import com.ccagame.protocal.Protocalable;
import com.ccagame.protocal.model.RequestData;
import com.ccagame.protocal.model.ResponseData;

public class AdPushProcess implements Protocalable {
	private static Logger logger = LoggerFactory.getLogger(AdPushProcess.class);
	private static final String USER_TABLE_NAME = "user_info";
	private static int count = 0;
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
			String sdkVersion = (String)dataSet.get(AdPool.KEY_SDK_VERSION);
			String appId = (String)dataSet.get(AppList.KEY_APP_ID);
			
			map = new HashMap<String, Object>();
			RegisterUser registerUser ;
			long startTime = System.currentTimeMillis();
			registerUser = Cache.getInstance().validRegisterIdCache(registerId, password);
			if(registerUser==null){
				registerUser = getRegisterUserDB(registerId,password);
				if(registerUser!=null){
					Cache.getInstance().addRegisterUserCache(registerUser);
				}
			}
			
			if(registerUser!=null){
				if(TestDataUtil.isTest(appId)){
					resMap.put("jsonStr", TestDataUtil.getTestData());
				}else{
					if(!registerUser.isPush()){
						responseCode = ErrorCode.CODE_NEW_REGISTER_NOT_AD_PUSH;
						resMap.put("errorReason", "new register user not ad pull");
					}else{
						AdPool adPool = Cache.getInstance().getBestAdPool(registerUser,appId);
						if(adPool==null){
							responseCode = ErrorCode.CODE_NONE_AD_PUSH;
							resMap.put("errorReason", "not ad push");
						}else{
							logger.info("pushSuccessCounts-------------"+(++count));
							map.put(AdInfo.KEY_CURRENT_AD_INFO, adPool.getAdInfo().buildAdJson());
							map.put(AdPopular.KEY_TODAY_AD_POPULAR, Cache.getInstance().getTodayAdPopular());
							resMap.put("jsonStr", JSONObject.toJSONString(map));
							Cache.getInstance().updateAdPushRecord(registerUser, adPool.getAdInfo().getAdId());
							logger.info("updateAdPushRecord dealTime:"+(System.currentTimeMillis()-startTime));
							startTime = System.currentTimeMillis();
							
							LogDataWriter logDataWriter = LogDataFactory.getLogDataWriter();
							   
							logDataWriter.writeUserStepData(appId,registerId, adPool.getAdInfo().getAdId(),
									UserStep.AD_PUSH, System.currentTimeMillis());
							logger.info("end dealTime:"+(System.currentTimeMillis()-startTime));
						}
					}
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
	
	private RegisterUser getRegisterUserDB(String registerId,String password){
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT app_id,register_key,package_name,register_id,password,device_info,create_date FROM t_register_user where register_id = ? and password = ?");
		DBAccessor dba = JdbcHelper.getDBAccessor();
		List<RegisterUser> registerUserList = dba.select(sql.toString(), new Object[] { registerId, password}, new DBResultSetProcessor<RegisterUser>() {

			@Override
			public RegisterUser processResultSetRow(ResultSet rs) throws SQLException {
				RegisterUser registerUser = new RegisterUser();
				registerUser.setAppId(rs.getString("app_id"));
				registerUser.setRegisterKey(rs.getString("register_key"));
				registerUser.setPackageName(rs.getString("package_name"));
				registerUser.setRegisterId(rs.getString("register_id"));
				registerUser.setPassword(rs.getString("password"));
				registerUser.setDeviceInfo(rs.getString("device_info"));
				registerUser.setCreateDate(rs.getLong("create_date"));
				return registerUser;
			}
		});
		if(registerUserList!=null&&registerUserList.size()>0){
			return registerUserList.get(0);
		}else{
			return null;
		}
	}
}