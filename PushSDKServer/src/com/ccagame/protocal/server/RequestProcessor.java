package com.ccagame.protocal.server;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ccagame.protocal.Protocal;
import com.ccagame.protocal.ProtocalField;
import com.ccagame.protocal.ProtocalUtil;
import com.ccagame.protocal.Protocalable;
import com.ccagame.protocal.model.ProtocalData;
import com.ccagame.protocal.model.RequestData;
import com.ccagame.protocal.model.ResponseData;
import com.ccagame.protocal.model.ServerInfo;
import com.ccagame.protocal.tool.DataConvert;

public class RequestProcessor {

	private static final Logger logger = LoggerFactory.getLogger(RequestProcessor.class);

	private ProtocalData pd;

	private String serverId;

	public RequestProcessor(byte[] data, String serverId) {
		pd = new ProtocalData(data);
		this.serverId = serverId;

	}

	public byte[] process() {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			Integer command = getCommand();
			Integer version = getVersion();
			Protocal protocal = ProtocalUtil.getServerInfo(serverId).getProtocal(String.valueOf(command));
			if (protocal != null) {
				// create new request data object
				RequestData reqData = new RequestData();

				reqData.setCommand(command);

				// get the request header field list
				List<ProtocalField> headerFieldList = protocal.getRequestHeader();

				// parse the header valuse and save it to a map object
				Map<String, Object> headerData = parseHeader(headerFieldList);
				reqData.setHeader(headerData);

				logger.debug("request header data to be process is: " + DataConvert.byteToHexString(pd.getHeaderData()));

				// get the request body list
				List<ProtocalField> bodyFieldList = protocal.getRequestBody();

				// parse the header valuse and save it to a map object
				Map<String, Object> bodyData = parseBody(bodyFieldList);
				reqData.setBody(bodyData);

				logger.debug("request body data to be process is: " + DataConvert.byteToHexString(pd.getBodyData()));

				if (pd.hasNextRequestData()) {
					logger.debug("more requset command ");
					byte[] nextRequestData = pd.getNextRequestData();
					byte[] nextResponseData = new RequestProcessor(nextRequestData, serverId).process();
					baos.write(nextResponseData);
				}
				Protocalable requestPprocessor = ProtocalUtil.getProcessImp(protocal, version);
				ResponseData respData = requestPprocessor.procssRequest(reqData);

				ResponseProcessor responseProcessor = new ResponseProcessor(reqData, respData, serverId);
				byte[] responseData = responseProcessor.process();
				baos.write(responseData);
				return baos.toByteArray();

			} else {
				logger.warn("Can not get the protocal by command [" + command + "] server id=[" + serverId + "]");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		return null;

	}

	public List<RequestData> processToRequestData() {
		List<RequestData> dataList = new ArrayList<RequestData>();
		try {
			Integer command = getCommand();
			Protocal protocal = ProtocalUtil.getServerInfo(serverId).getProtocal(String.valueOf(command));
			if (protocal != null) {
				// create new request data object
				RequestData reqData = new RequestData();

				reqData.setCommand(command);

				// get the request header field list
				List<ProtocalField> headerFieldList = protocal.getRequestHeader();

				// parse the header valuse and save it to a map object
				Map<String, Object> headerData = parseHeader(headerFieldList);
				reqData.setHeader(headerData);

				logger.debug("request header data to be process is: " + DataConvert.byteToHexString(pd.getHeaderData()));

				// get the request body list
				List<ProtocalField> bodyFieldList = protocal.getRequestBody();

				// parse the header valuse and save it to a map object
				Map<String, Object> bodyData = parseBody(bodyFieldList);
				reqData.setBody(bodyData);

				logger.debug("request body data to be process is: " + DataConvert.byteToHexString(pd.getBodyData()));

				if (pd.hasNextRequestData()) {
					logger.debug("more requset command ");
					byte[] nextRequestData = pd.getNextRequestData();
					List<RequestData> requestData = new RequestProcessor(nextRequestData, serverId)
							.processToRequestData();
					dataList.addAll(requestData);
				}
				dataList.add(reqData);

			} else {
				logger.warn("Can not get the protocal by command [" + command + "] server id=[" + serverId + "]");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		return dataList;
	}

	/**
	 * parse header fields
	 * 
	 * @param fieldList
	 *            header fields
	 * @return the header value from client
	 * @throws Exception
	 */
	private Map<String, Object> parseHeader(List<ProtocalField> fieldList) throws Exception {
		int startPos = pd.getPosition();
		Map<String, Object> data = parseContent(fieldList);
		int headerLength = pd.getPosition() - startPos;
		byte[] headerData = new byte[headerLength];
		System.arraycopy(pd.getData(), startPos, headerData, 0, headerData.length);
		pd.setHeaderData(headerData);
		pd.setHeaderLength(headerLength);
		return data;

	}

	/**
	 * parse body fields
	 * 
	 * @param fieldList
	 *            body fields
	 * @return the body value from client
	 * @throws Exception
	 */
	private Map<String, Object> parseBody(List<ProtocalField> fieldList) throws Exception {
		int startPos = pd.getPosition();
		Map<String, Object> data = parseContent(fieldList);
		int bodyLength = pd.getPosition() - startPos;
		byte[] bodyData = new byte[bodyLength];
		System.arraycopy(pd.getData(), startPos, bodyData, 0, bodyData.length);
		pd.setBodyData(bodyData);
		pd.setBodyLength(bodyLength);
		return data;

	}

	/**
	 * parse the content
	 * 
	 * @param fieldList
	 *            the field need to convert
	 * @return the value from clinet
	 * @throws Exception
	 */
	private Map<String, Object> parseContent(List<ProtocalField> fieldList) throws Exception {
		Map<String, Object> data = new HashMap<String, Object>();
		if (fieldList != null && fieldList.size() > 0) {
			for (int i = 0; i < fieldList.size(); i++) {
				ProtocalField pf = fieldList.get(i);

				if (pf.getSubCmd() != -1 && data.get(pf.getCode()) != null) {
					continue;
				}

				if (getVersion() < pf.getVersion()) {
					logger.debug("the field version is large than parse version field verion=[" + pf.getVersion()
							+ "] current version=[" + getVersion() + "]");
					continue;
				}

				String code = pf.getCode();
				Object value = DataConvert.getOject(pd, pf);
				logger.debug("parse field code=" + pf.getCode() + ", value=" + value);
				if (pf.getSubCmd() != -1 && pf.getSubCmd() != value) {
					pd.position = pd.position - pf.getLength();
					continue;
				}

				data.put(code, value);
				if (value == pf.getSubCmd()) {
					data.put("subCmd", value);
				}

				if (pf.getSubCmd() == -1) {
					List<ProtocalField> children = pf.getChildren();
					if (children != null && children.size() > 0) {
						int count = (Integer) value;
						List<Map<String, Object>> childrenData = new ArrayList<Map<String, Object>>();
						for (int j = 0; j < count; j++) {
							childrenData.add(parseContent(children));
						}

						data.put(code, childrenData);
					}
				} else if (value == pf.getSubCmd()) {
					List<ProtocalField> children = pf.getChildren();
					if (children != null && children.size() > 0) {
						for (int j = 0; j < children.size(); j++) {
							ProtocalField childField = children.get(j);
							Object childValue = DataConvert.getOject(pd, childField);
							if (childField.getChildren() != null) {
								List<Map<String, Object>> childData = new ArrayList<Map<String, Object>>();
								for (int m = 0; m < (Integer) childValue; m++) {
									childData.add(parseContent(childField.getChildren()));
								}
								data.put(childField.getCode(), childData);
							} else {
								data.put(childField.getCode(), childValue);
							}
							logger.debug("parse field code=" + childField.getCode() + ", value=" + childValue);
						}
					}
				}

			}
		}
		return data;

	}

	/**
	 * get command from request data
	 * 
	 * @param data
	 * @return
	 * @throws KKException
	 */
	public Integer getCommand() throws Exception {
		ServerInfo si = ProtocalUtil.getServerInfo(serverId);
		int startPos = si.getCommandStartPos();
		int length = si.getCommandLength();
		if (pd.getData().length >= startPos + length) {
			// command
			return DataConvert.getIntData(pd.getData(), startPos, length);
		} else {
			throw new Exception("the request data size is wrong, can not get the command");
		}

	}

	public Integer getVersion() throws Exception {
		ServerInfo si = ProtocalUtil.getServerInfo(serverId);
		int startPos = si.getVersionStartPos();
		int length = si.getVersionLength();
		if (length > 0) {
			if (pd.getData().length >= startPos + length) {
				// command
				return DataConvert.getIntData(pd.getData(), startPos, length);
			} else {
				throw new Exception("the request data size is wrong, can not get the command");
			}
		} else {
			return 0;
		}
	}

}
