/**
 * 
 */
package com.ccagame.protocal;

import java.util.List;
import java.util.Map;

/**
 * @author Martin
 *
 */
public class ProtocalField implements Comparable<ProtocalField> {
	private String code;

	private Integer length;

	private Integer type;

	private Integer seq;

	private List<ProtocalField> children;

	private Map<String, String> attributes;

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the length
	 */
	public Integer getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(Integer length) {
		this.length = length;
	}

	/**
	 * @return the type
	 */
	public Integer getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Integer type) {
		this.type = type;
	}

	/**
	 * @return the seq
	 */
	public Integer getSeq() {
		return seq;
	}

	/**
	 * @param seq the seq to set
	 */
	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	/**
	 * @return the attributes
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	
	public int compareTo(ProtocalField o) {

		return this.seq - o.seq;
	}

	/**
	 * @return the children
	 */
	public List<ProtocalField> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<ProtocalField> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return "code=" + code + ",type=" + type + ",seq=" + seq + ",length=" + length;
	}

	public Integer getVersion() {
		String versionStr = attributes.get("version");

		Integer version = 1;
		if (versionStr != null && !versionStr.trim().equals("")) {
			version = Integer.parseInt(versionStr.trim());
		}
		return version;
	}
	public Integer getSubCmd() {
		String subCmdStr = attributes.get("subCmd");
		int subCmd = -1;
		if(subCmdStr != null && !subCmdStr.trim().equals("")) {
			subCmd = Integer.valueOf(subCmdStr);
		}
		return subCmd;
	}

}
