/**
 * $Header: /data/cvs/TSVDB/src/java/com/gs/core/util/database/connection/BatchJobConnectionManagerImpl.java,v 1.2 2009/12/04 06:48:26 martinliu Exp $ 
 * $Revision: 1.2 $ 
 * $Date: 2009/12/04 06:48:26 $ 
 * 
 * ==================================================================== 
 * 
 * Copyright (c) 2006 Media Data Systems Pte Ltd All Rights Reserved. 
 * This software is the confidential and proprietary information of 
 * Media Data Systems Pte Ltd. You shall not disclose such Confidential 
 * Information. 
 * 
 * ==================================================================== 
 * 
 */

package cn.ccagame.database.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Logger;

import cn.ccagame.util.SystemConfig;

/**
 * This class use to implement the connection management for batch job.
 * 
 * @author Jimmy Shi
 * @version $Id: BatchJobConnectionManagerImpl.java,v 1.2 2009/12/04 06:48:26 martinliu Exp $
 */
public class JdbcConnectionManagerImpl implements ConnectionManager {

	private static Logger log = Logger.getLogger(JdbcConnectionManagerImpl.class.getName());

	/**
	 * Get a connection by specify connection string.
	 * @return Return a connection.
	 */
	public Connection getConnection() {
		Connection conn = null;
		String dbResource = String.valueOf(SystemConfig.getProperty("jdbc_url"));

		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(dbResource);
		} catch (Throwable ex) {
			log.severe("Errors occurs when get connection at url:" + dbResource + "\n" + ex);
		}
		return conn;
	}
}
