
package cn.ccagame.database.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DataPage stores DB query result, without further info: max rows, total row count, etc.
 * 
 * @author Martin Liu
 */
public class DataPage<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * This will limit the max rows can be gotten from db each time.
	 */
	public static int MAX_ROW_COUNT = 9999;

	/**
	 * The query result.
	 */
	protected List<T> result;

	/**
	 * A set of column names.
	 */
	protected String[] columnNames;

	/**
	 * Total row count
	 */
	protected int totalRowCount;

	/**
	 * Start row number.
	 */
	protected int startRowNo;

	/**
	 * Constructor
	 */
	public DataPage() {
		result = new ArrayList<T>(0);
	}

	/**
	 * Constructor
	 * @param DataPage dp 
	 */
	public DataPage(DataPage<T> dp) {
		if (dp != null) {
			this.result = dp.getResult();
			this.columnNames = dp.getColumnNames();
			this.totalRowCount = dp.getTotalRowCount();
			this.startRowNo = dp.getStartRowNo();
		}
	}

	/**
	 * @return List
	 */
	public List<T> getResult() {
		return result;
	}

	/**
	 * @param List result
	 */
	public void setResult(List<T> result) {
		this.result = result;
	}

	/**
	 * @return boolean
	 */
	public boolean hasNextPage() {
		return (startRowNo + getRowCount()) <= totalRowCount;
	}

	/**
	 * @return int
	 */
	public int getTotalRowCount() {
		return totalRowCount;
	}

	/**
	 * @param int totalRowCount
	 */
	public void setTotalRowCount(int totalRowCount) {
		this.totalRowCount = totalRowCount;
	}

	/**
	 * @return int
	 */
	public int getStartRowNo() {
		return startRowNo;
	}

	/**
	 * @param int startRowNo
	 */
	public void setStartRowNo(int startRowNo) {
		this.startRowNo = startRowNo;
	}

	/**
	 * To retrieve the appointed field's value current row.
	 * @param columnName
	 * @return column index
	 */
	public int getColumIndex(String columnName) {
		int index = 0;
		for (int i = 0; i < columnNames.length; i++) {
			if (columnNames[i].equalsIgnoreCase(columnName.trim())) {
				index = i;
				break;
			}
		}
		return index;
	}

	/**
	 * @return int
	 */
	public int getColumns() {
		return (result == null || result.isEmpty()) ? 0 : ((Object[]) this.result.iterator().next()).length;
	}

	/**
	 * @return int
	 */
	public int getRowCount() {
		return result == null ? 0 : result.size();
	}

	/**
	 * Returns the columnNames.
	 * @return String[]
	 */
	public String[] getColumnNames() {
		return columnNames;
	}

	/**
	 * Sets the columnNames.
	 * @param columnNames The columnNames to set
	 */
	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}
}
