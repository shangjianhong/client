package cn.ccagame.database;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import cn.ccagame.database.connection.ConnectionManager;
import cn.ccagame.database.connection.DataSourceConnectionManagerImpl;
import cn.ccagame.database.transaction.JdbcTransaction;
import cn.ccagame.database.transaction.Transaction;

/**
 * Jdbc Helper class for JDBC operation.
 * 
 * @author Martin Liu
 */
public class JdbcHelper {
	private static Map<ConnectionManager, DBAccessor> dbAccessorMap = Collections
			.synchronizedMap(new HashMap<ConnectionManager, DBAccessor>());

	private static Logger logger = Logger.getLogger(JdbcHelper.class.getName());

	/**
	 * Get the DBAccessor instance for default jboss datasource.
	 * 
	 * @return DBAccessor
	 * @see DBAccessor getDBAccessor(String dsJndiName)
	 */
	public static DBAccessor getDBAccessor() {
		return getDBAccessor(new DataSourceConnectionManagerImpl("java:comp/env/jdbc/gamesdk"));
	}

	/**
	 * Get the DBAccessor instance by assingn specified ConnectionManager.
	 * 
	 * @param conProvider
	 * @return DBAccessor
	 * @see DBAccessor getDBAccessor(String dsJndiName)
	 */
	public static DBAccessor getDBAccessor(ConnectionManager conProvider) {
		DBAccessor accessor;
		if (dbAccessorMap.get(conProvider) == null) {
			accessor = new DBAccessor(conProvider);
			dbAccessorMap.put(conProvider, accessor);
		} else {
			accessor = (DBAccessor) dbAccessorMap.get(conProvider);
		}
		logger.fine("get DBAccessor=" + accessor + " ConnectionManager=" + conProvider);
		return accessor;
	}

	/**
	 * Get the default transaction object.
	 * 
	 * @return Transaction object
	 * @see getTransaction(String dsJndiName)
	 */
	public static Transaction getTransaction() {
		DBAccessor accessor = JdbcHelper.getDBAccessor();
		return new JdbcTransaction(accessor);
	}


	/**
	 * Get transaction by assign specified ConnectionManager.
	 * 
	 * @param conProvider
	 * @return Transaction
	 */
	public static Transaction getTransaction(ConnectionManager conProvider) {
		DBAccessor accessor = JdbcHelper.getDBAccessor(conProvider);
		return new JdbcTransaction(accessor);
	}
}
