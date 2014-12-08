package cn.ccagame.cache.model;

public class AdSection {

	private int id;
	private int adStart;
	private int adEnd;
	private int limitedCount;
	private int limitedRate;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAdStart() {
		return adStart;
	}
	public void setAdStart(int adStart) {
		this.adStart = adStart;
	}
	public int getAdEnd() {
		return adEnd;
	}
	public void setAdEnd(int adEnd) {
		this.adEnd = adEnd;
	}
	public int getLimitedCount() {
		return limitedCount;
	}
	public void setLimitedCount(int limitedCount) {
		this.limitedCount = limitedCount;
	}
	public int getLimitedRate() {
		return limitedRate;
	}
	public void setLimitedRate(int limitedRate) {
		this.limitedRate = limitedRate;
	}
	
}
