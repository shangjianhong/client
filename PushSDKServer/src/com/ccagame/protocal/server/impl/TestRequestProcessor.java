/**
 * 
 */
package com.ccagame.protocal.server.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ccagame.protocal.Protocalable;
import com.ccagame.protocal.model.RequestData;
import com.ccagame.protocal.model.ResponseData;

/**
 * @author Martin
 *
 */
public class TestRequestProcessor implements Protocalable {

	/* (non-Javadoc)
	 * @see com.kkliaotian.tool.protocal.Protocalable#procssRequest(com.kkliaotian.model.protocal.RequestData)
	 */
	
	public ResponseData procssRequest(RequestData requestData) throws Exception {
		ResponseData rd = new ResponseData();
		rd.setResponseCode(0);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", "Martin Liu");
		List<Map<String, Object>> phone = new ArrayList<Map<String, Object>>();
		Map<String, Object> phoneMap = new HashMap<String, Object>();
		phoneMap.put("homePhone", "home phone");
		phoneMap.put("orgPhone", "org home");
		data.put("phone", phone);
		phone.add(phoneMap);
		data.put("addr", "address 中文");
		rd.setData(data);


		return rd;
	}

}
