package com.android.cc.info.protocol;

import java.util.HashMap;
import java.util.Map;

public class CustomException extends Exception {

	public static final Integer ERROR_CODE_DATA_LENGTH_WRONG = 1;
	public static final Integer ERROR_CODE_BAD_REQUEST_COMMAND = 2;
	public static final Integer ERROR_CODE_UNSUPPOERT_COMMAND = 3;
	public static final Integer ERROR_CODE_INVALID_RESPONSE = 4;
	

	private static Map<Integer, String> messageMap = new HashMap<Integer, String>();

	static {
		messageMap.put(ERROR_CODE_DATA_LENGTH_WRONG, "data length wrong");
		messageMap.put(ERROR_CODE_BAD_REQUEST_COMMAND, "bad request command");
		messageMap.put(ERROR_CODE_UNSUPPOERT_COMMAND, "unsupport response command");
		messageMap.put(ERROR_CODE_INVALID_RESPONSE, "invalid response command");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2206591266286948318L;

	private Integer errorCode;

	private String msg;

	public CustomException(String msg) {
		super(msg);
		this.msg = msg;
	}

	public CustomException(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public CustomException(Integer errorCode, String msg) {
		this.errorCode = errorCode;
		this.msg = msg;
	}
	
	@Override
	public String toString() {
		if (msg != null) {
			return msg;
		} else {
			return getMessage();
		}
	}

	@Override
	public String getMessage() {
		return messageMap.get(errorCode);
	}

	public int getErrorCode() {
		return this.errorCode;
	}
}
