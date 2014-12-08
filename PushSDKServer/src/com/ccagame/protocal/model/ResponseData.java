/**
 * 
 */
package com.ccagame.protocal.model;

import java.util.Map;

/**
 * @author Martin
 *
 */
public class ResponseData {

	private int responseCode;

	private Map<String, Object> data;

	/**
	 * @return the responseCode
	 */
	public Integer getResponseCode() {
		return responseCode;
	}

	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * @return the data
	 */
	public Map<String, Object> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return data.toString();
	}

}
