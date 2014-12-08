package com.ccagame.protocal.tool;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class PubUtil {

	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * generate a random code with the given length
	 * 
	 * @return a random code
	 */
	public static String generateCode(int length) {
		if (length <= 0) {
			return "";
		}
		final int charSum = 10; // 10 character
		int i; // generate random number
		int count = 0; // generate code length
		char[] strChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		StringBuffer code = new StringBuffer("");
		Random r = new Random();
		while (count < length) {
			i = Math.abs(r.nextInt(charSum)); // the i value between 1 - 36

			if (i >= 0 && i < strChar.length) {
				code.append(strChar[i]);
				count++;
			}

		}
		return code.toString();
	}

	/**
	 * generate a random password with the given length
	 * 
	 * @return a random password
	 */
	public static String generateStrCode(int length) {
		if (length < 1) {
			return "";
		}
		final int charSum = 36; // 26 character
		int i; // generate random number
		int count = 0; // generate password length
		char[] strChar = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
				's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		StringBuffer pwd = new StringBuffer("");
		Random r = new Random();
		while (count < length) {

			i = Math.abs(r.nextInt(charSum)); // the i value between 1 - 36

			if (i >= 0 && i < strChar.length) {
				pwd.append(strChar[i]);
				count++;
			}

		}
		return pwd.toString();
	}

	public static String md5Encrypt(String value) {
		byte[] obj = value.getBytes();
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.update(obj);

			StringBuffer sb = new StringBuffer();
			String temp = "";
			byte[] b = md5.digest();
			for (int i = 0; i < b.length; i++) {
				temp = Integer.toHexString(b[i] & 0Xff);
				if (temp.length() == 1)
					temp = "0" + temp;
				sb.append(temp);
			}
			return sb.toString().toUpperCase();

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
		return DataConvert.byteToHexString(b);
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
	 * @param value
	 *            the int value need to be convert;
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
	 * @param value
	 *            the int value need to be convert;
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
	 * copy the byte array,if the source array length is less than the byteSize,
	 * the append the 0 byte to the left
	 * 
	 * @param b
	 *            the by need to be copied
	 * @param byteSize
	 *            the return byte size
	 * @return the new byte with the byte size
	 */
	public static byte[] copyArray(byte[] b, int byteSize) {
		return copyArray(b, byteSize, true);

	}

	public static byte[] copyArray(byte[] b, int byteSize, boolean isFromHead) {
		byte[] value = new byte[byteSize];
		int index = 0;
		int srcIndex = 0;
		if (byteSize > b.length) {
			index = byteSize - b.length;
			byteSize = b.length;
		} else {
			if (!isFromHead) {
				srcIndex = b.length - byteSize;
			}
		}

		System.arraycopy(b, srcIndex, value, index, byteSize);
		return value;

	}

	/**
	 * convert the byte array to the int
	 * 
	 * @param b
	 *            the source byte array
	 * @return the int value
	 */
	public static final int byteArrayToInt(byte[] b) {
		return (int) byteArrayToLong(b);
	}

	/**
	 * convert the byte array to the long
	 * 
	 * @param b
	 *            the source byte array
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
	 * 
	 * @param value
	 *            the int value need to be connverted
	 * @param outSize
	 *            , the return array size
	 * @return byte array
	 */
	public static byte[] intToByteArray(int value, int outSize) {
		byte[] data = intToByteArray(value);

		return copyArray(data, outSize, false);
	}

	/**
	 * convert long data to byte array
	 * 
	 * @param value
	 *            the long value need to be connverted
	 * @param outSize
	 *            , the return array size
	 * @return byte array
	 */
	public static byte[] longToByteArray(long value, int outSize) {
		byte[] data = longToByteArray(value);
		return copyArray(data, outSize, false);
	}

	public static void main(String[] args) throws Exception {
		String str = "2011-08-01 23:12:23";
		System.out.println(getStandardSecond(df.parse(str)));
		System.out.println(df.format(getStandardDate(getStandardSecond(df.parse(str)))));
	}

	/**
	 * get seconds from the standard date
	 * 
	 * @param currentDate
	 * @return
	 */
	public static int getStandardSecond(Date currentDate) {
		try {
			Date date = df.parse(df.format(currentDate));
			int second = (int) ((date.getTime()) / 1000);
			return second;

		} catch (Exception e) {
		}
		return 0;
	}

	/**
	 * get the current start data
	 * 
	 * @param currentDate
	 * @return
	 */
	public static Date getStandardDate(int second) {
		try {
			long secondTmp = PubUtil.byteArrayToLong(PubUtil.intToByteArray(second));
			long time = secondTmp * 1000;
			return new Date(time);

		} catch (Exception e) {
		}
		return new Date();
	}

	/**
	 * get randomint
	 * 
	 * @return
	 */
	public static int getRandomInt() {
		Random r = new Random();
		int feed = r.nextInt(Integer.MAX_VALUE);
		return feed;
	}

	/**
	 * check whether the object is null or empyt
	 * 
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
	 * 
	 * @param e
	 *            exception object
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
	 * 
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

	/**
	 * get int data
	 * 
	 * @param data
	 *            the byte array data from client
	 * @param startIndex
	 *            the start index of the array
	 * @param length
	 *            the byte size need to read from the data
	 * @return the int vlaue
	 */
	public static int getIntData(byte[] data, int startIndex, int length) {
		byte[] tmp = PubUtil.getDefaultByte(length);
		System.arraycopy(data, startIndex, tmp, 0, tmp.length);
		return PubUtil.byteArrayToInt(tmp);

	}

	public static String generateToken(String uid, String password) {

		String time = String.valueOf(System.currentTimeMillis() / 1000);
		// step 1
		String key = "dk94a32b";
		String token = "";
		// step 2;
		if (uid == null || uid.trim().length() == 0) {
			uid = new String(new char[] { key.charAt(2), key.charAt(0), key.charAt(3), key.charAt(6) });
		}
		if (password == null || password.trim().length() == 0) {
			password = new String(new char[] { key.charAt(4), key.charAt(1), key.charAt(7), key.charAt(5) });
		}

		// step 3
		token = md5Encrypt(uid + time + password);

		String token1 = token.substring(0, 2);
		String token2 = token.substring(2, 10);
		String token3 = token.substring(10, 20);
		String token4 = token.substring(20);

		String time1 = time.substring(0, 3);
		String time2 = time.substring(3, 6);
		String time3 = time.substring(6);

		token = token1 + time1 + token2 + time2 + token3 + time3 + token4;
		return token;

	}

	public static String validToken(String uid, String password, String token) {
		// step 1
		String key = "dk94a32b";
		String strTime1 = null, strTime2 = null, strTime3 = null, strTime = null;
		// step 2;
		if (uid == null || uid.trim().length() == 0) {
			uid = new String(new char[] { key.charAt(2), key.charAt(0), key.charAt(3), key.charAt(6) });
		}
		// hack bug
		if (uid == null || uid.trim().length() == 0) {
			password = "39";
			uid = "0000";
		}
		if (password == null || password.trim().length() == 0) {
			password = new String(new char[] { key.charAt(4), key.charAt(1), key.charAt(7), key.charAt(5) });
		}

		// get strTime
		try {
			strTime1 = token.substring(2, 5);
			strTime2 = token.substring(13, 16);
			strTime3 = token.substring(26, (token.length() - 12));
		} catch (Exception ignore) {
			ignore.printStackTrace();
			return "";
		}
		strTime = strTime1 + strTime2 + strTime3;

		// step 3
		token = uid + strTime + password;
		// step 4
		token = md5Encrypt(token);
		StringBuffer sbToken = new StringBuffer(token);
		sbToken.insert(2, strTime1);
		sbToken.insert(13, strTime2);
		sbToken.insert(26, strTime3);

		return sbToken.toString().toLowerCase();

	}

}
