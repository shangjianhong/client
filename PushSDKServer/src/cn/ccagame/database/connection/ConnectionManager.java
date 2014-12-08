package cn.ccagame.database.connection;

import java.sql.Connection;

/**
 * The interface includes the interface of the connection manager.
 * 
 * @author Martin Liu
 */
public interface ConnectionManager {

	/**
	 * Get a DB connection by specify connection string.
	 * 
	 * @return Return a DB connection instance.
	 */
	public Connection getConnection();

}
