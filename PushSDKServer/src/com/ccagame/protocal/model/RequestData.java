package com.ccagame.protocal.model;

import java.util.HashMap;
import java.util.Map;

public class RequestData {

	private Integer command;

	private Map<String, Object> header;

	private Map<String, Object> body;

	private Map<String, Object> data = new HashMap<String, Object>();

	/**
	 * @return the command
	 */
	public Integer getCommand() {
		return command;
	}

	/**
	 * @param command
	 *            the command to set
	 */
	public void setCommand(Integer command) {
		this.command = command;
	}

	/**
	 * @return the header
	 */
	public Map<String, Object> getHeader() {
		return header;
	}

	/**
	 * @param header
	 *            the header to set
	 */
	public void setHeader(Map<String, Object> header) {
		this.header = header;
		if (header != null) {
			data.putAll(header);
		}
	}

	/**
	 * @return the body
	 */
	public Map<String, Object> getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(Map<String, Object> body) {
		this.body = body;
		if (body != null) {
			data.putAll(body);
		}
	}

	public Map<String, Object> getData() {
		return data;
	}

}
