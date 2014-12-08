/**
 * $Header: /data/cvs/TSVDB/src/java/com/gs/core/util/xml/XMLParser.java,v 1.3 2010/07/15 08:45:59 martinliu Exp $ 
 * $Revision: 1.3 $ 
 * $Date: 2010/07/15 08:45:59 $ 
 * 
 * ==================================================================== 
 * 
 * Copyright (c) 2009 Media Data Systems Pte Ltd All Rights Reserved. 
 * This software is the confidential and proprietary information of 
 * Media Data Systems Pte Ltd. You shall not disclose such Confidential 
 * Information. 
 * 
 * ==================================================================== 
 * 
 */
package com.ccagame.protocal.tool.xml;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ccagame.protocal.tool.file.monitor.FileChangeListener;
import com.ccagame.protocal.tool.file.monitor.FileMonitor;

/**
 * The class is use to parse xml file
 *
 * @author Martin Liu
 *
 */

public abstract class XMLParser implements FileChangeListener {

	protected Document doc;

	public final static String XML_ELEMENT_NAME = "function";

	public final static String XML_CELL_NAME = "field";

	private static Logger logger = LoggerFactory.getLogger(XMLParser.class.getName());

	public XMLParser() {
		parseXmlFile();
		parseContent();
		FileMonitor.getInstance().addFileChangeListener(this, getFileName(), getMonitorPeriod());

	}

	/**
	 * parse the xml file 
	 */
	protected void parseXmlFile() {

		SAXBuilder builder = new SAXBuilder(false);
		try {
			doc = builder.build(getFileName());
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
	}

	/**
	 * get the parse file name
	 * @return
	 */
	protected abstract String getFileName();

	protected abstract void parseContent();

	public void fileChanged(String filename) {
		parseXmlFile();
		parseContent();
	}

	/**
	 * get the file change monitor period
	 * @return monitor period
	 */
	protected int getMonitorPeriod() {
		return 60 * 1000;
	}
}
