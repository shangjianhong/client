package com.ccagame.protocal;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ccagame.protocal.model.ServerInfo;

/**
 * The Class ReportUtil.
 */
public class ProtocalUtil {
	/** The log. */
	private static final Logger logger = LoggerFactory.getLogger(ProtocalUtil.class.getName());

	/**
	 * Instantiates a new report util.
	 */
	private ProtocalUtil() {

	}

	/** The instance. */
	private static ProtocalUtil instance;

	/**
	 * Gets the single instance of ReportUtil.
	 * 
	 * @return single instance of ReportUtil
	 */
	public synchronized static ProtocalUtil getInstance() {

		if (instance == null) {
			instance = new ProtocalUtil();
		}
		return instance;

	}

	public static Protocalable getProcessImp(Protocal protocal, Integer version) throws Exception {
		try {
			String processClass = protocal.getProcessClass();
			if (version > 1) {
				processClass = processClass + "V" + version;
			}
			Class<?> clazz = null;
			try {
				clazz = Class.forName(processClass);
			} catch (Exception e) {
				processClass = protocal.getProcessClass();
				clazz = Class.forName(processClass);
			}
			try {

				@SuppressWarnings("rawtypes")
				Constructor constructor = clazz.getConstructor(String.class);
				Protocalable reportable = (Protocalable) constructor
						.newInstance(protocal.getServerInfo().getServerId());
				return reportable;
			} catch (Exception e) {
				Protocalable reportable = (Protocalable) clazz.newInstance();
				return reportable;
			}

		} catch (Exception e) {
			logger.warn("Can't load Reportable class : " + protocal.getProcessClass() + ", error" + e);
			throw new Exception("Can't load protocal class");
		}
	}

	public static List<ServerInfo> getServerInfo(boolean isServer) {
		List<ServerInfo> result = new ArrayList<ServerInfo>();

		Map<String, ServerInfo> servertInfo = ProtocalXmlParser.getInstance().getAllServerInfo();
		Iterator<ServerInfo> serverInfoMap = servertInfo.values().iterator();
		while (serverInfoMap.hasNext()) {
			ServerInfo server = serverInfoMap.next();
			if (isServer) {
				if ("true".equalsIgnoreCase(server.getAttributes().get(ProtocalXmlParser.XML_SERVER_IS_SERVER))) {
					result.add(server);
				}
			} else {
				if (!"true".equalsIgnoreCase(server.getAttributes().get(ProtocalXmlParser.XML_SERVER_IS_SERVER))) {
					result.add(server);
				}
			}
		}
		return result;
	}

	/**
	 * get server infomation by server id
	 * @param serverId
	 * @return
	 */
	public static ServerInfo getServerInfo(String serverId) {
		return ProtocalXmlParser.getInstance().getServerInfo(serverId);
	}

}