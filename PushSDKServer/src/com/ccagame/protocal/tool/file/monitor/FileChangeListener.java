/**
 * $Header: /data/cvs/TSVDB/src/java/com/gs/core/util/file/monitor/FileChangeListener.java,v 1.2 2009/12/04 06:48:26 martinliu Exp $ 
 * $Revision: 1.2 $ 
 * $Date: 2009/12/04 06:48:26 $ 
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

package com.ccagame.protocal.tool.file.monitor;

/**
 * @author Martin Liu
 * @version $Id: FileChangeListener.java,v 1.2 2009/12/04 06:48:26 martinliu Exp $
 */
public interface FileChangeListener
{
	/**
	 * Invoked when a file changes
	 * 
	 * @param filename Name of the changed file
	 */
	public void fileChanged(String filename);
}
