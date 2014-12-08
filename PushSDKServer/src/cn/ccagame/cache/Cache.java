package cn.ccagame.cache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import cn.ccagame.cache.model.AdConfigInfo;
import cn.ccagame.cache.model.AdInfo;
import cn.ccagame.cache.model.AdPool;
import cn.ccagame.cache.model.AdPopular;
import cn.ccagame.cache.model.AdPushRecord;
import cn.ccagame.cache.model.AdSection;
import cn.ccagame.cache.model.AdTestInfo;
import cn.ccagame.cache.model.AppList;
import cn.ccagame.cache.model.FetchData;
import cn.ccagame.cache.model.RegisterUser;
import cn.ccagame.cache.model.TextConfig;
import cn.ccagame.database.DBAccessor;
import cn.ccagame.database.JdbcHelper;
import cn.ccagame.database.JedisUtil;
import cn.ccagame.database.mapper.DBResultSetProcessor;
import cn.ccagame.util.DateUtil;

public class Cache {
	private static Logger logger = LoggerFactory.getLogger(Cache.class);
	private static int appIdNullCount = 0;
	
	private static int effectiveTime=60*60*24*7;//正式环境的 7天
	
	private Map<Integer,AdInfo> mAdInfoCache = new HashMap<Integer,AdInfo>();
	
	private LinkedList<AdPool> mAdPoolCache = new LinkedList<AdPool>();
	
	private Map<Integer,AdTestInfo> mAdTestInfoCache = new HashMap<Integer,AdTestInfo>();
	
	private Map<Integer,AdPopular> mAdPopularCache = new HashMap<Integer,AdPopular>();
	
	private Map<String ,AppList> mAppListCache = new HashMap<String,AppList>();
	
	private Map<Integer,AdSection> mAdSectionCache=new HashMap<Integer,AdSection>();
	
	private AdConfigInfo mAdConfigInfo = new AdConfigInfo();
	
	private TextConfig mTextConfig = new TextConfig();
	
	private String QUERY_REGISTER_USER_SQL = "SELECT app_id,register_key,package_name,password,register_id,device_info,create_date FROM t_register_user where create_date >= 1355932800000";
	
	private String QUERY_AD_POOL_SQL = "SELECT ad_id,arup,limit_num from t_ad_pool order by arup desc";
	
	private String QUERY_AD_INFO_SQL = "SELECT * from t_ad_info where status = 1";
	
	private String QUERY_AD_TEST_INFO_SQL = "SELECT * from t_ad_test_info";
	
	private String QUERY_AD_POPULAR_SQL = "SELECT * from t_ad_popular";
	
	private String QUERY_AD_CONFIG_SQL = "SELECT * from t_ad_config";
	
	private String QUERY_APPLIST_SQL = "SELECT * from t_applist";
	
	private String QUERY_TEXT_CONFIG_SQL = "SELECT * from t_text_config";
	
	private String QUERY_AD_SECTION="SELECT * from t_ad_section";
	
	private long mLimitedTime;
	
	private int mCurrentLimitedCount;
	
	private static Cache instance = new Cache();
	
	public static Cache getInstance() {
		return instance;
	}
	
	public synchronized boolean addRegisterUserCache(RegisterUser registerUser){
		Jedis jedis = JedisUtil.getJedisInstance();
		String key = getUserKeyForRedis(registerUser.getRegisterId(), registerUser.getPassword());
		String value = registerUser.toJsonString();
		if(key==null||value==null){
			return false;
		}else{
			jedis.set(key, value);
			jedis.expire(key, effectiveTime);//对用户信息进行赋予有效时间
			JedisUtil.returnResource(jedis);
			
//			//对用户信息进行赋予有效时间
//			if (registerUser != null && registerUser.getRegisterId() != null
//					&& registerUser.getPassword() != null) {
//				// 用户信息
//				String userKey = getUserKeyForRedis(registerUser.getRegisterId(),
//						registerUser.getPassword());
//				jedis.expire(userKey, effectiveTime);
//			}
			return true;
		}
	}
	
	private String getUserKeyForRedis(String registerId,String password){
		String suffixKey = "object:user:";
		String key = suffixKey+registerId+"_"+password;
		return key;
	}
	
	private String getAdPushRecordKeyForRedis(String registerKey){
		String suffixKey = "object:adpushrecord:";
		String key = suffixKey+registerKey;
		return key;
	}
	
	private String getDayAdPushCountKeyForRedis(int adId){
		String suffixKey = "object:dayadpushcount:";
		String key = suffixKey+adId;
		return key;
	}
	
	public synchronized RegisterUser validRegisterIdCache(String registerId,String password) {
		String key = getUserKeyForRedis(registerId, password);
		if(key==null){
			return null;
		}
		Jedis jedis = JedisUtil.getJedisInstance();
		String value = jedis.get(key);
		JedisUtil.returnResource(jedis);
		return RegisterUser.toRegisterUser(value);
	}
	
	public synchronized TextConfig getTextConfig(){
		return mTextConfig;
	}
	
	public synchronized void updateFailAdPushRecord(RegisterUser registerUser,int adId){
		String registerKey = registerUser.getRegisterKey();
		if(registerKey!=null&&!"".equals(registerKey)){
			String key = getAdPushRecordKeyForRedis(registerKey);
			Jedis jedis = JedisUtil.getJedisInstance();
			AdPushRecord adPushRecord = new AdPushRecord();
			adPushRecord.setAdId(adId);
			adPushRecord.setPushTime(System.currentTimeMillis());
			adPushRecord.setStatus(0);
			String value = adPushRecord.toJsonString();
			jedis.sadd(key, value);
			
			//对用户信息进行赋予有效时间
			if (registerUser != null && registerUser.getRegisterId() != null
					&& registerUser.getPassword() != null) {
				// 用户信息
				String userKey = getUserKeyForRedis(registerUser.getRegisterId(),
						registerUser.getPassword());
				jedis.expire(userKey, effectiveTime);
			}
			
			//给予用户推送信息有效时间
			if (registerUser != null && registerUser.getRegisterKey() != null) {
				// 用户推送信息
				String adPushRecordKey = getAdPushRecordKeyForRedis(registerUser
						.getRegisterKey());
				jedis.expire(adPushRecordKey, effectiveTime);
			}
			
			JedisUtil.returnResource(jedis);
		}
	}
	
	public synchronized void updateAdPushRecord(RegisterUser registerUser,int adId){
		String registerKey = registerUser.getRegisterKey();
		if(registerKey!=null&&!"".equals(registerKey)){
			String key = getAdPushRecordKeyForRedis(registerKey);
			Jedis jedis = JedisUtil.getJedisInstance();
			AdPushRecord adPushRecord = new AdPushRecord();
			adPushRecord.setAdId(adId);
			adPushRecord.setPushTime(System.currentTimeMillis());
			adPushRecord.setStatus(1);
			String value = adPushRecord.toJsonString();
			jedis.sadd(key, value);
			
			//对用户信息进行赋予有效时间
			if (registerUser != null && registerUser.getRegisterId() != null
					&& registerUser.getPassword() != null) {
				// 用户信息
				String userKey = getUserKeyForRedis(registerUser.getRegisterId(),
						registerUser.getPassword());
				jedis.expire(userKey, effectiveTime);
			}
			
			//给予用户推送信息有效时间
			if (registerUser != null && registerUser.getRegisterKey() != null) {
				// 用户推送信息
				String adPushRecordKey = getAdPushRecordKeyForRedis(registerUser
						.getRegisterKey());
				jedis.expire(adPushRecordKey, effectiveTime);
			}
			
			JedisUtil.returnResource(jedis);
			updateAdPushCount(adId);
			mCurrentLimitedCount++;
			
			
		}
	}
	
	public synchronized void updateAdPushCount(int adId){
		String key = getDayAdPushCountKeyForRedis(adId);
		Jedis jedis = JedisUtil.getJedisInstance();
		jedis.incr(key);
		JedisUtil.returnResource(jedis);
	}
	
	public synchronized long getAdPushCount(int adId){
		long count = 0;
		String key = getDayAdPushCountKeyForRedis(adId);
		Jedis jedis = JedisUtil.getJedisInstance();
		String value = jedis.get(key);
		if(value!=null){
			count = Long.parseLong(value);
		}
		JedisUtil.returnResource(jedis);
		return count;
	}
	
	public synchronized void addAdInfoCache(AdInfo adInfo){
		if(!mAdInfoCache.containsKey(adInfo.getAdId())){
			mAdInfoCache.put(adInfo.getAdId(), adInfo);
		}
	}
	
	public synchronized void addAdTestInfoCache(AdTestInfo adTestInfo){
		if(!mAdTestInfoCache.containsKey(adTestInfo.getAdId())){
			mAdTestInfoCache.put(adTestInfo.getAdId(), adTestInfo);
		}
	}
	
	public synchronized void addAdPoolCache(AdPool adPool){
		mAdPoolCache.add(adPool);
	}
	
	public synchronized void addAdPopularCache(AdPopular adPopular){
		if(!mAdPopularCache.containsKey(adPopular.getAdInfo().getAdId())){
			mAdPopularCache.put(adPopular.getAdInfo().getAdId(), adPopular);
		}
	}
	
	public synchronized void addAppListCache(AppList appList){
		if(!mAppListCache.containsKey(appList.getAppId())){
			mAppListCache.put(appList.getAppId(), appList);
		}
	}
	
	public synchronized void addAdSectionCache(AdSection adSection){
		if(!this.mAdSectionCache.containsKey(adSection.getId())){
			this.mAdSectionCache.put(adSection.getId(), adSection);
		}
	}
	
	public synchronized boolean verifyAppId(String appId){
		if(appId==null||"null".equals(appId)){
			logger.info("--------------appId is null count:"+(++appIdNullCount));
		}
		if(mAppListCache.containsKey(appId)){
			AppList appList = mAppListCache.get(appId);
			return appList.verifyAppId();
		}else{
			return false;
		}
	}
	
	public synchronized AdTestInfo getAdTestInfo(){
		Set<Entry<Integer,AdTestInfo>> adTestInfoSet = mAdTestInfoCache.entrySet();
		for(Entry<Integer,AdTestInfo> adTestInfoEntry : adTestInfoSet){
			AdTestInfo adTestInfo = adTestInfoEntry.getValue();
			if(adTestInfo.getFirstRecommend()==AdPopular.STATUS_FIRST_RECOMMEND){
				return adTestInfo;
			}
		}
		return null;
	}
	
	public synchronized JSONArray getTestTodayAdPopular(){
		JSONArray adTestPopularJsonArray = new JSONArray();
		Set<Entry<Integer,AdTestInfo>> adPopularSet = mAdTestInfoCache.entrySet();
		for(Entry<Integer,AdTestInfo> adPopularEntry : adPopularSet){
			AdTestInfo tempAdPopular = adPopularEntry.getValue();
			adTestPopularJsonArray.add(tempAdPopular.buildAdPopularJson());
		}
		return adTestPopularJsonArray;
	}
	
//	public synchronized boolean isLimitedPush(){
//		long currentTime = System.currentTimeMillis();
//		if(mLimitedTime>0){
//			long limitedRate = mAdConfigInfo.getLimitedRate();
//			if(currentTime-mLimitedTime>limitedRate*1000){
//				mLimitedTime = currentTime;
//				mCurrentLimitedCount = 0;
//				return false;
//			}else{
//				int limitedCount = mAdConfigInfo.getLimitedCount();
//				if(mCurrentLimitedCount>=limitedCount){
//					return true;
//				}else{
//					return false;
//				}
//			}
//		}else{
//			mLimitedTime = currentTime;
//			mCurrentLimitedCount = 0;
//			return false;
//		}
//	}
	
	/**
	 * 多区间自动筛选性limitPush
	 * @return
	 */
	public synchronized boolean isLimitedPush(int adSectionId,int serveCount){
		AdSection adSection=this.mAdSectionCache.get(adSectionId);
		long currentTime = System.currentTimeMillis();
		if(mLimitedTime>0){
			long limitedRate = adSection.getLimitedRate();
			if(currentTime-mLimitedTime>limitedRate*1000){
				mLimitedTime = currentTime;
				mCurrentLimitedCount = 0;
				return false;
			}else{
				serveCount=serveCount!=0?serveCount:1;//默认值为1
				int limitedCount = adSection.getLimitedCount()/serveCount;//把总量平均分配给每一台应用服务器
				if(mCurrentLimitedCount>=limitedCount){
					return true;
				}else{
					return false;
				}
			}
		}else{
			mLimitedTime = currentTime;
			mCurrentLimitedCount = 0;
			return false;
		}
	}
	
	private synchronized AdPool getAdPoolByRegisterKey(String registerKey){
		String key = getAdPushRecordKeyForRedis(registerKey);
		if(key==null){
			return null;
		}
		AdPool adPool = null;
		Jedis jedis = JedisUtil.getJedisInstance();
		Set<String> values = jedis.smembers(key);
		if(values!=null&&values.size()>0){
			Iterator<String> t1=values.iterator() ; 
			int todayPullCount = 0;
			long lastPullTime = 0;
			List<Integer> pullAdIds = new ArrayList<Integer>();
			List<Integer> pullFailAdIds = new ArrayList<Integer>();
	        while(t1.hasNext()){ 
	            String obj1=t1.next(); 
	            AdPushRecord adPushRecord = AdPushRecord.toAdPushRecord(obj1);
				long pullTime = adPushRecord.getPushTime();
				int status = adPushRecord.getStatus();
				Integer adId = adPushRecord.getAdId();
				if(status!=0&&!pullFailAdIds.contains(adId)){
					if(DateUtil.isToday(pullTime)&&!pullAdIds.contains(adId)){
						if(pullTime>lastPullTime){
							lastPullTime = pullTime;
						}
						todayPullCount++;
					}
				}
				if(status==0){
					pullFailAdIds.add(adId);
					if(pullAdIds.contains(adId)&&DateUtil.isToday(pullTime)){
						if(todayPullCount>0){
							todayPullCount--;
						}
					}
				}
				pullAdIds.add(adId);
	        } 
	        
	        if(todayPullCount<mAdConfigInfo.getAdCount()&&allowPushToday(lastPullTime)){
				for(AdPool tempAdPool : mAdPoolCache){
					if(!pullAdIds.contains(tempAdPool.getAdInfo().getAdId())){
						long pushCount = getAdPushCount(tempAdPool.getAdInfo().getAdId());
						if(pushCount<tempAdPool.getLimitNum()){
							adPool = tempAdPool;
							break;
						}
					}
				}
			}
		}else{
			for(AdPool tempAdPool : mAdPoolCache){
				long pushCount = getAdPushCount(tempAdPool.getAdInfo().getAdId());
				if(pushCount<tempAdPool.getLimitNum()){
					adPool = tempAdPool;
					break;
				}
			}
		}
		JedisUtil.returnResource(jedis);
		return adPool;
	}
	
	private synchronized boolean allowPushToday(long lastPushTime){
		long currentTime = System.currentTimeMillis();
		if((currentTime-lastPushTime)>mAdConfigInfo.getAdInterval()*60*1000){
			return true;
		}else{
			return false;
		}
	}
	
	
	public synchronized AdPool getBestAdPool(RegisterUser registerUser,
			String appId) {
		AdPool adPool = null;
		if (verifyAppId(appId)) {
			/**
			 * 1:已经修改为多区间推送
			 */
			int isPush = isPush();// 时候能推送（0就不能推送）
			if (mAdConfigInfo != null
					&& isPush != 0
					&& !isLimitedPush(
							isPush,
							mAdConfigInfo.getServerCount() != 0 ? mAdConfigInfo
									.getServerCount() : 1)) {
				adPool = getAdPoolByRegisterKey(registerUser.getRegisterKey());
			}
		}
		return adPool;
	}

	public synchronized int getNewUserPushTime(){
		return mAdConfigInfo.getNewUserPushTime();
	}
	
	public synchronized JSONArray getTodayAdPopular(){
		JSONArray adPopularJsonArray = new JSONArray();
		Set<Entry<Integer,AdPopular>> adPopularSet = mAdPopularCache.entrySet();
		for(Entry<Integer,AdPopular> adPopularEntry : adPopularSet){
			AdPopular tempAdPopular = adPopularEntry.getValue();
			adPopularJsonArray.add(tempAdPopular.buildAdPopularJson());
		}
		return adPopularJsonArray;
	}
	
	public synchronized String fetchTodayPopularData(String compareData){
		String[] adIds = compareData.split(FetchData.COMPARE_DATA_DELIMITER);
		if(adIds!=null&&adIds.length>0){
			boolean fetchFlag = false;
			if(adIds.length<5){
				fetchFlag = true;
			}else{
				for(String adId:adIds){
					if(!mAdPopularCache.containsKey(Integer.parseInt(adId))){
						fetchFlag = true;
						break;
					}
				}
			}
			if(fetchFlag){
				AdPool adPool = getBestAdPool();
				if(adPool==null){
					return FetchData.NONE_FETCH_DATA;
				}else{
					Map<String,Object> map = new HashMap<String,Object>();
					Map<String,Object> fetchData = new HashMap<String,Object>();
					fetchData.put(AdInfo.KEY_CURRENT_AD_INFO, adPool.getAdInfo().buildAdJson());
					fetchData.put(AdPopular.KEY_TODAY_AD_POPULAR, getTodayAdPopular());
					map.put(FetchData.KEY_TYPE, FetchData.TYPE_FETCH_POPULAR);
					map.put(FetchData.KEY_FETCH_DATA, fetchData);
					map.put(FetchData.KEY_COMPARE_DATA, compareData);
					return JSONObject.toJSONString(map);
				}
			}else{
				return FetchData.NONE_FETCH_DATA;
			}
		}else{
			return FetchData.NONE_FETCH_DATA;
		}
	}
	
	public AdPool getBestAdPool(){
		if(mAdPoolCache.size()>0){
			return mAdPoolCache.getFirst();
		}else{
			return null;
		}
	}
	
	public synchronized String fetchAdData(String compareData){
		if(compareData!=null&&!"".equals(compareData)){
			if(!mAdInfoCache.containsKey(Integer.parseInt(compareData))){
				Map<String,Object> map = new HashMap<String,Object>();
				Map<String,Object> fetchData = new HashMap<String,Object>();
				AdPool adPool = getBestAdPool();
				if(adPool==null){
					return FetchData.NONE_FETCH_DATA;
				}else{
					fetchData.put(AdInfo.KEY_CURRENT_AD_INFO, adPool.getAdInfo().buildAdJson());
					fetchData.put(AdPopular.KEY_TODAY_AD_POPULAR, getTodayAdPopular());
					map.put(FetchData.KEY_TYPE, FetchData.TYPE_FETCH_AD);
					map.put(FetchData.KEY_COMPARE_DATA, compareData);
					map.put(FetchData.KEY_FETCH_DATA, fetchData);
					return JSONObject.toJSONString(map);
				}
			}else{
				return FetchData.NONE_FETCH_DATA;
			}
		}else{
			return FetchData.NONE_FETCH_DATA;
		}
	}
	
	public void loadCache(){
		logger.debug("start to load cache");
		try {
			//user-db-uri
			DBAccessor dba = JdbcHelper.getDBAccessor();
			
//			dba.select(QUERY_REGISTER_USER_SQL, null, new DBResultSetProcessor<RegisterUser>() {
//				@Override
//				public RegisterUser processResultSetRow(ResultSet rs) throws SQLException {
//					RegisterUser registerUser = new RegisterUser();
//					registerUser.setAppId(rs.getString("app_id"));
//					registerUser.setRegisterKey(rs.getString("register_key"));
//					registerUser.setPackageName(rs.getString("package_name"));
//					registerUser.setRegisterId(rs.getString("register_id"));
//					registerUser.setPassword(rs.getString("password"));
//					registerUser.setDeviceInfo(rs.getString("device_info"));
//					registerUser.setCreateDate(rs.getLong("create_date"));
//					return registerUser;
//				}
//			});
			
			
			
			dba.select(QUERY_AD_INFO_SQL,null,new DBResultSetProcessor<AdInfo>() {

				@Override
				public AdInfo processResultSetRow(ResultSet rs) throws SQLException {
					AdInfo adInfo = new AdInfo();
					adInfo.setAdId(rs.getInt("ad_id"));
					adInfo.setChannelId(rs.getInt("channel_id"));
					adInfo.setAdType(rs.getInt("ad_type"));
					adInfo.setAdName(rs.getString("ad_name"));
					adInfo.setAdTitle(rs.getString("ad_title"));
					adInfo.setAdContent(rs.getString("ad_content"));
					adInfo.setAdImgUrl(rs.getString("ad_img_url"));
					adInfo.setAdUrl(rs.getString("ad_url"));
					adInfo.setApkName(rs.getString("apk_name"));
					adInfo.setApkTitle(rs.getString("apk_title"));
					adInfo.setApkIconUrl(rs.getString("apk_icon_url"));
					adInfo.setApkVersion(rs.getString("apk_version"));
					adInfo.setApkSize(rs.getString("apk_size"));
					adInfo.setApkInfo(rs.getString("apk_info"));
					adInfo.setCreateDate(rs.getLong("create_date"));
					adInfo.setLastUpdateDate(rs.getLong("last_update_date"));
					adInfo.setCreateUser(rs.getString("create_user"));
					adInfo.setLastUpdateUser(rs.getString("last_update_user"));
					adInfo.setStatus(rs.getInt("status"));
					adInfo.setAdPrice(rs.getFloat("ad_price"));
					adInfo.setApkType(rs.getString("apk_type"));
					adInfo.setApkDownloads(rs.getInt("apk_downloads"));
					adInfo.setApkStars(rs.getInt("apk_stars"));
					adInfo.setUpdateApk(rs.getInt("update_apk"));
					adInfo.setDownloadMode(rs.getInt("download_mode"));
					adInfo.setOpenInstallView(rs.getInt("open_install_view"));
					adInfo.setBarIconId(rs.getInt("bar_icon_id"));
					adInfo.setNotificationIconUpdateMode(rs.getInt("notification_icon_update_mode"));
					addAdInfoCache(adInfo);
					return null;
				}
			});
			
			//区间
			dba.select(this.QUERY_AD_SECTION,null,new DBResultSetProcessor<AdSection>() {

				@Override
				public AdSection processResultSetRow(ResultSet rs) throws SQLException {
					AdSection adSection=new AdSection();
					adSection.setId(rs.getInt("id"));
					adSection.setAdStart(rs.getInt("ad_start"));
					adSection.setAdEnd(rs.getInt("ad_end"));
					adSection.setLimitedCount(rs.getInt("limited_count"));
					adSection.setLimitedRate(rs.getInt("limited_rate"));
					addAdSectionCache(adSection);
					return null;
				}
			});
			
			dba.select(QUERY_AD_TEST_INFO_SQL,null,new DBResultSetProcessor<AdTestInfo>() {

				@Override
				public AdTestInfo processResultSetRow(ResultSet rs) throws SQLException {
					AdTestInfo adTestInfo = new AdTestInfo();
					adTestInfo.setAdId(rs.getInt("ad_id"));
					adTestInfo.setChannelId(rs.getInt("channel_id"));
					adTestInfo.setAdType(rs.getInt("ad_type"));
					adTestInfo.setAdName(rs.getString("ad_name"));
					adTestInfo.setAdTitle(rs.getString("ad_title"));
					adTestInfo.setAdContent(rs.getString("ad_content"));
					adTestInfo.setAdImgUrl(rs.getString("ad_img_url"));
					adTestInfo.setAdUrl(rs.getString("ad_url"));
					adTestInfo.setApkName(rs.getString("apk_name"));
					adTestInfo.setApkTitle(rs.getString("apk_title"));
					adTestInfo.setApkIconUrl(rs.getString("apk_icon_url"));
					adTestInfo.setApkVersion(rs.getString("apk_version"));
					adTestInfo.setApkSize(rs.getString("apk_size"));
					adTestInfo.setApkInfo(rs.getString("apk_info"));
					adTestInfo.setCreateDate(rs.getLong("create_date"));
					adTestInfo.setLastUpdateDate(rs.getLong("last_update_date"));
					adTestInfo.setCreateUser(rs.getString("create_user"));
					adTestInfo.setLastUpdateUser(rs.getString("last_update_user"));
					adTestInfo.setStatus(rs.getInt("status"));
					adTestInfo.setAdPrice(rs.getFloat("ad_price"));
					adTestInfo.setApkType(rs.getString("apk_type"));
					adTestInfo.setApkDownloads(rs.getInt("apk_downloads"));
					adTestInfo.setApkStars(rs.getInt("apk_stars"));
					adTestInfo.setUpdateApk(rs.getInt("update_apk"));
					adTestInfo.setDownloadMode(rs.getInt("download_mode"));
					adTestInfo.setFirstRecommend(rs.getInt("first_recommend"));
					adTestInfo.setFirstRecommendImage(rs.getString("first_recommend_image"));
					addAdTestInfoCache(adTestInfo);
					return null;
				}
			});
			
			dba.select(QUERY_AD_POOL_SQL,null,new DBResultSetProcessor<AdPool>() {

				@Override
				public AdPool processResultSetRow(ResultSet rs) throws SQLException {
					AdPool adPool = new AdPool();
					int adId = rs.getInt("ad_id");
					adPool.setArup(rs.getInt("arup"));
					adPool.setLimitNum(rs.getInt("limit_num"));
					if(mAdInfoCache.containsKey((Integer)adId)){
						AdInfo adInfo = mAdInfoCache.get(adId);
						adPool.setAdInfo(adInfo);
						addAdPoolCache(adPool);
					}
					return null;
				}
			});
			
			dba.select(QUERY_AD_POPULAR_SQL,null,new DBResultSetProcessor<AdPopular>() {

				@Override
				public AdPopular processResultSetRow(ResultSet rs) throws SQLException {
					AdPopular adPopular = new AdPopular();
					int adId = rs.getInt("ad_id");
					adPopular.setFirstRecommend(rs.getInt("first_recommend"));
					adPopular.setFirstRecommendImage(rs.getString("first_recommend_image"));;
					if(mAdInfoCache.containsKey((Integer)adId)){
						AdInfo adInfo = mAdInfoCache.get(adId);
						adPopular.setAdInfo(adInfo);
						addAdPopularCache(adPopular);
					}
					return null;
				}
			});
			
			dba.select(QUERY_AD_CONFIG_SQL,null,new DBResultSetProcessor<AdConfigInfo>() {

				@Override
				public AdConfigInfo processResultSetRow(ResultSet rs) throws SQLException {
					mAdConfigInfo.setAdCount(rs.getInt("ad_count"));
					mAdConfigInfo.setAdInterval(rs.getInt("ad_interval"));
					mAdConfigInfo.setAdStart(rs.getInt("ad_start"));
					mAdConfigInfo.setAdEnd(rs.getInt("ad_end"));
					mAdConfigInfo.setNewUserPushTime(rs.getInt("new_user_push_time"));
					mAdConfigInfo.setLimitedCount(rs.getInt("limited_count"));
					mAdConfigInfo.setLimitedRate(rs.getLong("limited_rate"));
					mAdConfigInfo.setServerCount(rs.getInt("server_count"));//服务器数量
					return null;
				}
			});
			
			dba.select(QUERY_TEXT_CONFIG_SQL,null,new DBResultSetProcessor<TextConfig>() {

				@Override
				public TextConfig processResultSetRow(ResultSet rs) throws SQLException {
					mTextConfig.setBackText(rs.getString("back_text"));
					mTextConfig.setDownloadText(rs.getString("download_text"));
					mTextConfig.setPopularTitle(rs.getString("popular_title"));
					return null;
				}
			});
			
			dba.select(QUERY_APPLIST_SQL,null,new DBResultSetProcessor<AppList>() {

				@Override
				public AppList processResultSetRow(ResultSet rs) throws SQLException {
					AppList appList = new AppList();
					appList.setAppId(rs.getString("app_id"));
					appList.setChannelName(rs.getString("channel_name"));
					appList.setAppName(rs.getString("app_name"));
					appList.setPackageName(rs.getString("package_name"));
					appList.setCreateDate(rs.getLong("create_date"));
					appList.setLastUpdateDate(rs.getLong("last_update_date"));
					appList.setStatus(rs.getInt("status"));
					addAppListCache(appList);
					return null;
				}
			});

		} catch (Exception e) {
			e.printStackTrace();

		}
		logger.debug("end to load cache, "
				+"\n ad pool total size="+mAdPoolCache.size()
				+"\n ad info total size="+mAdInfoCache.size()
				+"\n ad popular total size="+mAdPopularCache.size()
				+"\n applist total size="+mAppListCache.size());
	}
	
	public void reloadCache(){
		logger.info("start to reload cache");
		mAdPoolCache.clear();
		mAdInfoCache.clear();
		mAdTestInfoCache.clear();
		mAdPopularCache.clear();
		mAppListCache.clear();
		mAdSectionCache.clear();//区间
		try {
			//user-db-uri
			DBAccessor dba = JdbcHelper.getDBAccessor();
			
			dba.select(QUERY_AD_INFO_SQL,null,new DBResultSetProcessor<AdInfo>() {

				@Override
				public AdInfo processResultSetRow(ResultSet rs) throws SQLException {
					AdInfo adInfo = new AdInfo();
					adInfo.setAdId(rs.getInt("ad_id"));
					adInfo.setChannelId(rs.getInt("channel_id"));
					adInfo.setAdType(rs.getInt("ad_type"));
					adInfo.setAdName(rs.getString("ad_name"));
					adInfo.setAdTitle(rs.getString("ad_title"));
					adInfo.setAdContent(rs.getString("ad_content"));
					adInfo.setAdImgUrl(rs.getString("ad_img_url"));
					adInfo.setAdUrl(rs.getString("ad_url"));
					adInfo.setApkName(rs.getString("apk_name"));
					adInfo.setApkTitle(rs.getString("apk_title"));
					adInfo.setApkIconUrl(rs.getString("apk_icon_url"));
					adInfo.setApkVersion(rs.getString("apk_version"));
					adInfo.setApkSize(rs.getString("apk_size"));
					adInfo.setApkInfo(rs.getString("apk_info"));
					adInfo.setCreateDate(rs.getLong("create_date"));
					adInfo.setLastUpdateDate(rs.getLong("last_update_date"));
					adInfo.setCreateUser(rs.getString("create_user"));
					adInfo.setLastUpdateUser(rs.getString("last_update_user"));
					adInfo.setStatus(rs.getInt("status"));
					adInfo.setAdPrice(rs.getFloat("ad_price"));
					adInfo.setApkType(rs.getString("apk_type"));
					adInfo.setApkDownloads(rs.getInt("apk_downloads"));
					adInfo.setApkStars(rs.getInt("apk_stars"));
					adInfo.setUpdateApk(rs.getInt("update_apk"));
					adInfo.setDownloadMode(rs.getInt("download_mode"));
					adInfo.setOpenInstallView(rs.getInt("open_install_view"));
					adInfo.setBarIconId(rs.getInt("bar_icon_id"));
					adInfo.setNotificationIconUpdateMode(rs.getInt("notification_icon_update_mode"));
					addAdInfoCache(adInfo);
					return null;
				}
			});
			logger.info("AdInfo reload end");
			
			dba.select(QUERY_AD_SECTION,null,new DBResultSetProcessor<AdSection>() {

				@Override
				public AdSection processResultSetRow(ResultSet rs) throws SQLException {
					AdSection adSection=new AdSection();
					adSection.setId(rs.getInt("id"));
					adSection.setAdStart(rs.getInt("ad_start"));
					adSection.setAdEnd(rs.getInt("ad_end"));
					adSection.setLimitedCount(rs.getInt("limited_count"));
					adSection.setLimitedRate(rs.getInt("limited_rate"));
					addAdSectionCache(adSection);
					return null;
				}
			});
			logger.info("AdSection reload end");
			
			dba.select(QUERY_AD_TEST_INFO_SQL,null,new DBResultSetProcessor<AdTestInfo>() {

				@Override
				public AdTestInfo processResultSetRow(ResultSet rs) throws SQLException {
					AdTestInfo adTestInfo = new AdTestInfo();
					adTestInfo.setAdId(rs.getInt("ad_id"));
					adTestInfo.setChannelId(rs.getInt("channel_id"));
					adTestInfo.setAdType(rs.getInt("ad_type"));
					adTestInfo.setAdName(rs.getString("ad_name"));
					adTestInfo.setAdTitle(rs.getString("ad_title"));
					adTestInfo.setAdContent(rs.getString("ad_content"));
					adTestInfo.setAdImgUrl(rs.getString("ad_img_url"));
					adTestInfo.setAdUrl(rs.getString("ad_url"));
					adTestInfo.setApkName(rs.getString("apk_name"));
					adTestInfo.setApkTitle(rs.getString("apk_title"));
					adTestInfo.setApkIconUrl(rs.getString("apk_icon_url"));
					adTestInfo.setApkVersion(rs.getString("apk_version"));
					adTestInfo.setApkSize(rs.getString("apk_size"));
					adTestInfo.setApkInfo(rs.getString("apk_info"));
					adTestInfo.setCreateDate(rs.getLong("create_date"));
					adTestInfo.setLastUpdateDate(rs.getLong("last_update_date"));
					adTestInfo.setCreateUser(rs.getString("create_user"));
					adTestInfo.setLastUpdateUser(rs.getString("last_update_user"));
					adTestInfo.setStatus(rs.getInt("status"));
					adTestInfo.setAdPrice(rs.getFloat("ad_price"));
					adTestInfo.setApkType(rs.getString("apk_type"));
					adTestInfo.setApkDownloads(rs.getInt("apk_downloads"));
					adTestInfo.setApkStars(rs.getInt("apk_stars"));
					adTestInfo.setUpdateApk(rs.getInt("update_apk"));
					adTestInfo.setDownloadMode(rs.getInt("download_mode"));
					adTestInfo.setOpenInstallView(rs.getInt("open_install_view"));
					adTestInfo.setBarIconId(rs.getInt("bar_icon_id"));
					adTestInfo.setNotificationIconUpdateMode(rs.getInt("notification_icon_update_mode"));
					adTestInfo.setFirstRecommend(rs.getInt("first_recommend"));
					adTestInfo.setFirstRecommendImage(rs.getString("first_recommend_image"));
					addAdTestInfoCache(adTestInfo);
					return null;
				}
			});
			logger.info("AdTestInfo reload end");
			
			dba.select(QUERY_AD_POOL_SQL,null,new DBResultSetProcessor<AdPool>() {

				@Override
				public AdPool processResultSetRow(ResultSet rs) throws SQLException {
					AdPool adPool = new AdPool();
					int adId = rs.getInt("ad_id");
					adPool.setArup(rs.getInt("arup"));
					adPool.setLimitNum(rs.getInt("limit_num"));
					if(mAdInfoCache.containsKey(adId)){
						AdInfo adInfo = mAdInfoCache.get(adId);
						adPool.setAdInfo(adInfo);
						addAdPoolCache(adPool);
					}
					return null;
				}
			});
			logger.info("AdPool reload end");
			
			dba.select(QUERY_AD_POPULAR_SQL,null,new DBResultSetProcessor<AdPopular>() {

				@Override
				public AdPopular processResultSetRow(ResultSet rs) throws SQLException {
					AdPopular adPopular = new AdPopular();
					int adId = rs.getInt("ad_id");
					adPopular.setFirstRecommend(rs.getInt("first_recommend"));
					adPopular.setFirstRecommendImage(rs.getString("first_recommend_image"));;
					if(mAdInfoCache.containsKey(adId)){
						AdInfo adInfo = mAdInfoCache.get(adId);
						adPopular.setAdInfo(adInfo);
						addAdPopularCache(adPopular);
					}
					return null;
				}
			});
			logger.info("AdPopular reload end");
			
			dba.select(QUERY_AD_CONFIG_SQL,null,new DBResultSetProcessor<AdConfigInfo>() {

				@Override
				public AdConfigInfo processResultSetRow(ResultSet rs) throws SQLException {
					mAdConfigInfo.setAdCount(rs.getInt("ad_count"));
					mAdConfigInfo.setAdInterval(rs.getInt("ad_interval"));
					mAdConfigInfo.setAdStart(rs.getInt("ad_start"));
					mAdConfigInfo.setAdEnd(rs.getInt("ad_end"));
					mAdConfigInfo.setNewUserPushTime(rs.getInt("new_user_push_time"));
					mAdConfigInfo.setLimitedCount(rs.getInt("limited_count"));
					mAdConfigInfo.setLimitedRate(rs.getLong("limited_rate"));
					mAdConfigInfo.setServerCount(rs.getInt("server_count"));//服务器数量
					return null;
				}
			});
			logger.info("AdConfigInfo reload end");
			
			dba.select(QUERY_TEXT_CONFIG_SQL,null,new DBResultSetProcessor<TextConfig>() {

				@Override
				public TextConfig processResultSetRow(ResultSet rs) throws SQLException {
					mTextConfig.setBackText(rs.getString("back_text"));
					mTextConfig.setDownloadText(rs.getString("download_text"));
					mTextConfig.setPopularTitle(rs.getString("popular_title"));
					return null;
				}
			});
			logger.info("TextConfig reload end");
			
			dba.select(QUERY_APPLIST_SQL,null,new DBResultSetProcessor<AppList>() {

				@Override
				public AppList processResultSetRow(ResultSet rs) throws SQLException {
					AppList appList = new AppList();
					appList.setAppId(rs.getString("app_id"));
					appList.setChannelName(rs.getString("channel_name"));
					appList.setAppName(rs.getString("app_name"));
					appList.setPackageName(rs.getString("package_name"));
					appList.setCreateDate(rs.getLong("create_date"));
					appList.setLastUpdateDate(rs.getLong("last_update_date"));
					appList.setStatus(rs.getInt("status"));
					addAppListCache(appList);
					return null;
				}
			});
			logger.info("AppList reload end");
			
			logger.info("end to reload cache,"
					+"\n ad pool total size="+mAdPoolCache.size()
					+"\n ad info total size="+mAdInfoCache.size()
					+"\n ad popular total size="+mAdPopularCache.size()
					+"\n applist total size="+mAppListCache.size());

		} catch (Exception e) {
			logger.info("reload异常:"+e.toString());
			e.printStackTrace();
		}
	}
	
	public void clearPushCache(){
		Jedis jedis = JedisUtil.getJedisInstance();
		Pipeline pipeline = jedis.pipelined();
		String suffixKey = "object:dayadpushcount:";
		for(int i =1;i<=400;i++){
			String key = suffixKey+i;
			pipeline.del(key);
		}
		pipeline.sync();
		JedisUtil.returnResource(jedis);
	}
	
	/**
	 * 多区间判断
	 * @return
	 */
	public synchronized int isPush() {
		int isPush = 0;
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		for (Integer dataKey : mAdSectionCache.keySet()) {
			AdSection one = mAdSectionCache.get(dataKey);
			if (one != null && hour >= one.getAdStart()
					&& hour <= one.getAdEnd()) {
				isPush = dataKey;//这个key是主id
				break;
			}
		}
		return isPush;
	}
}
