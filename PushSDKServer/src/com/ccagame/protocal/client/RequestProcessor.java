package com.ccagame.protocal.client;

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
import com.ccagame.protocal.model.ServerInfo;
import com.ccagame.protocal.tool.DataConvert;

public class RequestProcessor {

	private static final Logger logger = LoggerFactory.getLogger(RequestProcessor.class);

	private RequestData reqData;

	private String serverId;

	private Integer version;

	public RequestProcessor(RequestData reqData, String serverId) {
		this.reqData = reqData;
		this.serverId = serverId;
		Object obj = reqData.getHeader().get("version");
		if (obj == null) {
			version = 1;
		} else {
			version = (Integer) obj;
			if (version == 0) {
				version = 1;
			}
		}
	}

	public byte[] process() throws Exception {
		Protocal protocal = ProtocalUtil.getServerInfo(serverId).getProtocal(String.valueOf(reqData.getCommand()));
		List<ProtocalField> reqHeaderList = protocal.getRequestHeader();
		List<ProtocalField> reqBodyList = protocal.getRequestBody();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Map<String, Object> headerData = reqData.getHeader();
		// write header
		for (int i = 0; i < reqHeaderList.size(); i++) {
			ProtocalField pf = reqHeaderList.get(i);
			convertData(baos, pf, headerData);
		}

		// write body
		Map<String, Object> bodyData = reqData.getBody();

		for (int i = 0; i < reqBodyList.size(); i++) {
			ProtocalField pf = reqBodyList.get(i);
			convertData(baos, pf, bodyData);
		}

		return updateTotalLength(baos);

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

		logger.debug("convert data, code=" + pf.getCode() + ",value=" + obj);

		List<ProtocalField> childrenFieldList = pf.getChildren();
		if (childrenFieldList != null && childrenFieldList.size() > 0) {
			if (pf.getSubCmd() == -1) {
				logger.debug("field code=" + pf.getCode() + " has children");

				List<Map<String, Object>> childrenData = (List<Map<String, Object>>) data.get(pf.getCode());
				int count = childrenData.size();
				baos.write(DataConvert.intToByteArray(count, pf.getLength()));
				for (int i = 0; i < childrenData.size(); i++) {
					for (int j = 0; j < childrenFieldList.size(); j++) {
						ProtocalField childrenField = childrenFieldList.get(j);
						convertData(baos, childrenField, childrenData.get(i));
					}
				}
			} else {
				byte[] value = DataConvert.convertObject(obj, pf);
				if (obj == pf.getSubCmd()) {
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

}
