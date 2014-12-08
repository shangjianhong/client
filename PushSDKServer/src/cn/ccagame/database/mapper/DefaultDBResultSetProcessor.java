package cn.ccagame.database.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class is provide default DB result processor
 * 
 * @author Martin Liu
 */
public class DefaultDBResultSetProcessor implements DBResultSetProcessor<Object> {
	
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
	public Object processResultSetRow(ResultSet rs) throws SQLException {
		int numcols = rs.getMetaData().getColumnCount();
		Object[] record = new Object[numcols];
		for (int i = 0; i < numcols; i++)
			record[i] = rs.getObject(i + 1);
		return record;
	}
	
}
