package com.ccagame.protocal.tool;

import com.ccagame.protocal.ProtocalUtil;
import com.ccagame.protocal.model.ServerInfo;

public class CommandUtil {

	/**
	 * get command from request data
	 * 
	 * @param data
	 * @return
	 * @throws KKException
	 */
	public static Integer getCommand(byte[] data, String serverId) throws Exception {
		ServerInfo serverInfo = ProtocalUtil.getServerInfo(serverId);
		int startPos = serverInfo.getCommandStartPos();
		int length = serverInfo.getCommandLength();
		if (data.length >= startPos + length) {
			return DataConvert.getIntData(data, startPos, length);
		} else {
			throw new Exception("the response data size is wrong, can not get the command");
		}

	}

	public static Integer getResponse(byte[] data, String serverId) throws Exception {
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

	public static Integer getVersion(byte[] data, String serverId) throws Exception {
		ServerInfo si = ProtocalUtil.getServerInfo(serverId);
		int startPos = si.getVersionStartPos();
		int length = si.getVersionLength();
		if (length > 0) {
			if (data.length >= startPos + length) {
				// command
				int version = DataConvert.getIntData(data, startPos, length);
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

	public static Integer getPacketLength(byte[] data, String serverId) throws Exception {
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
