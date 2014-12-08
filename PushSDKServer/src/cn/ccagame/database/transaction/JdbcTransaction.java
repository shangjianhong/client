package cn.ccagame.database.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import cn.ccagame.database.DBAccessor;

/**
 * Thread-bound JDBC transaction.
 * 
 * @author Martin Liu
 */
public class JdbcTransaction implements Transaction {
	private Connection conn;
	
	private DBAccessor accessor;
	
	private int status = INIT_STATE;
	
	private static Logger logger = Logger.getLogger(JdbcTransaction.class.getName());
	
	public JdbcTransaction(DBAccessor accessor) {
		this.accessor = accessor;
		try {
			conn = accessor.getConnection();
		} catch (SQLException e) {
			logger.warning("Error occurs when getting connection! " + e.getMessage());
			
		}
	}
	
	/**
	 * Begin the transaction.
	 */
	public void begin() {
		logger.fine("begin transaction");
		try {
			// Disabe the JDBC autocommit mode.
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
				logger.fine("disabling autocommit and begin a transaction.");
			}
		} catch (SQLException e) {
			logger.warning("begin failed. " + e.getMessage());
			
		}
		status = BEGIN_STATE;
	}
	
	/**
	 * Commit the transaction.
	 */
	public void commit() {
		if (status != BEGIN_STATE) {
			logger.warning("Transaction not successfully started");
		}
		logger.fine("commit transaction");
		try {
			conn.commit();
			logger.fine("enable autocommit and commit a transaction.");
			status = COMMIT_STATE;
		} catch (SQLException e) {
			logger.warning("Commit failed. " + e.getMessage());
			status = COMMIT_FAILED_STATE;
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (Exception e) {
				logger.warning("error when set auto commit mode to true. " + e.getMessage());
			}
			accessor.closeConnection(conn);
			
		}
	}
	
	/**
	 * Rollback the transaction.
	 */
	public void rollback() {
		if (status != BEGIN_STATE) {
			logger.warning("Transaction not successfully started");
		}
		logger.fine("rollback transaction");
		if (status != COMMIT_FAILED_STATE) {
			try {
				conn.rollback();
				status = ROLLBACK_STATE;
			} catch (SQLException e) {
				logger.warning("Rollback failed." + e.getMessage());
				
			} finally {
				try {
					conn.setAutoCommit(true);
				} catch (Exception e) {
					logger.warning("error when set auto commit mode to true." + e.getMessage());
				}
				accessor.closeConnection(conn);
			}
		}
		status = ROLLBACK_STATE;
		
	}
	
	/**
	 * Get the status of the transaction instance.
	 */
	public int getStatus() {
		return this.status;
	}
	
}
