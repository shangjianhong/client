package com.ccagame.protocal.model;

import java.util.Map;

import com.ccagame.protocal.Protocal;

public class ServerInfo {

	private String serverId;

	private Map<String, String> attributes;

	private Map<String, Protocal> protocalMap;

	/**
	 * @return the attributes
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes
	 *            the attributes to set
	 */
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @return the protocalMap
	 */
	public Map<String, Protocal> getProtocalMap() {
		return protocalMap;
	}

	/**
	 * @param protocalMap
	 *            the protocalMap to set
	 */
	public void setProtocalMap(Map<String, Protocal> protocalMap) {
		this.protocalMap = protocalMap;
	}

	/**
	 * @return the serverId
	 */
	public String getServerId() {
		return serverId;
	}

	/**
	 * @param serverId
	 *            the serverId to set
	 */
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	/**
	 * @return the lengthStartPos
	 */
	public int getLengthStartPos() {
		if (attributes.containsKey("lengthStartPos")) {
			return Integer.valueOf(attributes.get("lengthStartPos"));
		} else {
			return 0;
		}
	}

	/**
	 * @return the lengthLength
	 */
	public int getLengthLength() {
		if (attributes.containsKey("lengthLength")) {
			return Integer.valueOf(attributes.get("lengthLength"));
		} else {
			return 2;
		}
	}

	/**
	 * @return the commandStartPos
	 */
	public int getCommandStartPos() {
		return Integer.valueOf(attributes.get("commandStartPos"));
	}

	/**
	 * @return the commandLength
	 */
	public int getCommandLength() {
		return Integer.valueOf(attributes.get("commandLength"));
	}

	/**
	 * @return the commandStartPos
	 */
	public int getResponseCodeStartPos() {
		return Integer.valueOf(attributes.get("responseCodeStartPos"));
	}

	/**
	 * @return the commandLength
	 */
	public int getResponseCodeLength() {
		return Integer.valueOf(attributes.get("responseCodeLength"));
	}

	/**
	 * @return the versionStartPos
	 */
	public int getVersionStartPos() {
		return Integer.valueOf(attributes.get("versionStartPos"));
	}

	/**
	 * @return the versionLength
	 */
	public int getVersionLength() {
		return Integer.valueOf(attributes.get("versionLength"));
	}

	/**
	 * get protocal
	 * 
	 * @param code
	 * @return
	 */
	public Protocal getProtocal(String code) {
		return protocalMap.get(code);
	}

}
