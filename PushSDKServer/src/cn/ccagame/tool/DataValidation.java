package cn.ccagame.tool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ccagame.tool.rule.InitRulesImp;


public class DataValidation {
	private static Logger logger = LoggerFactory.getLogger(DataValidation.class);	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void filterDataSet(Map dataSet,Map checkRule){
		String dataType = (String) checkRule.get(InitRulesImp.TAG_DATA_TYPE);
		String ruleName = (String) checkRule.get(InitRulesImp.TAG_RULE_NAME);
		
		Map <String,Object> keys = new HashMap<String,Object>();
		if (dataType.equals(InitRulesImp.DATA_TYPE_SET ) && (dataSet instanceof Map)){
			Object rulesObj = checkRule.get(InitRulesImp.TAG_BEAN_NAME);
			if (rulesObj instanceof List) {
				List rules = (List) rulesObj;
				if (rules != null) {
					for (int i=0;i<rules.size();i++) {
						Map<String,Object> ruleMap = (Map<String,Object>) rules.get(i);
						keys.put((String) ruleMap.get(InitRulesImp.TAG_RULE_NAME), 1);
					}
				}
			}else{
				keys.put((String) ((Map<String,Object>) rulesObj).get(InitRulesImp.TAG_RULE_NAME), 1);
			}
		} keys.put(ruleName, 1);
		
		Iterator iter = dataSet.keySet().iterator();
		while (iter.hasNext()){
			String key = (String) iter.next();
			if (keys.get(key)==null) {
				logger.warn("remove "+key+" from dataSet");
				iter.remove();
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static boolean checkData(Object dataSet,Map<String,Object> checkRule){
		String key = (String) checkRule.get(InitRulesImp.TAG_RULE_NAME);
		String dataType = (String) checkRule.get(InitRulesImp.TAG_DATA_TYPE);
		String noNull = (String) checkRule.get(InitRulesImp.TAG_NO_NULL);

		if (noNull == null){
			logger.error(" Server error parse checkRule TAG_NO_NULL is null");
			return false;
		} else if (key == null){
			logger.error(" Server error parse checkRule TAG_RULE_NAME is null");
			return false;
		} else if (dataSet == null && noNull.equals(InitRulesImp.IS_NULL_CAN)){
			return true;
		} else if (dataSet == null){
			logger.error(" Server error dataSet "+key+" is null");
			return false;
		} else if (dataType == null){
			logger.error(" Server error parse checkRule TAG_DATA_TYPE is null");
			return false;
		} else if(dataType.equals(InitRulesImp.DATA_TYPE_STRING) && !(dataSet instanceof String)){
			logger.error(" Server error "+key+"(should be String) is not String class.");
			return false;						
		}
		else if(dataType.equals(InitRulesImp.DATA_TYPE_LONG) && !(dataSet instanceof Long)){
			logger.error(" Server error "+key+"(should be Long) is not Long class.");
			return false;						
		}
		else if(dataType.equals(InitRulesImp.DATA_TYPE_SET) && !(dataSet instanceof Map)){
			logger.error(" Server error "+key+"(should be Map) is not Map class.");
			return false;						
		}
		else if(dataType.equals(InitRulesImp.DATA_TYPE_LIST) && !(dataSet instanceof List)){
			logger.error(" Server error "+key+"(should be List Array) is not List class.");
			return false;						
		}
		
		if (dataType.equals(InitRulesImp.DATA_TYPE_SET ) && (dataSet instanceof Map)){
			filterDataSet((Map)dataSet,checkRule);
			Object rules = checkRule.get(InitRulesImp.TAG_BEAN_NAME);
			List<Object> ruleList = null;
			Map<String,Object> mp = null;
			if (rules instanceof List) ruleList = (List<Object>) rules;
				else mp = (Map<String,Object>) rules;
			
			if (ruleList != null && ruleList.size()>0) {
				for (int i=0;i<ruleList.size();i++){
					Map<String,Object> ruleMap = (Map<String,Object>) ruleList.get(i);
					boolean checkResult = checkSubRule((Map<String,Object>) dataSet,ruleMap);
					if (!checkResult) return false;
				}
			}
			else return checkSubRule((Map<String,Object>) dataSet,mp);
		}
		return true;
	}
	
	private static boolean checkSubRule(Map<String,Object> dataSet,Map<String,Object> rulemp){
		String defaultValue = null;
		String mpKey = (String) rulemp.get(InitRulesImp.TAG_RULE_NAME);
		//if default value is null, default value will be set 
		try{
			String noNullValue = (String) rulemp.get(InitRulesImp.TAG_NO_NULL);
			if (noNullValue.equals(InitRulesImp.IS_NULL_SYS) && dataSet instanceof Map  
					&& ((Map<String, Object>)dataSet).get(mpKey)==null) {
				defaultValue = (String) rulemp.get(InitRulesImp.TAG_DEFAULT_VALUE);
				if (rulemp.get(InitRulesImp.TAG_DATA_TYPE).equals(InitRulesImp.DATA_TYPE_DOUBLE)){
					((Map<String, Object>)dataSet).put(mpKey, Double.parseDouble(defaultValue));
				}else if (rulemp.get(InitRulesImp.TAG_DATA_TYPE).equals(InitRulesImp.DATA_TYPE_INTEGER) ||
						rulemp.get(InitRulesImp.TAG_DATA_TYPE).equals(InitRulesImp.DATA_TYPE_LONG)){
					if (defaultValue.equals(InitRulesImp.SYSTEM_SEC))
						((Map<String, Object>)dataSet).put(mpKey, System.currentTimeMillis()/1000);
					else ((Map<String, Object>)dataSet).put(mpKey, Long.parseLong(defaultValue));
				}else if (rulemp.get(InitRulesImp.TAG_DATA_TYPE).equals(InitRulesImp.DATA_TYPE_STRING)
						||rulemp.get(InitRulesImp.TAG_DATA_TYPE).equals(InitRulesImp.DATA_TYPE_LIST)){
					((Map<String, Object>)dataSet).put(mpKey, defaultValue);
				}
			}
			else{
				boolean checkResult = checkData(((Map<String,Object>)dataSet).get(mpKey),rulemp);
				if (!checkResult) return false;
			}
			return true;
		}catch (Exception e){
			logger.error("dataset not match rule:");
			logger.error("dataset:"+dataSet.toString());
			logger.error("rule:"+rulemp.toString());
			e.printStackTrace();
			return false;
		}
	}

}