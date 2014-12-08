package com.android.cc.info.protocol.resp;

import com.android.cc.info.protocol.Command;
import com.android.cc.info.protocol.CustomException;
import com.android.cc.info.util.DebugLog;
import com.android.cc.info.util.ProtocolUtil;
import com.android.cc.info.util.StringUtils;


/**
 * From server to client
 */
public abstract class ResponseCommand extends Command {
	private static final String TAG = "ResponseCommand";
	
	public int mErrorCode;
	public String mErrorReason;

	public ResponseCommand() {
	}
	
	public boolean isErrorResult() {
		return (mErrorCode > 0);
	}
		
	public ResponseCommand(byte[] data) throws CustomException {
		if (null == data) return;
		this.mData = data;
		parseData(data);
	}
	
	/**
	 * parse the data from client
	 * @param data
	 * @throws KKException
	 */
	protected void parseData(byte[] data) throws CustomException {
		DebugLog.d(TAG, "responseData:"+StringUtils.toHex(data));
		this.mHeader = new byte[13];
		if (data.length < mHeader.length) {
			throw new CustomException(CustomException.ERROR_CODE_DATA_LENGTH_WRONG, 
					"data length is lower than baisc header len - 6");
		}
		System.arraycopy(data, 0, mHeader, 0, mHeader.length);
		parseHeader();
		if (this.mLength > data.length) {
			throw new CustomException(CustomException.ERROR_CODE_DATA_LENGTH_WRONG, 
					"header len should not be larger than actual data length - header len:" + this.mLength + ", data len:" + data.length);
		}
		
		byte[] eCode = new byte[2];
		System.arraycopy(data, 11, eCode, 0, eCode.length);
		mErrorCode = ProtocolUtil.byteArrayToInt(eCode);
		
		this.mBody = new byte[data.length - 13];
		System.arraycopy(data, 13, mBody, 0, mBody.length);
		
		if (mErrorCode > 0) {
			int startIndex = 0;
			int len = getIntData(this.mBody, startIndex, 2);
			startIndex += 2;
			mErrorReason = ProtocolUtil.getStringData(this.mBody, startIndex, len);
			return;
		}
		
		try {
			parseBody();
		} catch (ArrayIndexOutOfBoundsException e) {
			DebugLog.d(TAG, "parseData ArrayIndexOutOfBoundsException",e);
			throw new CustomException(CustomException.ERROR_CODE_INVALID_RESPONSE);
		}
	}
	
	protected abstract void parseBody() throws CustomException;

	/**
	 * parse header param
	 * @param mData header byte
	 * @throws KKException
	 */
	protected void parseHeader() throws CustomException {
		//length
		int startIndex = 0;
		byte[] bLen = new byte[4];
		System.arraycopy(mHeader, startIndex, bLen, 0, bLen.length);
		this.mLength = ProtocolUtil.byteArrayToInt(bLen);
		startIndex += 4;

		//version
		byte param = mHeader[startIndex];
		this.mVersion = param & 0xFF;
		startIndex += 1;
		
		//command
		byte[] bCommand = new byte[2];
		System.arraycopy(mHeader, startIndex, bCommand, 0, bCommand.length);
		this.mCommand = ProtocolUtil.byteArrayToInt(bCommand);
		startIndex += 2;
		
		//rid
		byte[] bRid = new byte[4];
		System.arraycopy(mHeader, startIndex, bRid, 0, bRid.length);
		this.mRid = ProtocolUtil.byteArrayToInt(bRid);
	}
	
	protected void parseParam(byte param) {
		int value = param;
		mVersion = value & 0xFF;
	}
}
