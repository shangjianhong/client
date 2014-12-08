package cn.ccagame.process;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ccagame.cache.Cache;
import cn.ccagame.cache.model.AppList;
import cn.ccagame.cache.model.RegisterUser;
import cn.ccagame.database.DBAccessor;
import cn.ccagame.database.JdbcHelper;
import cn.ccagame.database.mapper.DBResultSetProcessor;

import com.ccagame.protocal.Protocalable;
import com.ccagame.protocal.model.RequestData;
import com.ccagame.protocal.model.ResponseData;



public class RegisterProcess implements Protocalable {
	private static Logger logger = LoggerFactory.getLogger(RegisterProcess.class);
	private static final String USER_TABLE_NAME = "user_info";

	public ResponseData procssRequest(RequestData reqd) throws Exception {
		int responseCode = 0;
		ResponseData rdata = new ResponseData();
		int cmd = reqd.getCommand();

		logger.debug("call sendComment map=" + reqd.getBody());
		Map<String, Object> map = reqd.getBody();
		Map<String, Object> resMap = new HashMap<String, Object>();

		try {
			DBAccessor dba = JdbcHelper.getDBAccessor();
			JSONObject dataSet = (JSONObject) JSONValue.parse(map.get("jsonStr").toString());
			String registerKey = (String)dataSet.get(RegisterUser.KEY_REGISTER_KEY);
			String packageName = (String)dataSet.get(RegisterUser.KEY_PACKAGE_NAME);
			String deviceInfo = (String)dataSet.get(RegisterUser.KEY_DEVICE_INFO);
			String appId = (String)dataSet.get(AppList.KEY_APP_ID);
			String registerId = null;
			String password = null;
			long startTime = System.currentTimeMillis();
			if(isExistDB(registerKey, appId)){
				RegisterUser exitRegisterUser = getRegisterUserDB(registerKey, appId);
				
				registerId = exitRegisterUser.getRegisterId();
				password = exitRegisterUser.getPassword();
				
				Cache.getInstance().addRegisterUserCache(exitRegisterUser);
			}else{
				registerId = UUID.randomUUID().toString();
				password = genRandomPwd(8);
				long createDate = System.currentTimeMillis();
				int id = dba.insert("insert t_register_user(app_id,register_key,package_name,register_id,password,device_info,create_date) values(?,?,?,?,?,?,?)",
						new Object[]{appId,registerKey,packageName,registerId,password,deviceInfo,createDate});
				RegisterUser newRegisterUser = new RegisterUser();
				newRegisterUser.setAppId(appId);
				newRegisterUser.setRegisterKey(registerKey);
				newRegisterUser.setPackageName(packageName);
				newRegisterUser.setRegisterId(registerId);
				newRegisterUser.setPassword(password);
				newRegisterUser.setDeviceInfo(deviceInfo);
				newRegisterUser.setCreateDate(createDate);
				
				Cache.getInstance().addRegisterUserCache(newRegisterUser);
			}
			logger.info("end dealTime:"+(System.currentTimeMillis()-startTime));
			map = new HashMap<String, Object>();
			map.put(RegisterUser.KEY_REGISTER_ID, registerId);
			map.put(RegisterUser.KEY_PASSWORD, password);
			resMap.put("jsonStr", JSONObject.toJSONString(map));
		} catch (Exception e) {
			e.printStackTrace();
			responseCode = 1;
		}
		logger.debug("responseCode Data:" + responseCode);
		rdata.setData(resMap);
		rdata.setResponseCode(responseCode);
		return rdata;
	}
	
	 public String genRandomPwd(int pwd_len){
		  //35是因为数组是从0开始的，26个字母+10个数字
		  final int  maxNum = 36;
		  int i;  //生成的随机数
		  int count = 0; //生成的密码的长度
		  char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
		    'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
		    'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		  
		  StringBuffer pwd = new StringBuffer("");
		  Random r = new Random();
		  while(count < pwd_len){
		   //生成随机数，取绝对值，防止生成负数，
		   
		   i = Math.abs(r.nextInt(maxNum));  //生成的数最大为36-1
		   
		   if (i >= 0 && i < str.length) {
		    pwd.append(str[i]);
		    count ++;
		   }
		  }
		  
		  return pwd.toString();
	}
	 
	private boolean isExistDB(String registerKey,String appId) {
		StringBuffer sql = new StringBuffer();
		sql.append("select count(1) from t_register_user where register_key =? and app_id = ?");
		DBAccessor dba = JdbcHelper.getDBAccessor();
		return dba.getRowCount(sql.toString(), new Object[] { registerKey,appId }) > 0;

	}
	
	private RegisterUser getRegisterUserDB(String registerKey,String appId){
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT app_id,register_key,package_name,register_id,password,device_info,create_date FROM t_register_user where register_key = ? and app_id = ?");
		DBAccessor dba = JdbcHelper.getDBAccessor();
		List<RegisterUser> registerUserList = dba.select(sql.toString(), new Object[] { registerKey, appId}, new DBResultSetProcessor<RegisterUser>() {

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
		return registerUserList.get(0);
	}
}