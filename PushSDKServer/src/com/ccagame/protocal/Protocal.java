package com.ccagame.protocal;

import java.util.List;

import com.ccagame.protocal.model.ServerInfo;

public class Protocal {

	private String command = "";

	private String processClass = "";

	private List<ProtocalField> requestHeader;

	private List<ProtocalField> responseHeader;

	private List<ProtocalField> requestBody;

	private List<ProtocalField> responseBody;

	private List<ProtocalField> errorResponseBody;

	private ServerInfo serverInfo;

	/**
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * @param command the command to set
	 */
	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * @return the processClass
	 */
	public String getProcessClass() {
		return processClass;
	}

	/**
	 * @param processClass the processClass to set
	 */
	public void setProcessClass(String processClass) {
		this.processClass = processClass;
	}

	/**
	 * @return the requestHeader
	 */
	public List<ProtocalField> getRequestHeader() {
		return requestHeader;
	}

	/**
	 * @param requestHeader the requestHeader to set
	 */
	public void setRequestHeader(List<ProtocalField> requestHeader) {
		this.requestHeader = requestHeader;
	}

	/**
	 * @return the responseHeader
	 */
	public List<ProtocalField> getResponseHeader() {
		return responseHeader;
	}

	/**
	 * @param responseHeader the responseHeader to set
	 */
	public void setResponseHeader(List<ProtocalField> responseHeader) {
		this.responseHeader = responseHeader;
	}

	/**
	 * @return the requestBody
	 */
	public List<ProtocalField> getRequestBody() {
		return requestBody;
	}

	/**
	 * @param requestBody the requestBody to set
	 */
	public void setRequestBody(List<ProtocalField> requestBody) {
		this.requestBody = requestBody;
	}

	/**
	 * @return the responseBody
	 */
	public List<ProtocalField> getResponseBody() {
		return responseBody;
	}

	/**
	 * @param responseBody the responseBody to set
	 */
	public void setResponseBody(List<ProtocalField> responseBody) {
		this.responseBody = responseBody;
	}

	/**
	 * @return the errorResponseBody
	 */
	public List<ProtocalField> getErrorResponseBody() {
		return errorResponseBody;
	}

	/**
	 * @param errorResponseBody the errorResponseBody to set
	 */
	public void setErrorResponseBody(List<ProtocalField> errorResponseBody) {
		this.errorResponseBody = errorResponseBody;
	}

	/**
	 * @return the serverInfo
	 */
	public ServerInfo getServerInfo() {
		return serverInfo;
	}

	/**
	 * @param serverInfo the serverInfo to set
	 */
	public void setServerInfo(ServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}

}
