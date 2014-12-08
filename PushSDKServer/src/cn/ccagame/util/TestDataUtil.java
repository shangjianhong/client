package cn.ccagame.util;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ccagame.cache.Cache;
import cn.ccagame.cache.model.AdInfo;
import cn.ccagame.cache.model.AdPopular;




public class TestDataUtil
{
   private static Logger logger = LoggerFactory.getLogger(TestDataUtil.class);
   private static final String TEST_APP_ID = "1000000001";
   
   public static boolean isTest(String appId){
	   return TEST_APP_ID.equals(appId);
   }
   
   public static String getTestData(){
	   Map<String,Object> map = new HashMap<String, Object>();
	   map.put(AdInfo.KEY_CURRENT_AD_INFO, Cache.getInstance().getAdTestInfo().buildAdJson());
	   map.put(AdPopular.KEY_TODAY_AD_POPULAR, Cache.getInstance().getTestTodayAdPopular());
	   return JSONObject.toJSONString(map);
   }
}
