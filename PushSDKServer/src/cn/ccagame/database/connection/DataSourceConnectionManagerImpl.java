package cn.ccagame.database.connection;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ccagame.tool.JNDIUtil;

/**
 * Class for datasource connection provider.
 * 
 * @author Martin Liu
 */
public class DataSourceConnectionManagerImpl implements ConnectionManager {

	private DataSource dataSource;

	private String dsJndiName;

	 private static final Logger logger = LoggerFactory.getLogger(DataSourceConnectionManagerImpl.class);


	/**
	 * Constructor.
	 * 
	 * @param dsJndiName
	 */
	public DataSourceConnectionManagerImpl(String dsJndiName) {
		try {
//			ComboPooledDataSource cds = new ComboPooledDataSource(dsJndiName);
//			Class.forName(cds.getDriverClass());
//			DataSource ds = DataSources.unpooledDataSource(cds.getJdbcUrl(),cds.getProperties());
//			dataSource = DataSources.pooledDataSource(ds);
//			this.dsJndiName = dsJndiName;
			dataSource=(DataSource) JNDIUtil.lookupLocalObject(dsJndiName);
			this.dsJndiName = dsJndiName;
			//cds.close();
		} catch (Throwable e) {
			logger.error("can not connect to datasource");
		}
	}

	/**
	 * @return DataSource
	 */
	public DataSource getDataSource() {
		return this.dataSource;
	}

	/**
	 * @return Connection
	 */
	public Connection getConnection() {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			logger.debug("DataSourceConnectionManagerImpl.getConnection().getAutoCommit()=" + conn.getAutoCommit());
		} catch (Throwable e) {
			logger.error("can not get connection from datasource");
		}
		return conn;
	}

	/**
	 * Close connection
	 * 
	 * @param Connection
	 *            conn
	 */
	public void closeConnection(Connection conn) throws SQLException {
		conn.close();
	}

	/**
	 * @return String
	 */
	public String getDsJndiName() {
		return dsJndiName;
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * @param Object
	 * @return boolean
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof DataSourceConnectionManagerImpl))
			return false;
		if (this == obj) {
			return true;
		} else {
			DataSourceConnectionManagerImpl o = (DataSourceConnectionManagerImpl) obj;
			return this.dsJndiName.equals(o.getDsJndiName());
		}
	}

	/**
	 * Returns a hash code value for the object.
	 * 
	 * @return int
	 */
	public int hashCode() {
		return this.dsJndiName.hashCode();
	}

}
