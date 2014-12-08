package cn.ccagame.database;

/**
 * The class use to store the information of pagination and some search
 * criterias.
 * 
 * @author Martin Liu
 */
public class QueryFormat implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private int startRow = 0;

	private int endRow = 99999;

	private int totalRowCount = -1;

	private int sortField;

	private int sortOrder;

	/**
	 * QueryFormat Constructor
	 */
	public QueryFormat() {
	}

	/**
	 * QueryFormat Constructor
	 * 
	 * @param startRow
	 *            int
	 * @param endRow
	 *            int
	 */
	public QueryFormat(int startRow, int endRow) {
		this.setEndRow(endRow);
		this.setStartRow(startRow);
	}

	/**
	 * QueryFormat Constructor
	 * 
	 * @param paginator
	 */
	public QueryFormat(Paginator paginator) {
		this.setEndRow(paginator.getEndRow());
		this.setStartRow(paginator.getStartRow());
		this.setSortField(paginator.getSortField());
		this.setSortOrder(paginator.getSortOrder());
	}

	public boolean nextPage() {
		int pageSize = endRow - startRow + 1;
		endRow += pageSize;
		startRow += pageSize;
		return true;
	}

	/**
	 * @return Returns the endRow.
	 */
	public int getEndRow() {
		return endRow;
	}

	/**
	 * @param endRow
	 *            The endRow to set.
	 */
	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	/**
	 * @return Returns the startRow.
	 */
	public int getStartRow() {
		return startRow;
	}

	/**
	 * @param startRow
	 *            The startRow to set.
	 */
	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	/**
	 * @return Returns the totalRowNum.
	 */
	public int getTotalRowCount() {
		return totalRowCount;
	}

	/**
	 * @param totalRowNum
	 *            The totalRowNum to set.
	 */
	public void setTotalRowCount(int totalRowCount) {
		this.totalRowCount = totalRowCount;
	}

	public int getSortField() {
		return sortField;
	}

	public void setSortField(int sortField) {
		this.sortField = sortField;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

}