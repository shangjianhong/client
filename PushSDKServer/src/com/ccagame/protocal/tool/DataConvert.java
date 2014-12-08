package com.ccagame.protocal.tool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ccagame.protocal.Constants;
import com.ccagame.protocal.ProtocalField;
import com.ccagame.protocal.model.ProtocalData;

public class DataConvert {
	public static final Logger logger = LoggerFactory.getLogger(DataConvert.class);

	public static final String DEFAULT_ENCODING = Constants.DATA_ENCODING_UTF8;

	public static Object getOject(ProtocalData pd, ProtocalField pf) {
		int length = pf.getLength();
		int type = pf.getType();
		if (type == DataType.DT_INTEGER) {
			Integer value = getIntData(pd.getData(), pd.getPosition(), length);
			pd.position += length;
			return value;
		} else if (type == DataType.DT_LONG) {
			Long value = getLongData(pd.getData(), pd.getPosition(), length);
			pd.position += length;
			return value;
		} else if (type == DataType.DT_STRING) {
			String value = getStringTrueValue(pd.getData(), pd.getPosition(), length);
			pd.position += length;
			return value;
		} else if (type == DataType.DT_TLV2) {
			Integer valueLength = getIntData(pd.getData(), pd.getPosition(), length);
			logger.debug("length=" + valueLength);
			pd.position += length;
			String value = getStringTrueValue(pd.getData(), pd.getPosition(), valueLength);
			pd.position += valueLength;
			return value;
		} else if (type == DataType.DT_TLV3) {
			Integer valueLength = getIntData(pd.getData(), pd.getPosition(), length);
			logger.debug("length=" + valueLength);
			pd.position += length;
			String value = getZipValue(pd.getData(), pd.getPosition(), valueLength);
			pd.position += valueLength;
			return value;
		} else if (type == DataType.DT_TLV4) {
			Integer valueLength = getIntData(pd.getData(), pd.getPosition(), length);
			logger.debug("length=" + valueLength);
			pd.position += length;
			byte[] value = new byte[valueLength];
			System.arraycopy(pd.getData(), pd.getPosition(), value, 0, value.length);
			pd.position += valueLength;
			return value;
		} else if (type == DataType.DT_TLV5) {
			String subTypeStr = pf.getAttributes().get("subType");
			if (subTypeStr == null) {
				return new byte[0];
			} else {
				int subType = Integer.valueOf(subTypeStr);
				if (subType == DataType.DT_INTEGER) {
					List<Integer> valueList = new ArrayList<Integer>();
					while (pd.hasNextRequestData()) {
						Integer value = getIntData(pd.getData(), pd.getPosition(), length);
						pd.position += length;
						valueList.add(value);
					}
					return valueList;
				} else if (subType == DataType.DT_STRING) {
					List<String> valueList = new ArrayList<String>();
					while (pd.hasNextRequestData()) {
						String value = getStringTrueValue(pd.getData(), pd.getPosition(), length);
						pd.position += length;
						valueList.add(value);
					}
					return valueList;
				} else {
					return null;
				}
			}
		}

		else {
			return null;
		}

	}

	/**
	 * convert the object to byte array[]
	 * @param obj the object need to be convert
	 * @param pf response field;
	 * @return the byte array
	 */
	@SuppressWarnings("unchecked")
	public static byte[] convertObject(Object obj, ProtocalField pf) throws Exception {
		int type = pf.getType();
		int length = pf.getLength();
		//integer value
		if (type == DataType.DT_INTEGER) {
			int value = 0;
			if (obj != null) {
				value = (Integer) obj;
			}
			byte[] data = intToByteArray(value, length);
			return data;
		}
		//long value
		else if (type == DataType.DT_LONG) {
			long value = 0;
			if (obj != null) {
				value = (Long) obj;
			}
			byte[] data = longToByteArray(value, length);
			return data;
		}
		//string value
		else if (type == DataType.DT_STRING) {
			String value = "";
			if (obj != null) {
				value = (String) obj;
			}
			byte[] tmp = new byte[length];
			byte[] data = value.getBytes(DEFAULT_ENCODING);

			int maxLength = data.length > length ? length : data.length;
			System.arraycopy(data, 0, tmp, 0, maxLength);
			return tmp;

		}
		//length string vlaue
		else if (type == DataType.DT_TLV2) {
			String value = "";
			if (obj != null) {
				value = (String) obj;
			}
			byte[] data = value.getBytes(DEFAULT_ENCODING);

			byte[] lengthValue = intToByteArray(data.length, length);
			byte[] tmp = new byte[length + data.length];

			System.arraycopy(lengthValue, 0, tmp, 0, lengthValue.length);
			System.arraycopy(data, 0, tmp, lengthValue.length, data.length);
			return tmp;

		}
		//length string zip value
		else if (type == DataType.DT_TLV3) {
			String value = "";
			if (obj != null) {
				value = (String) obj;
			}
			byte[] data = value.getBytes(DEFAULT_ENCODING);
			data = toZipValue(data);

			byte[] lengthValue = intToByteArray(data.length, length);
			byte[] tmp = new byte[length + data.length];

			System.arraycopy(lengthValue, 0, tmp, 0, lengthValue.length);
			System.arraycopy(data, 0, tmp, lengthValue.length, data.length);
			return tmp;
		}

		else if (type == DataType.DT_TLV4) {
			byte[] data = (byte[]) obj;
			if (data == null) {
				return new byte[pf.getLength()];
			} else {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				baos.write(DataConvert.intToByteArray(data.length, pf.getLength()));
				baos.write(data);
				return baos.toByteArray();
			}

		} else if (type == DataType.DT_TLV5) {
			String subTypeStr = pf.getAttributes().get("subType");
			if (subTypeStr == null) {
				return new byte[0];
			} else {
				int subType = Integer.valueOf(subTypeStr);
				if (subType == DataType.DT_INTEGER) {
					List<Integer> objList = (List<Integer>) obj;
					if (objList == null) {
						return new byte[0];
					}
					ByteArrayOutputStream baos = new ByteArrayOutputStream();

					for (int i = 0; i < objList.size(); i++) {
						Integer data = objList.get(i);
						baos.write(DataConvert.intToByteArray(data, pf.getLength()));

					}
					return baos.toByteArray();
				}

				else if (subType == DataType.DT_STRING) {
					List<String> objList = (List<String>) obj;
					if (objList == null) {
						return new byte[0];
					}
					ByteArrayOutputStream baos = new ByteArrayOutputStream();

					for (int i = 0; i < objList.size(); i++) {
						String data = objList.get(i);
						if (data != null) {
							baos.write(DataConvert.getStringByte(data, pf.getLength()));
						}

					}
					return baos.toByteArray();
				} else {
					return new byte[0];
				}
			}

		} else {
			return new byte[0];

		}

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
	public static int byteArrayToInt(byte[] b) {
		return (int) byteArrayToLong(b);
	}

	/**
	 * convert the byte array to the long
	 * @param b the source byte array
	 * @return the long value
	 */
	public static long byteArrayToLong(byte[] b) {
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

	/**
	 * get int data 
	 * @param data the byte array data from client
	 * @param startIndex the start index of the array
	 * @param length the byte size need to read from the data
	 * @return the int vlaue
	 */
	public static int getIntData(byte[] data, int startIndex, int length) {
		byte[] tmp = getDefaultByte(length);
		System.arraycopy(data, startIndex, tmp, 0, tmp.length);
		return byteArrayToInt(tmp);

	}

	/**
	 * get long data 
	 * @param data the byte array data from client
	 * @param startIndex the start index of the array
	 * @param length the byte size need to read from the data
	 * @return the long vlaue
	 */
	public static long getLongData(byte[] data, int startIndex, int length) {
		byte[] tmp = getDefaultByte(length);
		System.arraycopy(data, startIndex, tmp, 0, tmp.length);
		return byteArrayToLong(tmp);

	}

	/**
	 * get String data 
	 * @param data the byte array data from client
	 * @param startIndex the start index of the array
	 * @param length the byte size need to read from the data
	 * @return the String vlaue
	 */
	public static String getStringData(byte[] data, int startIndex, int length) {
		try {
			byte[] tmp = getDefaultByte(length);
			System.arraycopy(data, startIndex, tmp, 0, tmp.length);
			return new String(tmp, DEFAULT_ENCODING);
		} catch (Exception e) {
			e.printStackTrace();

		}
		return "";

	}

	/**
	 * get String data, the [0] will be remove
	 * @param data the byte array data from client
	 * @param startIndex the start index of the array
	 * @param length the byte size need to read from the data
	 * @return the String vlaue
	 */
	public static String getStringTrueValue(byte[] data, int startIndex, int length) {
		try {
			byte[] tmp = getDefaultByte(length);
			System.arraycopy(data, startIndex, tmp, 0, tmp.length);
			for (int i = 0; i < tmp.length; i++) {
				if (tmp[i] == 0) {
					return new String(tmp, 0, i, DEFAULT_ENCODING);
				}
			}
			return new String(tmp, 0, tmp.length, DEFAULT_ENCODING);

		} catch (Exception e) {
			e.printStackTrace();

		}
		return "";

	}

	/**
	 * get String data, the [0] will be remove
	 * @param data the byte array data from client
	 * @param startIndex the start index of the array
	 * @param length the byte size need to read from the data
	 * @return the String vlaue
	 */
	public static String getZipValue(byte[] data, int startIndex, int length) {
		try {
			logger.debug("getZipValue data length="+data.length);
			logger.debug("getZipValue length="+length);
			byte[] tmp = getDefaultByte(length);
			logger.debug("getZipValue tmp length="+tmp.length);
			logger.debug("getZipValue startIndex="+startIndex);
			System.arraycopy(data, startIndex, tmp, 0, tmp.length);
			ByteArrayInputStream bais = new ByteArrayInputStream(tmp);
			InputStream is = new GZIPInputStream(bais);
			ByteArrayOutputStream baos = new ByteArrayOutputStream(length);
			tmp = new byte[length];
			int count = is.read(tmp);
			while (count > 0) {
				baos.write(tmp, 0, count);
				count = is.read(tmp);
			}
			is.close();
			bais.close();
			baos.close();
			return new String(baos.toByteArray(), DEFAULT_ENCODING);

		} catch (Exception e) {
			e.printStackTrace();

		}
		return "";

	}

	/**
	 * get String data, the [0] will be remove
	 * @param data the byte array data from client
	 * @param startIndex the start index of the array
	 * @param length the byte size need to read from the data
	 * @return the String vlaue
	 */
	public static byte[] toZipValue(byte[] data) {
		try {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OutputStream os = new GZIPOutputStream(baos);
			os.write(data);
			os.close();
			baos.close();
			return baos.toByteArray();

		} catch (Exception e) {
			e.printStackTrace();

		}
		return new byte[] {};

	}

	/**
	 * get byte with empty data
	 * @param byteSize the array size
	 * @return the byte array which no data;
	 */
	public static byte[] getDefaultByte(int byteSize) {
		byte[] data = new byte[byteSize];
		for (int i = 0; i < data.length; i++) {
			data[0] = 0;
		}
		return data;
	}

	/**
	 * convert the byte array to string, only for printing and tracing
	 * @param data the byte array need to be convert
	 * @return the decimal string;
	 */
	public static String byteToString(byte[] data) {
		StringBuffer buffer = new StringBuffer();
		if (data != null) {
			buffer.append("[");
			for (int i = 0; i < data.length; i++) {
				buffer.append(data[i]).append(",");
			}
			if (buffer.indexOf(",") > 0) {
				buffer.deleteCharAt(buffer.lastIndexOf(","));
			}
			buffer.append("]");
		}
		return buffer.toString();
	}

	/**
	 * convert the byte array to string, only for printing and tracing
	 * @param data the byte array need to be convert
	 * @return the decimal string;
	 */
	public static String byteToHexString(byte[] data) {
		StringBuffer buffer = new StringBuffer();
		if (data != null) {
			buffer.append("\n[");
			for (int i = 0; i < data.length; i++) {
				int value = data[i] & 0xFF;
				String str = Integer.toHexString(value);
				if (str.length() == 1) {
					str = "0" + str;
				}
				buffer.append(str.toUpperCase()).append(" ,");
				if (i > 0 && i % 16 == 0) {
					buffer.append('\n');
				}
			}
			if (buffer.indexOf(",") > 0) {
				buffer.deleteCharAt(buffer.lastIndexOf(","));
			}
			buffer.append("]");
		}
		return buffer.toString();
	}

	public static byte[] getStringByte(String str, int length) {
		try {
			byte[] data = str.getBytes("UTF-8");
			byte[] resVlue = new byte[length];
			int readLength = data.length > length ? length : data.length;
			System.arraycopy(data, 0, resVlue, 0, readLength);
			return resVlue;
		} catch (Exception e) {

		}
		return new byte[length];
	}

}
