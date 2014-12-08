package com.android.cc.info.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import android.text.TextUtils;

import com.android.cc.info.util.ProtocolUtil;

public class Command {

	public static final int CMD_REGISTER = 1; 
	
	public static final int CMD_AD_PSUH = 2;
	
	public static final int CMD_AD_BANNER = 3;
	
	public static final int CMD_USER_STEP = 4;
	
	public static final int CMD_BATCH_USER_STEP = 5;
	
	public static final int CMD_FETCH_DATA = 6;
	
	public int mLength;

	public int mVersion = 1;

	public int mCommand;

	public int mRid;

	protected byte[] mHeader;
	protected byte[] mBody;
	protected byte[] mData;

	

	public void buidRequestHeader() {
		mHeader = new byte[11];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			//header defined
			/**
			 * len|version|command|RID|SID
			   2B|1B|1B|2B|4B
			 */
			//length  2 bytes
			bos.write(ProtocolUtil.intToByteArray(11, 4));
			//param field 1 byte
			bos.write(ProtocolUtil.intToByteArray(mVersion, 1));
			//command field 1 byte
			bos.write(ProtocolUtil.intToByteArray(mCommand, 2));
			//rid field 2 bytes
			bos.write(ProtocolUtil.intToByteArray(mRid, 4));

			mHeader = bos.toByteArray();
		} catch (Exception e) {
			try {
				bos.close();
			} catch (IOException e1) {
			}
		}
	}
	
	public void writeTLV2(ByteArrayOutputStream baos, String string) throws IOException {
		if (TextUtils.isEmpty(string)) {
			baos.write(ProtocolUtil.intToByteArray(0, 2));
		} else {
			byte[] data = string.getBytes("UTF-8");
			baos.write(ProtocolUtil.intToByteArray(data.length, 2));
			baos.write(data);
		}
	}
	
	public void writeTLV3(ByteArrayOutputStream baos, String string) throws IOException {
		if (TextUtils.isEmpty(string)) {
			baos.write(ProtocolUtil.intToByteArray(0, 2));
		} else {
			byte[] data = compress(string);
			baos.write(ProtocolUtil.intToByteArray(data.length, 2));
			baos.write(data);
		}
	}
	
	protected static String getTLV3Data(byte[] data, int startIndex, int length){
		byte[] tmp = ProtocolUtil.getDefaultByte(length);
		System.arraycopy(data, startIndex, tmp, 0, tmp.length);
		return decompress(tmp);
	}
	
	private byte[] compress(String str) {
		if (str == null)
			return null;

		ByteArrayOutputStream out = new ByteArrayOutputStream();
	    GZIPOutputStream gzip;
		try {
			gzip = new GZIPOutputStream(out);
		    gzip.write(str.getBytes());
		    gzip.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return out.toByteArray();
	}
	
	public static final String decompress(byte[] compressed) {
		 if (compressed == null || compressed.length == 0) {
		      return "";
		    }
		    ByteArrayOutputStream out = new ByteArrayOutputStream();
		    ByteArrayInputStream in = new ByteArrayInputStream(compressed);
		    GZIPInputStream gunzip;
			try {
				gunzip = new GZIPInputStream(in);
			    byte[] buffer = new byte[256];
			    int n;
			    while ((n = gunzip.read(buffer)) >= 0) {
			      out.write(buffer, 0, n);
			    }
			} catch (IOException e) {
			}

			return out.toString();
	}
	
	protected static int getIntData(byte[] data, int startIndex, int length) {
		byte[] tmp = ProtocolUtil.getDefaultByte(length);
		System.arraycopy(data, startIndex, tmp, 0, tmp.length);
		return ProtocolUtil.byteArrayToInt(tmp);
	}
	
	public static int getCommand(byte[] data) throws CustomException {
		if (data.length > 6) {
			//command
			byte bCommand = data[6];
			return (bCommand) & 0xFF;
		} else {
			throw new CustomException(CustomException.ERROR_CODE_DATA_LENGTH_WRONG);
		}
	}
}
