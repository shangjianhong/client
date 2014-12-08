package cn.ccagame.database;

import java.io.IOException;
import java.io.Writer;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.naming.NamingException;

import cn.ccagame.database.connection.ConnectionManager;
import cn.ccagame.database.mapper.DBResultSetProcessor;
import cn.ccagame.database.mapper.DefaultDBResultSetProcessor;
import cn.ccagame.database.support.BatchPreparedStatementSetter;
import cn.ccagame.database.support.DataPage;

import com.ccagame.protocal.tool.PubUtil;

/**
 * Class for database accessor.
 * 
 * @author Martin Liu
 */
public class DBAccessor {

	private static String SEPERATOR = "?";

	protected ConnectionManager connMng = null;

	private final ThreadLocal<Connection> dbConnThreadLocal = new ThreadLocal<Connection>();

	private static Logger logger = Logger.getLogger(DBAccessor.class.getName());

	protected DBAccessor(ConnectionManager connManagerImpl) {
		this.connMng = connManagerImpl;
	}

	/**
	 * Get connection from DB. If bounded thread has a connection already, reuse
	 * it, else get a new connection from datasource.
	 * 
	 * @return Connection
	 */

	public Connection getConnection() throws SQLException {
		Connection conn = (Connection) dbConnThreadLocal.get();
		if (conn != null && !conn.isClosed()) {
			logger.fine("Get connection from ThreadLocal,bounded thread=" + Thread.currentThread());
		} else {
			conn = connMng.getConnection();
			logger.fine("Get a new connection from DataSourceConnectionProvider,bounded thread="
					+ Thread.currentThread());
			dbConnThreadLocal.set(conn);
		}
		logger.fine("connection=" + conn);
		logger.fine("connection.getAutoCommit()=" + conn.getAutoCommit());
		return conn;
	}

	/**
	 * Close the Connection, created via the given DataSource, if it is not
	 * managed externally (i.e. not bound to the thread). Will never close a
	 * Connection from a SmartDataSource returning shouldClose=false.
	 * 
	 * @param con
	 *            Connection to close if necessary (if this is null, the call
	 *            will be ignored)
	 * @param dataSource
	 *            DataSource that the Connection came from (can be
	 *            <code>null</code>)
	 * @see SmartDataSource#shouldClose
	 */

	private void closeConnection() {
		logger.fine("enter closeConnection()");
		Connection conn = (Connection) dbConnThreadLocal.get();
		if (conn != null) {
			try {
				logger.fine("conn.getAutoCommit()=" + conn.getAutoCommit());
				logger.fine("conn.isClosed()=" + conn.isClosed());

				// if connection is in autoCommit mode,close the connection
				if (conn.getAutoCommit() && !conn.isClosed()) {
					conn.close();
					logger.fine("close connection from ThreadLocal,bounded thread=" + Thread.currentThread());
					dbConnThreadLocal.set(null);
				}
			} catch (SQLException e) {
				logger.warning("JDBC CLOSE Connection EXCEPTION: message = " + e.getMessage());
			}
		} else {
			logger.fine("dbConnThreadLocal is null");
		}
	}

	/**
	 * Close the Connection, created via the given DataSource, if it is not
	 * managed externally (i.e. not bound to the thread). Will never close a
	 * Connection from a SmartDataSource returning shouldClose=false.
	 * 
	 * @param con
	 *            Connection to close if necessary (if this is null, the call
	 *            will be ignored)
	 * @param dataSource
	 *            DataSource that the Connection came from (can be
	 *            <code>null</code>)
	 * @see SmartDataSource#shouldClose
	 */
	public void closeConnection(Connection conn) {
		try {
			if (conn != null && !conn.isClosed()) {
				logger.fine("close connection, current thread=" + Thread.currentThread());
				conn.close();
			}
		} catch (SQLException e) {
			logger.warning("JDBC CLOSE Connection EXCEPTION: message = " + e.getMessage());
		} finally {
			// set the threadlocal connection to null
			dbConnThreadLocal.set(null);
		}
	}

	/**
	 * Close the given JDBC Statement and ignore any thrown exception. This is
	 * useful for typical finally blocks in manual JDBC code.
	 * 
	 * @param stmt
	 *            the JDBC Statement to close
	 */
	private void closeStatement(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException ex) {
				logger.warning("Could not close JDBC Statement");
			} catch (RuntimeException ex) {
				logger.warning("Unexpected exception on closing JDBC Statement");
			}
		}
	}

	/**
	 * Close the given JDBC ResultSet and ignore any thrown exception. This is
	 * useful for typical finally blocks in manual JDBC code.
	 * 
	 * @param rs
	 *            the JDBC ResultSet to close
	 */
	private void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException ex) {
				logger.warning("Could not close JDBC ResultSet");
			} catch (RuntimeException ex) {
				logger.warning("Unexpected exception on closing JDBC ResultSet");
			}
		}
	}

	/**
	 * Return whether the given JDBC driver supports JDBC 2.0 batch updates.
	 * <p>
	 * Typically invoked right before execution of a given set of statements: to
	 * decide whether the set of SQL statements should be executed through the
	 * JDBC 2.0 batch mechanism or simply in a traditional one-by-one fashion.
	 * <p>
	 * Logs a warning if the "supportsBatchUpdates" methods throws an exception
	 * and simply returns false in that case.
	 * 
	 * @param con
	 *            the Connection to check
	 * @return whether JDBC 2.0 batch updates are supported
	 * @see java.sql.DatabaseMetaData#supportsBatchUpdates
	 */
	private boolean supportBatchUpdate(Connection con) {
		try {
			DatabaseMetaData dbmd = con.getMetaData();
			if (dbmd != null) {
				if (dbmd.supportsBatchUpdates()) {
					logger.fine("JDBC driver supports batch updates");
					return true;
				} else {
					logger.fine("JDBC driver does not support batch updates");
				}
			}
		} catch (SQLException ex) {
			logger.warning("JDBC driver 'supportsBatchUpdates' method threw exception");
		} catch (AbstractMethodError err) {
			logger.warning("JDBC driver does not support JDBC 2.0 'supportsBatchUpdates' method");
		}
		return false;
	}



	/**
	 * @param sqlString
	 *            The original SQL that is used to query data
	 * @param parameters
	 *            The original search criterias
	 * @param conn
	 *            The db connection
	 * @return How many rows satified the search criteria
	 * @throws NamingException
	 */
	private int getTotalRowCount(String sqlString, boolean isAlreadyCountSql, Object[] parameters, Connection conn) {
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		int count = 0;
		try {
			String sql;
			if (isAlreadyCountSql) {
				sql = sqlString;
			} else {
				String upperCaseSQL = sqlString.toUpperCase();
				StringBuffer sqlStringCount = new StringBuffer("SELECT COUNT(*) from ( ");
				sqlStringCount.append(sqlString).append(") a");
				sql = sqlStringCount.toString();
				upperCaseSQL = sql.toUpperCase();
				int rownumIndex = Math.max(upperCaseSQL.lastIndexOf(" ROWNUM "), upperCaseSQL.lastIndexOf(" ROWNUM<"));
				int fromIndex = upperCaseSQL.lastIndexOf(" FROM ");
				if (fromIndex > 0 && rownumIndex > fromIndex) {
					sql = sql.substring(0, rownumIndex) + " 0 " + sql.substring(rownumIndex + 7, sql.length());
				}
			}
			logger.fine(assembleSQL(sql, parameters, SEPERATOR));
			pstmt = conn.prepareStatement(sql);
			this.setParameters(pstmt, parameters);
			rs = pstmt.executeQuery();
			if (rs.next())
				count = rs.getInt(1);
		} catch (SQLException sqlEx) {
			logger.warning("execute sql error");
		} finally {
			closeResultSet(rs);
			closeStatement(pstmt);
		}
		return count;
	}

	/**
	 * Execute a SQL, returns a list of record.
	 * 
	 * @param sqlString
	 *            The sql.
	 * @param parameters
	 *            The search criteria values.
	 * @return A Collection of Object array which every element map a field in
	 *         ResultSet. e.g. If there are 2 rowa selected with 4 columns
	 *         "SERV_PER_CODE, PER_ID1, PER_ID2, PER_ID3", the result may be
	 * @throws SQLException
	 */
	public List<Object> select(String sqlString, Object[] parameters) {
		return select(sqlString, parameters, new DefaultDBResultSetProcessor());
	}

	/**
	 * Execute a SQL, returns a list of record.
	 * 
	 * @param sqlString
	 *            The sql.
	 * @param parameters
	 *            The search criteria values.
	 * @param processor
	 *            DBResultSetProcessor
	 * @return A Collection of Object array which every element map a field in
	 *         ResultSet. e.g.
	 */
	public <T> List<T> select(String sqlString, Object[] parameters, DBResultSetProcessor<T> processor) {
		return select(sqlString, parameters, null, processor);
	}

	/**
	 * Execute a SQL, returns a list of record.
	 * 
	 * @param sqlString
	 *            The sql.
	 * @param parameters
	 *            The search criteria values.
	 * @param qf
	 *            format object
	 * @param processor
	 *            DBResultSetProcessor
	 * @return A Collection of Object array which every element map a field in
	 *         ResultSet. e.g.
	 */
	public <T> List<T> select(String sqlString, Object[] parameters, QueryFormat qf, DBResultSetProcessor<T> processor) {
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		List<T> records = new ArrayList<T>(50);
		Connection conn = null;

		String sql = sqlString;
		try {
			if (qf != null && qf.getSortField() > 0) {
				if (qf.getSortField() > 0) {
					sql = "SELECT  a.* FROM " + "( " + sqlString + " ) a  order by  " + qf.getSortField();
					if (qf.getSortOrder() == 1) {
						sql += " DESC";
					} else {
						sql += " ASC";
					}
				}

			}
			conn = getConnection();
			pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			logger.fine(assembleSQL(sql, parameters, SEPERATOR));
			this.setParameters(pstmt, parameters);
			rs = pstmt.executeQuery(); // nerver null
			while (rs.next())
				records.add(processor.processResultSetRow(rs));
			logger.fine(records.size() + " records returned.");
			return records;
		} catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			logger.warning("execute sql error");
		} finally {
			closeResultSet(rs);
			closeStatement(pstmt);
			closeConnection();
			pstmt = null;
			conn = null;
		}
		return records;
	}

	/**
	 * Execute a SQL, returns a single record.
	 * 
	 * @param sqlString
	 *            The sql.
	 * @param parameters
	 *            The search criteria values.
	 * @return A Object array of Object array which map a field in ResultSet.
	 *         e.g.
	 * @throws SQLException
	 */
	public Object locate(String sqlString, Object[] parameters) {
		return locate(sqlString, parameters, new DefaultDBResultSetProcessor());
	}

	/**
	 * Execute a SQL, returns a single record.
	 * 
	 * @param sqlString
	 *            The sql.
	 * @param parameters
	 *            The search criteria values.
	 * @param processor
	 *            DBResultSetProcessor
	 * @return A Object array of Object array which map a field in ResultSet.
	 *         e.g.
	 */
	public <T> T locate(String sqlString, Object[] parameters, DBResultSetProcessor<T> processor) {
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		T retObj = null;
		Connection conn = null;

		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sqlString, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			logger.fine(assembleSQL(sqlString, parameters, SEPERATOR));
			this.setParameters(pstmt, parameters);
			rs = pstmt.executeQuery(); // nerver null
			if (rs.next())
				retObj = processor.processResultSetRow(rs);
			logger.fine("Return Object:" + retObj);
			return retObj;
		} catch (SQLException sqlEx) {
			logger.warning("DataAccessException execute sql error" + sqlString + "    parameters:"
					+ parameters.toString());

		} finally {
			closeResultSet(rs);
			closeStatement(pstmt);
			closeConnection();
			pstmt = null;
			conn = null;
		}
		return null;
	}

	/**
	 * Set PreparedStatement parameters
	 * 
	 * @param pstmt
	 *            PreparedStatement
	 * @param parameters
	 *            The search criteria values.
	 * @throws SQLException
	 */
	private void setParameters(PreparedStatement pstmt, Object[] parameters) throws SQLException {
		if (parameters != null) {
			for (int i = 0; i < parameters.length; i++) {
				if ((parameters[i] == null) || "".equals(parameters[i])) {
					pstmt.setString(i + 1, null);
				} else if (parameters[i] instanceof java.util.Date) {
					pstmt.setTimestamp(i + 1, new Timestamp(((java.util.Date) parameters[i]).getTime()));
				} else {
					pstmt.setObject(i + 1, parameters[i]);
				}
			}
		}
	}

	/**
	 * To execute the SQL statement like INSERT,UPDATE,DELETE.It means that no
	 * record set would been returned.
	 * 
	 * @param sqlString
	 * @param parameters
	 * @return either the row count for INSERT, UPDATE or DELETE statements, or
	 *         0 for SQL statements that return nothing.
	 * @throws SQLException
	 */
	public int update(String sqlString, Object[] parameters) {
		PreparedStatement pstmt = null;
		Connection conn = null;
		try {
			logger.fine(assembleSQL(sqlString, parameters, SEPERATOR));
			conn = getConnection();
			pstmt = conn.prepareStatement(sqlString);
			this.setParameters(pstmt, parameters);
			int count = pstmt.executeUpdate();
			logger.fine(count + " Rows are affected.");
			return count;
		} catch (SQLException sqlEx) {
			logger.warning(assembleSQL(sqlString, parameters, SEPERATOR));
			logger.warning(PubUtil.stackTraceToString(sqlEx));
			logger.warning("execute sql error");
		} finally {
			closeStatement(pstmt);
			closeConnection();
			pstmt = null;
			conn = null;
		}
		return 0;
	}

	public boolean delete(String sqlString, Object[] parameters) {
		PreparedStatement pstmt = null;
		Connection conn = null;
		try {
			logger.fine(assembleSQL(sqlString, parameters, SEPERATOR));
			conn = getConnection();
			pstmt = conn.prepareStatement(sqlString);
			this.setParameters(pstmt, parameters);
			boolean res = pstmt.execute();
			logger.fine(res + " Rows are affected.");
			return res;
		} catch (SQLException sqlEx) {
			logger.warning("execute sql error");
		} finally {
			closeStatement(pstmt);
			closeConnection();
			pstmt = null;
			conn = null;
		}
		return false;
	}

	/**
	 * To execute the SQL statement INSERT.It means that no record set would
	 * been returned.
	 * 
	 * @param sqlString
	 * @param parameters
	 * @return either the row count for INSERT, UPDATE or DELETE statements, or
	 *         0 for SQL statements that return nothing.
	 * @throws SQLException
	 */
	public int insert(String sqlString, Object[] parameters) {
		PreparedStatement pstmt = null;
		Connection conn = null;
		try {
			logger.fine(assembleSQL(sqlString, parameters, SEPERATOR));
			conn = getConnection();
			pstmt = conn.prepareStatement(sqlString);
			this.setParameters(pstmt, parameters);
			int count = pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();

			int id = 0;
			if (rs != null && rs.next()) {
				id = rs.getInt(1);
			}
			logger.fine(count + " Rows are affected.");
			return id;
		} catch (SQLException sqlEx) {
			sqlEx.printStackTrace();
			logger.warning("execute sql error");
		} finally {
			closeStatement(pstmt);
			closeConnection();
			pstmt = null;
			conn = null;
		}
		return 0;
	}

	/**
	 * Update CLOB column. Only can update on CLOB column at once time.<br>
	 * ussage:<br>
	 * <blockquote> tableName="ARTICLE"; clobColumn="CONTENT"; sqlCondition =
	 * "ART_ID=10000202";<br>
	 * clobValue="This is clob,This is clob, This is clob, ...";<br>
	 * dba.updateCLOB(tableName, clobColumn, clobValue, sqlCondition); <br>
	 * </blockquote>
	 * 
	 * @param tableName
	 *            the table name, only can update one table at once time.
	 * @param clobColumn
	 *            the column name which defined as CLOB and to be update.
	 * @param clobValue
	 *            the clob string to be update
	 * @param sqlCondition
	 *            the sql condition.
	 * @return efftected row counts.
	 */
	public void updateCLOB(String tableName, String columnName, String clobValue, String sqlCondition) {
		PreparedStatement pstmt = null;
		Connection conn = null;
		ResultSet rs = null;

		String insEmptyClob = "UPDATE " + tableName + " SET " + columnName + " = EMPTY_CLOB() WHERE " + sqlCondition;
		String selectForUpdate = "SELECT " + columnName + " FROM " + tableName + " WHERE " + sqlCondition
				+ " FOR UPDATE";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(insEmptyClob);
			pstmt.executeUpdate();
			closeStatement(pstmt);
			pstmt = null;
			pstmt = conn.prepareStatement(selectForUpdate);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Clob clob = (Clob) rs.getClob(columnName);
				String value = clobValue;
				if (value == null)
					value = "";
				char[] valueChars = new char[value.length()];
				value.getChars(0, value.length(), valueChars, 0);
				clob.truncate(0);
				Writer writer = clob.setCharacterStream(0);
				writer.write(valueChars);
				writer.close();
			}
		} catch (SQLException sqlEx) {
			logger.warning("execute sql error");
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			closeResultSet(rs);
			closeStatement(pstmt);
			closeConnection();
			pstmt = null;
			conn = null;
		}
	}

	/**
	 * Execute the sql in batch.
	 * 
	 * @param sqlString
	 * @param pss
	 * @return
	 */
	public int[] batchUpdate(String sqlString, BatchPreparedStatementSetter pss) {
		PreparedStatement pstmt = null;
		Connection conn = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sqlString);
			int batchSize = pss.getBatchSize();

			// if database support batch update then execute it, else run
			// executeUpdate repetitionally
			if (supportBatchUpdate(conn)) {
				logger.fine("Run executeBatch()...");
				for (int i = 0; i < batchSize; i++) {
					Object[] parameters = pss.setValues(pstmt, i);
					logger.fine(assembleSQL(sqlString, parameters, SEPERATOR));
					pstmt.addBatch();
				}
				return pstmt.executeBatch();
			} else {
				int[] rowsAffected = new int[batchSize];
				for (int i = 0; i < batchSize; i++) {
					Object[] parameters = pss.setValues(pstmt, i);
					logger.fine(assembleSQL(sqlString, parameters, SEPERATOR));
					rowsAffected[i] = pstmt.executeUpdate();
				}
				return rowsAffected;
			}
		} catch (SQLException sqlEx) {
			logger.warning("batch execute sql error :"+sqlEx.getMessage());
		} finally {
			closeStatement(pstmt);
			closeConnection();
			pstmt = null;
			conn = null;
		}
		return new int[0];
	}

	/**
	 * conert to DataPage
	 * 
	 * @param rs
	 *            ResultSet
	 * @param qf
	 *            QueryFormat
	 * @param processor
	 *            DBResultSetProcessor
	 * @return DataPage
	 * @throws SQLException
	 */
	private <T> DataPage<T> convertToDataPage(ResultSet rs, QueryFormat qf, DBResultSetProcessor<T> processor)
			throws SQLException {
		DataPage<T> dataPage = new DataPage<T>();
		int startRowNo = qf.getStartRow();

		if (startRowNo < 1)
			startRowNo = 1;
		ResultSetMetaData rmd = rs.getMetaData();
		int numcols = rmd.getColumnCount();
		String[] listColumns = new String[numcols];
		for (int i = 0; i < numcols; i++) {
			listColumns[i] = rmd.getColumnName(i + 1);
		}
		dataPage.setColumnNames(listColumns); // set
		dataPage.setTotalRowCount(qf.getTotalRowCount()); // set
		dataPage.setStartRowNo(startRowNo); // set
		logger.fine("startRowNo=" + startRowNo);
		try {
			List<T> result = new ArrayList<T>();

			while (rs.next()) {
				result.add(processor.processResultSetRow(rs)); // set
			}
			dataPage.setResult(result);
			logger.fine(result.size() + " Rows got.");
		} catch (Exception e) {
			logger.warning(this.getClass().getName() + "#result.add(processor.processResultSetRow(rs))="
					+ e.getMessage());
		}
		return dataPage;
	}

	/**
	 * Print SQL with param
	 * 
	 * @param sql
	 * @param params
	 * @param seperator
	 * @return
	 */
	private String assembleSQL(String sql, Object[] params, String seperator) {
		StringBuffer retValue = new StringBuffer();
		try {
			if ((sql != null) && (sql.length() > 0)) {
				retValue.append(sql);
				if ((params != null) && (params.length > 0)) {
					for (int i = 0; i < params.length; i++) {
						int pos1 = retValue.indexOf(seperator);
						if (params[i] == null) {
							retValue.replace(pos1, pos1 + seperator.length(), "");
						} else if (params[i] instanceof Date) {
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date date = (Date) params[i];
							retValue.replace(pos1, pos1 + seperator.length(), "STR_TO_DATE('" + sdf.format(date)
									+ "', '%Y-%m-%d %H:%i:%s')");
						} else if (params[i] instanceof String)
							retValue.replace(pos1, pos1 + seperator.length(), "'" + params[i].toString() + "'");
						else
							retValue.replace(pos1, pos1 + seperator.length(), params[i].toString());
					}
				}
			}
		} catch (Exception ex) {
		}
		return retValue.toString();
	}

	/**
	 * getRowCount
	 * 
	 * @param sqlString
	 *            String
	 * @param parameters
	 *            Object[]
	 * @return int
	 */
	public int getRowCount(String sqlString, Object[] parameters) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		int count = 0;
		try {
			conn = getConnection();
			logger.fine(assembleSQL(sqlString, parameters, SEPERATOR));
			pstmt = conn.prepareStatement(sqlString);
			this.setParameters(pstmt, parameters);
			rs = pstmt.executeQuery();
			if (rs.next())
				count = rs.getInt(1);
		} catch (SQLException sqlEx) {
			logger.warning("execute sql error");
		} finally {
			closeResultSet(rs);
			closeStatement(pstmt);
			closeConnection();
			pstmt = null;
			conn = null;
		}
		return count;
	}
}
