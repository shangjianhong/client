package com.ccagame.protocal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ccagame.protocal.model.ServerInfo;
import com.ccagame.protocal.tool.DataType;
import com.ccagame.protocal.tool.xml.XMLParser;

public class ProtocalXmlParser extends XMLParser {

	public static final String XML_SERVER = "server";

	public static final String XML_SERVER_ATTR_IP = "ip";

	public static final String XML_SERVER_ATTR_PORT = "port";

	public static final String XML_SERVER_SERVER_ID = "serverId";

	public static final String XML_SERVER_IS_SERVER = "isServer";

	public static final String XML_SERVER_ATTR_IS_LONG_CONNECTION = "isLongConnection";

	public static final String XML_HEADER = "header";

	public static final String XML_HEADER_REQUEST = "requestHeader";

	public static final String XML_HEADER_RESPONSE = "responseHeader";

	public static final String XML_ERROR_RESPONSE = "errorResponseField";

	public static final String XML_PROTOCALS = "protocals";

	public static final String XML_PROTOCAL = "protocal";

	public static final String XML_PROTOCAL_ATTR_COMMAND = "command";

	public static final String XML_PROTOCAL_ATTR_PROCESSCLASS = "processClass";

	public static final String XML_PROTOCAL_REQUEST_FIELD = "requestField";

	public static final String XML_PROTOCAL_RESPONSE_FIELD = "responseField";

	public final static String XML_FIELD = "field";

	public static final String XML_FIELD_ATTR_CODE = "code";

	public static final String XML_FIELD_ATTR_LENGTH = "length";

	public static final String XML_FIELD_ATTR_TYPE = "type";

	public static final String XML_FIELD_ATTR_SEQ = "seq";

	private static Map<String, ServerInfo> serverMap = new HashMap<String, ServerInfo>();

	private static ProtocalXmlParser instances = null;

	private static Logger logger = LoggerFactory.getLogger(ProtocalXmlParser.class);

	public static synchronized ProtocalXmlParser getInstance() {
		if (instances == null) {
			instances = new ProtocalXmlParser();
		}
		return instances;
	}

	private ProtocalXmlParser() {

	}

	/**
	 * get the xml file name
	 */
	protected String getFileName() {
		String fileName = "";
		
		if (System.getProperty("CONFIG_PATH") != null) {
			fileName = System.getProperty("CONFIG_PATH") + "/conf/ProtocalConfig.xml";
		} else {
			fileName = ProtocalXmlParser.class.getClassLoader().getResource("ProtocalConfig.xml").getPath();
		}
		logger.debug("xml file=" + fileName);
		return fileName;
	}

	/**
	 * get import function
	 */
	protected void parseContent() {
		try {
			serverMap.clear();
			Element root = doc.getRootElement();

			//get server information

			List<?> serverElementList = root.getChildren(XML_SERVER);
			if (serverElementList != null && serverElementList.size() > 0) {
				for (int j = 0; j < serverElementList.size(); j++) {
					ServerInfo serverInfo = new ServerInfo();
					Element serverElement = (Element) serverElementList.get(j);
					List<?> serverAttrList = serverElement.getAttributes();
					if (serverAttrList != null && serverAttrList.size() > 0) {
						HashMap<String, String> serverAttr = new HashMap<String, String>();
						for (int i = 0; i < serverAttrList.size(); i++) {
							Attribute attr = (Attribute) serverAttrList.get(i);
							serverAttr.put(attr.getName(), attr.getValue());
						}
						serverInfo.setServerId(serverAttr.get(XML_SERVER_SERVER_ID));
						serverInfo.setAttributes(serverAttr);
						serverMap.put(serverAttr.get(XML_SERVER_SERVER_ID), serverInfo);
					}

					//get header inforamtion
					Element header = serverElement.getChild(XML_HEADER);

					List<ProtocalField> commonReqHeaderList = null;
					List<ProtocalField> commonRespHeaderList = null;
					if (header != null) {
						//request header
						Element reqHeader = header.getChild(XML_HEADER_REQUEST);
						commonReqHeaderList = getFieldList(reqHeader);
						//response header
						Element respHeader = header.getChild(XML_HEADER_RESPONSE);
						commonRespHeaderList = getFieldList(respHeader);
					}

					//get error response field
					Element errorResp = serverElement.getChild(XML_ERROR_RESPONSE);
					List<ProtocalField> commonErrorRespFieldList = null;
					if (errorResp != null) {
						commonErrorRespFieldList = getFieldList(errorResp);

					}

					//get detail protocal
					Element protocals = serverElement.getChild(XML_PROTOCALS);

					if (protocals != null) {
						List<?> protocalList = protocals.getChildren(XML_PROTOCAL);
						Map<String, Protocal> protocalMap = new HashMap<String, Protocal>();
						serverInfo.setProtocalMap(protocalMap);
						for (int i = 0; i < protocalList.size(); i++) {
							Protocal pd = new Protocal();

							pd.setServerInfo(serverInfo);

							Element protocal = (Element) protocalList.get(i);
							String command = protocal.getAttributeValue(XML_PROTOCAL_ATTR_COMMAND);
							String processClass = protocal.getAttributeValue(XML_PROTOCAL_ATTR_PROCESSCLASS);

							//request header
							Element reqHeader = protocal.getChild(XML_HEADER_REQUEST);
							List<ProtocalField> reqHeaderList = commonReqHeaderList;
							if (reqHeader != null) {
								reqHeaderList = getFieldList(reqHeader);
							}

							//response header
							Element respHeader = protocal.getChild(XML_HEADER_RESPONSE);
							List<ProtocalField> respHeaderList = commonRespHeaderList;
							if (respHeader != null) {
								respHeaderList = getFieldList(respHeader);
							}

							//request field
							Element reqField = protocal.getChild(XML_PROTOCAL_REQUEST_FIELD);
							List<ProtocalField> reqFieldList = getFieldList(reqField);

							//response field;
							Element respField = protocal.getChild(XML_PROTOCAL_RESPONSE_FIELD);
							List<ProtocalField> respFieldList = getFieldList(respField);

							//error response
							Element errorRespField = protocal.getChild(XML_ERROR_RESPONSE);
							List<ProtocalField> errorRespFieldList = commonErrorRespFieldList;
							if (errorRespField != null) {
								errorRespFieldList = getFieldList(errorRespField);
							}

							//set value
							pd.setCommand(command);
							pd.setProcessClass(processClass);
							if (reqHeaderList != null) {
								pd.setRequestHeader(reqHeaderList);
							} else {
								pd.setRequestHeader(commonReqHeaderList);
							}
							if (respHeaderList != null) {
								pd.setResponseHeader(respHeaderList);
							} else {
								pd.setResponseHeader(commonRespHeaderList);
							}

							pd.setRequestBody(reqFieldList);
							pd.setResponseBody(respFieldList);
							pd.setErrorResponseBody(errorRespFieldList);

							protocalMap.put(pd.getCommand(), pd);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);

		}

	}

	private List<ProtocalField> getFieldList(Element element) throws Exception {
		List<ProtocalField> fl = new ArrayList<ProtocalField>();
		if (element != null) {
			List<?> fieldList = element.getChildren(XML_FIELD);
			if (fieldList != null && fieldList.size() > 0) {
				for (int i = 0; i < fieldList.size(); i++) {
					Map<String, String> attrMap = new HashMap<String, String>();
					ProtocalField pf = new ProtocalField();
					Element fieldElement = (Element) fieldList.get(i);
					String code = fieldElement.getAttributeValue(XML_FIELD_ATTR_CODE);
					Integer length = fieldElement.getAttribute(XML_FIELD_ATTR_LENGTH).getIntValue();
					Integer type = fieldElement.getAttribute(XML_FIELD_ATTR_TYPE).getIntValue();
					Integer seq = fieldElement.getAttribute(XML_FIELD_ATTR_SEQ).getIntValue();
					List<?> attributes = fieldElement.getAttributes();
					for (int j = 0; j < attributes.size(); j++) {
						Attribute attribute = (Attribute) attributes.get(j);
						attrMap.put(attribute.getName(), attribute.getValue());
					}

					List<ProtocalField> children = getFieldList(fieldElement);
					if (children != null && children.size() > 0) {
						if (type != DataType.DT_INTEGER) {
							throw new Exception("the field [" + pf.toString() + "] have child,the type should be int,"
									+ " this field is used to store the loop count of the child");
						} else {
							pf.setChildren(children);
						}
					}

					pf.setCode(code);
					pf.setLength(length);
					pf.setType(type);
					pf.setSeq(seq);
					pf.setAttributes(attrMap);
					fl.add(pf);

				}
			}

		}
		Collections.sort(fl);
		return fl;
	}

	/**
	 * get server information by id;
	 * @param serverId
	 * @return
	 */
	public ServerInfo getServerInfo(String serverId) {
		return serverMap.get(serverId);
	}

	/**
	 * get all server information
	 * @return
	 */
	public Map<String, ServerInfo> getAllServerInfo() {
		return serverMap;
	}

}
