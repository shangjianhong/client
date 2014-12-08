package cn.ccagame.database;

/**
 * This class is used to pagination as a action member variable class.
 * 
 * @author Martin Liu
 * 
 */
public class Paginator {
	
	private String cpage;
	
	private String total;
	
	private String url = "";
	
	private int rowCnt;
	
	private int startRow;
	
	private int endRow;
	
	private String sPageSize;
	
	private int sortField;
	
	private int sortOrder;
	
	public Paginator() {
	}
	
	public int getRowCnt() {
		return rowCnt;
	}
	
	public void setRowCnt(int rowCnt) {
		this.rowCnt = rowCnt;
		int pageSize = 10;
		try {
			pageSize = Integer.parseInt(sPageSize);
		} catch (NumberFormatException nfx) {
			// ignore the exception
		}
		total = rowCnt / pageSize + (rowCnt % pageSize == 0 ? 0 : 1) + "";
	}
	
	public int getStartRow() {
		int pageSize = 10, iCpage = 1;
		try {
			if (cpage != null && !"".equals(cpage))
				iCpage = Integer.parseInt(cpage);
			pageSize = Integer.parseInt(sPageSize);
		} catch (NumberFormatException nfx) {
			// ignore the exception
		}
		
		startRow = (iCpage - 1) * pageSize;
		return startRow;
	}
	
	public int getEndRow() {
		int pageSize = 10;
		try {
			
			pageSize = Integer.parseInt(sPageSize);
		} catch (NumberFormatException nfx) {
			// ignore the exception
		}
		
		endRow = pageSize;
		return endRow;
	}
	
	public String getCpage() {
		if (cpage == null || "".equals(cpage))
			cpage = "1";
		return cpage;
	}
	
	public void setCpage(String cpage) {
		this.cpage = cpage;
	}
	
	public String getTotal() {
		if (total == null || "".equals(total)) {
			int pageSize = 10;
			try {
				pageSize = Integer.parseInt(sPageSize);
			} catch (NumberFormatException nfx) {
				// ignore the exception
			}
			total = rowCnt / pageSize + (rowCnt % pageSize == 0 ? 0 : 1) + "";
		}
		return total;
	}
	
	public void setTotal(String total) {
		if (total == null || "".equals(total))
			total = "0";
		this.total = total;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getSPageSize() {
		
		if (sPageSize == null) {
			sPageSize = "10";
		}
		return sPageSize;
	}
	
	public void setSPageSize(String pageSize) {
		sPageSize = pageSize;
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
