package com.ccagame.protocal;

import com.ccagame.protocal.model.RequestData;
import com.ccagame.protocal.model.ResponseData;

/**
 * Interface Exportable If the Business class should use for importing data, it
 * should implements this interface.
 * 
 * @author Martin Liu
 * 
 */
public interface Protocalable {

	public static final String DEFAULT_CHARSET = "UTF-8";

	/**
	 * Parse data, and save to DB
	 * 
	 * @param reader
	 * @param tsID
	 * @throws Exception
	 */
	public ResponseData procssRequest(final RequestData requestData) throws Exception;

}
