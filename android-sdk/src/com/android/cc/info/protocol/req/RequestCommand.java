package com.android.cc.info.protocol.req;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.android.cc.info.protocol.Command;
import com.android.cc.info.protocol.CustomException;
import com.android.cc.info.util.ProtocolUtil;

public abstract class RequestCommand extends Command {
	private static final String TAG = "RequestCommand";
	
	public int mSendTimes = 0;
	
	public RequestCommand(int command) {
		this.mCommand = command;
	}
	
	public void buildBody() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			buildBody(baos);
			this.mBody = baos.toByteArray();
		} catch (IOException e) {
			
		}
	}
	
	public void buildDataBeforeAdd() throws Exception{
		try {
			mData = getRequestByte();
		} catch (CustomException e) {
			mData = new byte[0];
		}
	}
	
	/**
	 * get request bytes
	 * @return byte array
	 */
	public byte[] getRequestByte() throws Exception {
		mSendTimes ++;
		buidRequestHeader();
		if (null == this.mHeader 
				||  (null == this.mBody)) {
			throw new CustomException(CustomException.ERROR_CODE_BAD_REQUEST_COMMAND);
		}
		return combineData(this.mHeader, this.mBody);
	}
	
	protected abstract void buildBody(ByteArrayOutputStream baos) throws IOException;
	
	/**
	 * commbie header and body
	 * 
	 * @param header header data array
	 * @param body body data array
	 * @return
	 */
	protected byte[] combineData(byte[] header, byte[] body) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			if (header != null) {
				baos.write(header);
			}
			if (body != null) {
				baos.write(body);
			}
			return updateTotalLength(baos);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}

	}
	
	/**
	 * update the header length field valuebyte length
	 * @param bos
	 * @return
	 * @throws IOException
	 */
	private byte[] updateTotalLength(ByteArrayOutputStream bos) throws IOException {
		mLength = bos.size();
		byte[] totalLenth = ProtocolUtil.intToByteArray(mLength, 4);
//		DebugLog.v("KKCommand", "TotalLength:" + bos.size());
		try {
			bos.flush();
		} catch (Exception e) {
		}
		byte[] data = bos.toByteArray();
		System.arraycopy(totalLenth, 0, data, 0, totalLenth.length);
		return data;
	}	
}

