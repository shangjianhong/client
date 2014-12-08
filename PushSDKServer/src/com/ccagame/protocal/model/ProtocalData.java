package com.ccagame.protocal.model;


public class ProtocalData {

	public int position;

	private int length;

	private byte[] data;

	private byte[] headerData;

	private byte[] bodyData;

	private int headerLength;

	private int bodyLength;

	public ProtocalData(byte[] data) {
		position = 0;
		length = data.length;
		this.data = data;
	}

	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(byte[] data) {
		this.data = data;
	}

	/**
	 * @return the headerData
	 */
	public byte[] getHeaderData() {
		return headerData;
	}

	/**
	 * @param headerData the headerData to set
	 */
	public void setHeaderData(byte[] headerData) {
		this.headerData = headerData;
	}

	/**
	 * @return the bodyData
	 */
	public byte[] getBodyData() {
		return bodyData;
	}

	/**
	 * @param bodyData the bodyData to set
	 */
	public void setBodyData(byte[] bodyData) {
		this.bodyData = bodyData;
	}

	/**
	 * get the byte array by the length
	 * 
	 * @param length
	 * @return
	 * @throws Exception
	 */
	public byte[] getFieldByteArray(int length) throws Exception {
		if ((position + length) >= this.length) {
			throw new Exception("The data size is wrong, the max size is =[" + this.length
					+ "], but the position+length=[" + (position + length));
		} else {
			byte[] tmp = new byte[length];
			System.arraycopy(data, position, tmp, 0, tmp.length);
			return tmp;
		}
	}

	/**
	 * @return the headerLength
	 */
	public int getHeaderLength() {
		return headerLength;
	}

	/**
	 * @param headerLength the headerLength to set
	 */
	public void setHeaderLength(int headerLength) {
		this.headerLength = headerLength;
	}

	/**
	 * @return the bodyLength
	 */
	public int getBodyLength() {
		return bodyLength;
	}

	/**
	 * @param bodyLength the bodyLength to set
	 */
	public void setBodyLength(int bodyLength) {
		this.bodyLength = bodyLength;
	}

	/**
	 * check whether has more request data
	 * @return
	 */
	public boolean hasNextRequestData() {
		if (length > position) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * get next request data if it have any
	 * @return the the pos
	 */
	public byte[] getNextRequestData() {
		if (hasNextRequestData()) {
			int nextRequestLength = length - position;
			byte[] nextRequestData = new byte[nextRequestLength];
			System.arraycopy(data, position, nextRequestData, 0, nextRequestData.length);
			return nextRequestData;
		} else {
			return new byte[] {};
		}
	}

}
