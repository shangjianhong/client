package cn.ccagame.cache.model;



public class TextConfig {
	public static final String KEY_BACK_TEXT = "BACK_TEXT";
	public static final String KEY_DOWNLOAD_TEXT = "DOWNLOAD_TEXT";
	public static final String KEY_POPULAR_TITLE = "POPULAR_TITLE";
	
	private String backText;
	private String downloadText;
	private String popularTitle;
	
	public String getBackText() {
		return backText;
	}
	public void setBackText(String backText) {
		this.backText = backText;
	}
	public String getDownloadText() {
		return downloadText;
	}
	public void setDownloadText(String downloadText) {
		this.downloadText = downloadText;
	}
	public String getPopularTitle() {
		return popularTitle;
	}
	public void setPopularTitle(String popularTitle) {
		this.popularTitle = popularTitle;
	}
	
}
