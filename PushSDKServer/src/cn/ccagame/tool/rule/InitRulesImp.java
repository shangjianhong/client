package cn.ccagame.tool.rule;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import cn.ccagame.util.XmlUtils;

@SuppressWarnings("rawtypes")
public class InitRulesImp{
	public static final String TAG_BEAN_NAME     = "rulebean";
	public static final String TAG_RULE_NAME     = "ruleName";
	public static final String TAG_DATA_TYPE     = "dataType";
	public static final String TAG_NO_NULL       = "noNull";
	public static final String TAG_DEFAULT_VALUE = "defaultValue";
	
	public static final String DATA_TYPE_STRING  = "STRING";
	public static final String DATA_TYPE_INTEGER = "INTEGER";
	public static final String DATA_TYPE_LONG    = "LONG";
	public static final String DATA_TYPE_DOUBLE  = "DOUBLE";
	public static final String DATA_TYPE_SET     = "SET";
	public static final String DATA_TYPE_LIST     = "LIST";
	
	public static final String IS_NULL_NO  = "NO_NULL";
	public static final String IS_NULL_CAN = "CAN_NULL";
	public static final String IS_NULL_SYS = "SYS_VALUE";

	public static final String SYSTEM_SEC = "SystemSecond";
	public static final String SYSTEM_MILL = "SystemMillSecond";
	
	
	public static final String RULE_ADD_SERVICE = "addServiceRule";
	public static final String RULE_DEL_SERVICE = "delServiceRule";
	public static final String RULE_QRY_SERVICE = "queryServiceRule";
	public static final String RULE_UPD_SERVICE = "updateServiceRule";
	public static final String RULE_SET_LBS 	= "setLBSRule";
	public static final String RULE_QRY_LBS 	= "queryLBSRule";
	
	
    private static String ruleConfigPath = "/JsonRule.xml";
	private static Map<String,Object> map =null;
    static{
        Class configClass;
		try {
			configClass = Class.forName("cn.ccagame.util.SystemConfig");
	        InputStream is = configClass.getResourceAsStream(ruleConfigPath);    
	        SAXReader reader = new SAXReader();
	    	Document doc = reader.read(is);
	        map = XmlUtils.Dom2Map(doc);  	        
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRule(String ruleName) {
		List ruleList = (List) map.get(TAG_BEAN_NAME);
		Map<String,Object> result = null;
		
		if (ruleList != null && ruleList.size()>0){
			for (int i=0;i<ruleList.size();i++){
				Map<String,Object> ruleBean = (Map) ruleList.get(i);
				String ruleNameIter = (String) (ruleBean).get(TAG_RULE_NAME);
				if (ruleNameIter!= null && ruleNameIter.equals(ruleName)){
					result = ruleBean;
					break;
				}
			}
		}
		return result;
	}
}
