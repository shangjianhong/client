package cn.ccagame.util;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class StringHelper {

	public static boolean isCheckedLngLat(double lng, double lat)
	{
		if (lng == 0.0 || lat == 0.0) {
			return false;
		} else if (lng == 0 || lat == 0) {
			return false;
		} else if (lng > 180 || lng < -180) {
			return false;
		} else if (lat > 90 || lat < -90) {
			return false;
		} else {
			return true;
		}

	}
	
	/**
	 * �??Html???
	 * 
	 * @param input
	 * @return
	 */
	public static String escapeHTMLTags(String input) {
		if (input == null || input.length() == 0) {
			return input;
		}

		StringBuffer buf = new StringBuffer(input.length() + 6);
		char ch = ' ';
		for (int i = 0; i < input.length(); i++) {
			ch = input.charAt(i);
			if (ch == '<') {
				buf.append("&lt;");
			} else if (ch == '>') {
				buf.append("&gt;");
			} else if (ch == '"') {
				buf.append("&quot;");
			}
			// Convert single quote
			else if (ch == '\'') {
				buf.append("&#39;");
			} else {
				buf.append(ch);
			}
		}
		return buf.toString();
	}

	/**
	 * @param line
	 * @param oldString
	 * @param newString
	 * @return
	 */
	private static String replace(String line, String oldString,
			String newString) {
		if (line == null) {
			return null;
		}
		int i = 0;
		if ((i = line.indexOf(oldString, i)) >= 0) {
			char[] line2 = line.toCharArray();
			char[] newString2 = newString.toCharArray();
			int oLength = oldString.length();
			StringBuffer buf = new StringBuffer(line2.length);
			buf.append(line2, 0, i).append(newString2);
			i += oLength;
			int j = i;
			while ((i = line.indexOf(oldString, i)) > 0) {
				buf.append(line2, j, i - j).append(newString2);
				i += oLength;
				j = i;
			}
			buf.append(line2, j, line2.length - j);
			return buf.toString();
		}

		return line;
	}


	/**
	 * �?????<br>
	 * ??etBytes(encoding)�?????�?��???�?yte?��?<br>
	 * �?[0]�?63?��?�????��???�?br>
	 * A???乱�????�??�?���?<br>
	 * 1??ncoding??B2312?��?�?yte????��?<br>
	 * 2??ncoding??SO8859_1?��?b[i]?��?63??br>
	 * B??��???�??�??串�?<br>
	 * 1??ncoding??SO8859_1?��?�?yte�??�??�?br>
	 * 2??ncoding??B2312?��?b[i]大�????63??br>
	 * C??????�?��<br>
	 * 1??ncoding??SO8859_1??B2312?��?�?yte?�大�?�?br>
	 * <p>
	 * ?��?�??�??�??�?���??getBytes("iso8859_1") <br>
	 * 1?????[i]??3�???�转??? A-2<br>
	 * 2?????[i]?�大�?�??�?��?��?�??串�?�??�??�?B-1<br>
	 * 3?????[i]???�?????��?已�?乱�?�??�????C-1 <br>
	 */
	public static String toGBK(String source) {
		if (source == null) {
			return null;
		}

		if (source.trim().equals("")) {
			return source;
		}

		String retStr = source;

		try {
			byte b[] = source.getBytes("ISO8859_1");

			for (int i = 0; i < b.length; i++) {
				byte b1 = b[i];
				if (b1 == 63) {
					break; // 1
				} else if (b1 > 0) {
					continue;// 2
				} else if (b1 < 0) {
					// �???�为0�?为�?�?���??�?
					retStr = new String(b, "GBK");
					break;
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return retStr;
	}

	/**
	 * �??�??�??�?��??��GBK�??
	 * 
	 * @param source
	 * @return
	 */
	public static String[] toGBK(String[] source) {
		String[] ret = new String[source.length];
		for (int i = 0; i < source.length; i++) {
			ret[i] = toGBK(source[i]);
		}

		return ret;
	}

	/**
	 * ?��?�??串�?�?ull???�?��""
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isNotNull(String value) {
		if (null != value && !"".equals(value.trim())) {
			return true;
		}

		return false;
	}

	/**
	 * @param value
	 * @return
	 */
	public static boolean isNotNull(Object value) {
		// �??对象�?���?
		if (null != value) {
			// �??对象???�?���??对象为空?��?�?? false�??�????rue
			if (value instanceof String && "".equals(((String) value).trim())) {
				return false;
			}

			return true;
		}

		return false;
	}

	/**
	 * ?��?�??串�?�??�?ull???�?���?
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isNotNull(String[] value) {
		if (null != value && value.length != 0) {
			return true;
		}

		return false;
	}

	/**
	 * �??�?��?��?�?���?rg??????�?��<br>
	 * 
	 * @param args
	 * @param arg
	 * @return
	 */
	public static String array2String(String[] args, String arg) {
		StringBuffer sb = new StringBuffer();

		// ?��?为空,?��?�??空�?�?��
		if (args == null || args.length <= 0) {
			return "";
		}

		for (int i = 0; i < args.length; i++) {
			// ?��????为空??���??串�???��??��空�?
			if (args[i] == null || args[i].trim().equals("")) {
				sb.append(arg + "");
			} else {
				sb.append(arg + args[i]);
			}
		}

		// ?��?�??�?????
		if (sb.length() > 0) {
			return sb.substring(arg.length());
		}

		return sb.toString();
	}

	/**
	 * �??�?��?��?�?���?rg??????�?��<br>
	 * 
	 * @param args
	 * @param arg
	 * @return
	 */
	public static String array2Int(int[] args, String arg) {
		StringBuffer sb = new StringBuffer();

		// ?��?为空,?��?�??空�?�?��
		if (args == null || args.length <= 0) {
			return "";
		}

		for (int i = 0; i < args.length; i++) {
			// ?��????为空??���??串�???��??��空�?
			sb.append(arg + args[i]);

		}

		// ?��?�??�?????
		if (sb.length() > 0) {
			return sb.substring(arg.length());
		}

		return sb.toString();
	}

	/**
	 * ???�??串�?�??�????���??�???????��???�??代�?
	 * 
	 * @param source
	 * @return
	 */
	public static String truncateString(String source, int maxLength,
			String substitute) {
		if (StringHelper.isNotNull(source)) {
			char[] array = source.toCharArray();
			int length = 0;
			for (int i = 0; i < array.length; i++) {
				char c = array[i];
				if (c <= 0 || c >= 126) {
					length += 2;
				} else {
					length++;
				}
			}

			if (length > maxLength && source.length() > maxLength) {
				return source.substring(0, maxLength) + substitute;
			}
		}

		return source;
	}

	/**
	 * ???�??串�?�??�????���??�???????��???�??代�?
	 * 
	 * @param source
	 * @return
	 */
	public static String truncateString(String source, int maxLength) {
		return StringHelper.truncateString(source, maxLength, "");
	}

	/**
	 * ???�??串中???HTML???
	 * 
	 * @param input
	 * @return
	 */
	public static String toPlainText(String input) {
		if (input == null) {
			return "";
		}

		input = input.replaceAll("</?[^>]+>", "");

		return input;
	}

	/**
	 * �?????�??串为HTML�??�?
	 * 
	 * @param input
	 */
	public static String toHtmlText(String input) {
		String ret = escapeHTMLTags(input);

		ret = replace(ret, "\r\n", "<br>");
		ret = replace(ret, "\n", "<br>");
		ret = replace(ret, "\r", "<br>");
		ret = replace(ret, " ", "&nbsp;");
		ret = replace(ret, "<", "&lt;");
		ret = replace(ret, "<", "&gt;");

		return ret;
	}

	/**
	 * �?????�??串为HTML�??�?
	 * 
	 * @param input
	 */
	public static String textToHtml(String input) {
		return toHtmlText(input);
	}

	public static String toSqlStringField(String[] ids) {
		if (ids == null || ids.length < 1) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ids.length; i++) {

			if (i > 0) {
				sb.append(",'").append(ids[i]).append("'");
			} else {
				sb.append("'").append(ids[i]).append("'");
			}
		}
		return sb.toString();

	}

	public static String toSqlIntField(String[] ids) {
		if (ids == null || ids.length < 1) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ids.length; i++) {

			if (i > 0) {
				sb.append(",").append(ids[i]);
			} else {
				sb.append(ids[i]);
			}
		}
		return sb.toString();
	}

	public static String toSqlParam(Object object) {

		if (object instanceof String) {
			return "'" + object.toString() + "'";
		} else if (object instanceof Integer) {
			return object.toString();
		} else if (object instanceof Long) {
			return object.toString();
		}
		return object.toString();
	}

	/**
	 * ??????�??�?
	 * 
	 * @param pwd_len
	 *            ??????�?��????�度
	 * @return �??�?
	 */
	public static String getRandomStr(int pwd_len) {
		// 35???为�?�??�?�?????26�??�?10�??�?
		final int maxNum = 36;
		int i; // ???????��?
		int count = 0; // ??????????�度
		char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
				'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
				'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

		StringBuffer pwd = new StringBuffer("");
		Random r = new Random();
		while (count < pwd_len) {
			// ???????��????对�?�??止�?????��?

			i = Math.abs(r.nextInt(maxNum)); // ????????���?6-1

			if (i >= 0 && i < str.length) {
				pwd.append(str[i]);
				count++;
			}
		}

		return pwd.toString();
	}

	/**
	 * ??????�??�?
	 * 
	 * @param pwd_len
	 *            ??????�?��????�度
	 * @return �??�?
	 */
	public static String getRandomNum(int length) {
		// 35???为�?�??�?�????10�??�?
		final int maxNum = 10;
		int i; // ???????��?
		int count = 0; // ??????????�度
		char[] str = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

		StringBuffer pwd = new StringBuffer("");
		Random r = new Random();
		while (count < length) {
			// ???????��????对�?�??止�?????��?

			i = Math.abs(r.nextInt(maxNum)); // ????????���?6-1

			if (i >= 0 && i < str.length) {
				pwd.append(str[i]);
				count++;
			}
		}

		// to do list ....
		// 1. ???????��??��?�??�??????��?????��?

		return pwd.toString();
	}

	/*
	 * ?��?类�?�??
	 */
	@SuppressWarnings("rawtypes")
	public static Object[] arrayConvert(Class className, Object[] objects) {
		Object[] objs = new Object[objects.length];
		for (int i = 0; i < objects.length; i++) {
			if (className.getName().equals(String.class.getName())) {
				objs[i] = String.valueOf(objects[i]);
			} else if (className.getName().equals(Integer.class.getName())) {
				objs[i] = Integer.valueOf(objects[i].toString());
			} else if (className.getName().equals(Long.class.getName())) {
				objs[i] = Long.valueOf(objects[i].toString());
			}
		}
		return objs;
	}

	public static String toMsgId(int uid, int messageId) {
		return messageId + ":" + uid;
	}

	public static String getMsgId(String messageId) {
		return messageId.split(":")[0];
	}

	public static int getMessageId(String messageId) {
		return Integer.parseInt(messageId.split(":")[0]);
	}

	public static int getMsgUid(String messageId) {
		return Integer.parseInt(messageId.split(":")[1]);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String mapToString(Map map) {
		if (map == null || map.size() < 1) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		Iterator<String> keys = map.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			sb.append(key).append(",");
		}
		return sb.toString().substring(0, sb.toString().length() - 1);
	}




	public static void main(String[] args) {

	}
}
