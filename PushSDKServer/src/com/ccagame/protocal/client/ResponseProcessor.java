package com.ccagame.protocal.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ccagame.protocal.Protocal;
import com.ccagame.protocal.ProtocalField;
import com.ccagame.protocal.ProtocalUtil;
import com.ccagame.protocal.model.ProtocalData;
import com.ccagame.protocal.model.RequestData;
import com.ccagame.protocal.model.ResponseData;
import com.ccagame.protocal.model.ServerInfo;
import com.ccagame.protocal.tool.DataConvert;

public class ResponseProcessor {

	private static final Logger logger = LoggerFactory.getLogger(ResponseProcessor.class);

	private ProtocalData pd;

	private String serverId;

	public ResponseProcessor(byte[] data, String sreverId) {
		pd = new ProtocalData(data);
		this.serverId = sreverId;

	}

	public List<ResponseData> process() {

		logger.debug("data response from server:=" + DataConvert.byteToHexString(pd.getData()));

		List<ResponseData> respDataList = new ArrayList<ResponseData>();
		try {
			Integer command = getCommand(pd.getData());
			Protocal protocal = ProtocalUtil.getServerInfo(serverId).getProtocal(String.valueOf(command));

			if (protocal != null) {
				// create new request data object
				RequestData reqData = new RequestData();

				reqData.setCommand(command);

				// get the request header field list
				List<ProtocalField> headerFieldList = protocal.getResponseHeader();

				// parse the header valuse and save it to a map object
				Map<String, Object> headerData = parseHeader(headerFieldList);
				reqData.setHeader(headerData);

				logger.debug("request header data to be process is" + DataConvert.byteToHexString(pd.getHeaderData()));
				Object responseCodeObj = headerData.get("responseCode");
				int responseCode = 0;
				if (responseCodeObj == null) {
					responseCode = getResponse(pd.getData());
				} else {
					responseCode = (Integer) responseCodeObj;
				}

				// get the request body list
				List<ProtocalField> bodyFieldList = null;
				if (responseCode == 0) {
					bodyFieldList = protocal.getResponseBody();
				} else {
					bodyFieldList = protocal.getErrorResponseBody();
				}

				// parse the header valuse and save it to a map object
				Map<String, Object> bodyData = parseBody(bodyFieldList);
				reqData.setBody(bodyData);
				Map<String, Object> respData = new HashMap<String, Object>();
				respData.putAll(headerData);
				respData.putAll(bodyData);
				ResponseData rp = new ResponseData();
				rp.setData(respData);
				rp.setResponseCode(responseCode);
				respDataList.add(rp);
				logger.debug("request body data to be process is" + DataConvert.byteToString(pd.getBodyData()));

				Integer packetLength = getPacketLength(pd.getData());
				if (responseCode != 0 && pd.hasNextRequestData() && packetLength > 0) {
					pd.position = packetLength;
				}

				if (pd.hasNextRequestData()) {
					logger.debug("more requset command ");
					byte[] nextRequestData = pd.getNextRequestData();
					List<ResponseData> nextRp = new ResponseProcessor(nextRequestData, this.serverId).process();
					respDataList.addAll(nextRp);
				}

			} else {
				logger.warn("Can not get the protocal by command [" + command + "] server id=[" + serverId + "]");
			}

		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
		return respDataList;

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

				logger.debug("parse field code=" + pf.getCode());

				if (getVersion() < pf.getVersion()) {
					logger.debug("the field version is large than parse version field verion=[" + pf.getVersion()
							+ "] current version=[" + getVersion() + "]");
					continue;
				}

				String code = pf.getCode();
				Object value = DataConvert.getOject(pd, pf);
				logger.debug("value=" + value);
				if (pf.getSubCmd() != -1 && pf.getSubCmd() != value) {
					pd.position = pd.position - pf.getLength();
					continue;
				}

				if (code.equals("im_responseData")) {
					ResponseProcessor resp = new ResponseProcessor((byte[]) value, serverId);
					List<ResponseData> responseData = resp.process();
					data.put(code, responseData);
				} else if (code.equals("im_iqId")) {
					data.put("id", value);
				} else if (code.equals("im_rid")) {
					data.put("rid", value);
				} else if (code.equals("im_command")) {
					data.put("command", value);
				} else {
					data.put(code, value);
				}
				// it is not sub cmd
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
					List<ProtocalField> childrenField = pf.getChildren();
					Map<String, Object> childrenData = parseBody(childrenField);
					data.putAll(childrenData);
					data.put("subCmdValue", value);
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
	private Integer getCommand(byte[] data) throws Exception {
		ServerInfo serverInfo = ProtocalUtil.getServerInfo(serverId);
		int startPos = serverInfo.getCommandStartPos();
		int length = serverInfo.getCommandLength();
		if (data.length >= startPos + length) {
			return DataConvert.getIntData(data, startPos, length);
		} else {
			throw new Exception("the response data size is wrong, can not get the command");
		}

	}

	private Integer getResponse(byte[] data) throws Exception {
		ServerInfo serverInfo = ProtocalUtil.getServerInfo(serverId);
		int startPos = serverInfo.getResponseCodeStartPos();
		int length = serverInfo.getResponseCodeLength();
		if (length > 0) {
			if (data.length >= startPos + length) {
				return DataConvert.getIntData(data, startPos, length);
			} else {
				throw new Exception("the response data size is wrong, can not get the responseCode");
			}
		} else {
			return 0;
		}
	}

	private Integer getVersion() throws Exception {
		ServerInfo si = ProtocalUtil.getServerInfo(serverId);
		int startPos = si.getVersionStartPos();
		int length = si.getVersionLength();
		if (length > 0) {
			if (pd.getData().length >= startPos + length) {
				// command
				int version = DataConvert.getIntData(pd.getData(), startPos, length);
				if (version == 0) {
					version = 1;
				}
				return version;
			} else {
				throw new Exception("the request data size is wrong, can not get the command");
			}
		} else {
			return 1;
		}
	}

	private Integer getPacketLength(byte[] data) throws Exception {
		ServerInfo serverInfo = ProtocalUtil.getServerInfo(serverId);
		int startPos = serverInfo.getLengthStartPos();
		int length = serverInfo.getLengthLength();
		if (data.length >= startPos + length) {
			return DataConvert.getIntData(data, startPos, length);
		} else {
			throw new Exception("the response data size is wrong, can not get the command");
		}
	}

}
