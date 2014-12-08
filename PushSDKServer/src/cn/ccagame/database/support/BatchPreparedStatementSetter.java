
package cn.ccagame.database.support;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Helper class interface for batch update process
 * 
 * @author Martin Liu
 */
public interface BatchPreparedStatementSetter {

	/** 
	 * Set values on the given PreparedStatement.
	 * @param ps PreparedStatement we'll invoke setter methods on
	 * @param i index of the statement we're issuing in the batch, starting from 0
	 * @throws SQLException there is no need to catch SQLExceptions
	 * @return parameters that client need to print sql
	 * that may be thrown in the implementation of this method.
	 * The DBAccessor class will handle them.
	 */
	Object[] setValues(PreparedStatement ps, int i) throws SQLException;

	/** 
	 * @return the size of the batch.
	 */
	int getBatchSize();

}
