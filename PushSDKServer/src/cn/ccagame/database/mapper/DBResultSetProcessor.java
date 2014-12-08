package cn.ccagame.database.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This interface is provide interface of DB result processor *
 * 
 * @author Martin Liu
 */
public interface DBResultSetProcessor<T> {
	
	/**
	 * Method processResultSetRow. The implmenter can decide how to process the
	 * current row of the ResultSet. Usually, the DAOOracle implements this API
	 * to convert a result set row into a datamodel.
	 * 
	 * @param rs
	 *            The SQL execute result set from database.
	 * @return Object The process result. It's usually a data model.
	 * @throws SQLException
	 *             Due to any data access exception encountered.
	 */
	public T processResultSetRow(ResultSet rs) throws SQLException;
	
}
