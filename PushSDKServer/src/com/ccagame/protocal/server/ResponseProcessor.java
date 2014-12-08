package com.ccagame.protocal.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ccagame.protocal.Protocal;
import com.ccagame.protocal.ProtocalField;
import com.ccagame.protocal.ProtocalUtil;
import com.ccagame.protocal.model.RequestData;
import com.ccagame.protocal.model.ResponseData;
import com.ccagame.protocal.model.ServerInfo;
import com.ccagame.protocal.tool.DataConvert;

public class ResponseProcessor {

	private static final Logger logger = LoggerFactory.getLogger(ResponseProcessor.class);

	private RequestData reqData;

	private ResponseData respData;

	private String serverId;

	private Integer version;

	public ResponseProcessor(RequestData reqData, ResponseData respData, String serverId) {
		this.reqData = reqData;
		this.respData = respData;
		this.serverId = serverId;

		Object obj = null;
		if (respData.getData() != null) {
			obj = respData.getData().get("version");
			if (obj == null) {
				obj = reqData.getHeader().get("version");
			}
		}
		if (obj == null) {
			version = 1;
		} else {
			version = (Integer) obj;
		}
	}

	public byte[] process() throws Exception {
		Protocal protocal = ProtocalUtil.getServerInfo(serverId).getProtocal(String.valueOf(reqData.getCommand()));
		List<ProtocalField> respHeaderList = protocal.getResponseHeader();
		List<ProtocalField> respBodyList = protocal.getResponseBody();
		List<ProtocalField> errorRespBodyList = protocal.getErrorResponseBody();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Map<String, Object> data = respData.getData();
		if (!data.containsKey("responseCode")) {
			data.put("responseCode", respData.getResponseCode());
		}
		// write header
		for (int i = 0; i < respHeaderList.size(); i++) {
			ProtocalField pf = respHeaderList.get(i);
			Object obj = null;
			if (data != null) {
				obj = data.get(pf.getCode());
				if (obj == null) {
					obj = reqData.getHeader().get(pf.getCode());
				}
			}
			convertData(baos, pf, data, obj);
		}

		// write body

		if (respData.getResponseCode() == 0) {
			for (int i = 0; i < respBodyList.size(); i++) {
				ProtocalField pf = respBodyList.get(i);
				convertData(baos, pf, data);
			}
		} else {
			for (int i = 0; i < errorRespBodyList.size(); i++) {
				ProtocalField pf = errorRespBodyList.get(i);
				convertData(baos, pf, data);
			}
		}
		byte[] dataTmp = updateTotalLength(baos);
		logger.debug("response data= " + DataConvert.byteToHexString(dataTmp));
		return dataTmp;
	}

	private void convertData(ByteArrayOutputStream baos, ProtocalField pf, Map<String, Object> data) throws Exception {
		convertData(baos, pf, data, null);
	}

	@SuppressWarnings("unchecked")
	private void convertData(ByteArrayOutputStream baos, ProtocalField pf, Map<String, Object> data, Object obj)
			throws Exception {

		if (version < pf.getVersion()) {
			logger.debug("the field version is large than parse version field verion=[" + pf.getVersion()
					+ "] current version=[" + version + "]");
			return;

		}
		if (obj == null) {
			if (data != null) {
				obj = data.get(pf.getCode());
			}
		}
		logger.debug("parse field code=" + pf.getCode() + ", value=" + obj);

		List<ProtocalField> childrenFieldList = pf.getChildren();
		if (childrenFieldList != null && childrenFieldList.size() > 0) {
			if (pf.getSubCmd() == -1) {
				List<Map<String, Object>> childrenData = (List<Map<String, Object>>) data.get(pf.getCode());
				if (childrenData != null) {
					int count = childrenData.size();
					baos.write(DataConvert.intToByteArray(count, pf.getLength()));
					for (int i = 0; i < childrenData.size(); i++) {
						for (int j = 0; j < childrenFieldList.size(); j++) {
							ProtocalField childrenField = childrenFieldList.get(j);
							convertData(baos, childrenField, childrenData.get(i));
						}
					}
				} else {
					baos.write(DataConvert.intToByteArray(0, pf.getLength()));
				}
			} else {
				if (obj == pf.getSubCmd()) {
					byte[] value = DataConvert.convertObject(obj, pf);
					baos.write(value);
					for (int j = 0; j < childrenFieldList.size(); j++) {
						ProtocalField childrenField = childrenFieldList.get(j);
						convertData(baos, childrenField, data);
					}
				}
			}
		} else {
			byte[] value = DataConvert.convertObject(obj, pf);
			baos.write(value);
		}
	}

	/**
	 * update the header length field valuebyte length
	 * 
	 * @param baos
	 * @return
	 * @throws IOException
	 */
	public byte[] updateTotalLength(ByteArrayOutputStream baos) throws IOException {
		ServerInfo si = ProtocalUtil.getServerInfo(serverId);
		byte[] totalLenth = DataConvert.intToByteArray(baos.size(), si.getLengthLength());

		try {
			baos.flush();
		} catch (Exception e) {

		}
		byte[] data = baos.toByteArray();
		System.arraycopy(totalLenth, 0, data, 0, si.getLengthLength());
		baos.close();
		return data;
	}

	public static void main(String[] args) {

	}

}
