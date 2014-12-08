package com.android.cc.info.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ProtocolUtil {

	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static final String CM_WAP_PROXY_HOST = "10.0.0.172";
	public static final String CM_WAP_PROXY_HEADER = "X-Online-Host";
	
	/**
	 * 
	 * @param oUrl Original access complete url
	 * @return two params in String array:  0 - new url,  1: host name
	 * @throws Exception
	 */
	public static String[] useWapConnection(String oUrl) throws Exception {
		URL url = new URL(oUrl);
		int port = url.getPort();
		String sPort = (port > 0 ? ":" + port  : "");
		
		String newUrl = url.getProtocol() + "://" + CM_WAP_PROXY_HOST + sPort + url.getPath();
		
		return new String[] { newUrl, url.getHost() };
	}
	

	public static String md5Encrypt(String value) {
		byte[] obj = value.getBytes();
		MessageDigest md5;
		try {
//			md5 = MessageDigest.getInstance("sha-1");
			md5 = MessageDigest.getInstance("md5");
			md5.update(obj);
			return byteToHexString(md5.digest()).toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;

	}

	/**
	 * convert byte to hex string
	 * 
	 * @param b
	 *            the byte need to be converted
	 * @return
	 */
	public static String byteToHexString(byte[] b) {
		StringBuffer sb = new StringBuffer();
		String temp = "";
		for (int i = 0; i < b.length; i++) {
			temp = Integer.toHexString(b[i] & 0Xff);
			if (temp.length() == 1)
				temp = "0" + temp;
			sb.append(temp);
		}
		return sb.toString();
	}

	/**
	 * append the string using the special
	 * 
	 * @param str
	 *            the string need to be appended
	 * @param isLeft
	 *            if true append the characher to the left, else append to right
	 * @param appendChar
	 *            appened character
	 * @param length
	 *            the total length of the return string
	 * @return
	 */
	public static String appendStr(String str, boolean isLeft, char appendChar, int length) {
		if (str != null) {
			str = "";
		}
		for (int i = 0; i < (length - str.length()); i++) {
			if (isLeft) {
				str = appendChar + str;
			} else {
				str = str + appendChar;
			}
		}
		return str;
	}

	/**
	 * convert the int value to byte array
	 * 
	 * @param value the int value need to be convert;
	 * @return four byte array
	 */
	public static byte[] intToByteArray(int value) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			int offset = (b.length - 1 - i) * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
	}

	/**
	 * convert the int value to byte array
	 * 
	 * @param value the int value need to be convert;
	 * @return four byte array
	 */
	public static byte[] longToByteArray(long value) {
		byte[] b = new byte[8];
		for (int i = 0; i < 8; i++) {
			int offset = (b.length - 1 - i) * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
	}

	/**
	 * copy the byte array,if the source array length is less than the byteSize, the applen the 0 byte to the left
	 * @param b the by need to be copy
	 * @param byteSize the return byte size
	 * @return the new byte with the byte size
	 */
	public static byte[] copyArray(byte[] b, int byteSize) {
		byte[] value = new byte[byteSize];
		int index = 0;
		int srcIndex = b.length - byteSize;
		if (byteSize > b.length) {
			index = byteSize - b.length;
			byteSize = b.length;
			srcIndex = 0;
		}

		System.arraycopy(b, srcIndex, value, index, byteSize);
		return value;

	}

	/**
	 * convert the byte array to the int
	 * @param b the source byte array
	 * @return the int value
	 */
	public static final int byteArrayToInt(byte[] b) {
		return (int) byteArrayToLong(b);
	}

	/**
	 * convert the byte array to the long
	 * @param b the source byte array
	 * @return the long value
	 */
	public static final long byteArrayToLong(byte[] b) {
		long value = 0;
		for (int i = 0; i < b.length - 1; i++) {
			value += (((long) b[i] & 0xFF) << (b.length - 1 - i) * 8);
		}
		value += (long) b[b.length - 1] & 0xFF;
		return value;
	}

	/**
	 * convert int data to byte array
	 * @param value the int value need to be connverted
	 * @param outSize, the return array size
	 * @return byte array
	 */
	public static byte[] intToByteArray(int value, int outSize) {
		byte[] data = intToByteArray(value);
		return copyArray(data, outSize);
	}

	/**
	 * convert long data to byte array
	 * @param value the long value need to be connverted
	 * @param outSize, the return array size
	 * @return byte array
	 */
	public static byte[] longToByteArray(long value, int outSize) {
		byte[] data = longToByteArray(value);
		return copyArray(data, outSize);
	}

	public static void main(String[] args) {
		System.out.println(md5Encrypt("1.6"));
	}

	/**
	 * get seconds from the standard date
	 * @param currentDate
	 * @return
	 */
	public static int getStandardSecond(Date currentDate) {
		try {
			Date date = df.parse(df.format(currentDate));
			Date standandDate = df.parse("1970-01-01 00:00:00");
			int second = (int) ((date.getTime() - standandDate.getTime()) / 1000);
			return second;

		} catch (Exception e) {
		}
		return 0;
	}

	/**
	 * get the current start data
	 * @param currentDate
	 * @return
	 */
	public static Date getStandardDate(int second) {
		try {
			Date standandDate = df.parse("1970-01-01 00:00:00");
			long secondTmp = ProtocolUtil.byteArrayToLong(ProtocolUtil.intToByteArray(second));
			long time = standandDate.getTime() + secondTmp * 1000;
			return new Date(time);

		} catch (Exception e) {
		}
		return new Date();
	}

	/**
	 * get randomint 
	 * @return
	 */
	public static int getRandomInt() {
		Random r = new Random();
		int feed = r.nextInt(Integer.MAX_VALUE);
		return feed;
	}

	/**
	 * check whether the object is null or empyt
	 * @param obj
	 * @return
	 */
	public static boolean isNullAndEmpty(Object obj) {
		if (obj == null) {
			return true;
		} else if (obj.toString().trim().equals("")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * print stack tracet information
	 * @param e exception object
	 * @return
	 */
	public static String stackTraceToString(Exception e) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream p = new PrintStream(baos);
		e.printStackTrace(p);
		p.flush();
		return baos.toString();
	}

	public static byte[] getDefaultByte(int byteSize) {
		byte[] data = new byte[byteSize];
		for (int i = 0; i < data.length; i++) {
			data[0] = 0;
		}
		return data;
	}

	/**
	 * if the object is null, set the value to empty
	 * @param obj
	 * @return
	 */
	public static Object setNullValueToEmpty(Object obj) {
		if (isNullAndEmpty(obj)) {
			return "";
		} else {
			return obj;
		}
	}

	public static boolean isDigital(String str) {
		try {
			Integer.valueOf(str);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static synchronized String getStringData(byte[] data, int startIndex, int length) {
		byte[] tmp = getDefaultByte(length);
		System.arraycopy(data, startIndex, tmp, 0, tmp.length);
		try {
			return new String(tmp, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	public static synchronized long getLongData(byte[] data, int startIndex, int length) {
		byte[] tmp = getDefaultByte(length);
		System.arraycopy(data, startIndex, tmp, 0, tmp.length);
		return byteArrayToLong(tmp);
	}

	public static synchronized int getIntData(byte[] data, int startIndex, int length) {
		byte[] tmp = getDefaultByte(length);
		System.arraycopy(data, startIndex, tmp, 0, tmp.length);
		return byteArrayToInt(tmp);
	}
}
