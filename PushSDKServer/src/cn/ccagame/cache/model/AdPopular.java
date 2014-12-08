package cn.ccagame.cache.model;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class AdPopular {
	public static final String KEY_TODAY_AD_POPULAR = "TODAY_AD_POPULAR";
	
	public static final String KEY_FIRST_RECOMMEND = "FIRST_RECOMMEND";
	public static final String KEY_FIRST_RECOMMEND_IMAGE = "FIRST_RECOMMEND_IMAGE";
	
	public static final int STATUS_FIRST_RECOMMEND = 1;
	
	private AdInfo adInfo;
	private int firstRecommend ;
	private String firstRecommendImage;
	
	public AdInfo getAdInfo() {
		return adInfo;
	}
	public void setAdInfo(AdInfo adInfo) {
		this.adInfo = adInfo;
	}
	public int getFirstRecommend() {
		return firstRecommend;
	}
	public void setFirstRecommend(int firstRecommend) {
		this.firstRecommend = firstRecommend;
	}
	public String getFirstRecommendImage() {
		return firstRecommendImage;
	}
	public void setFirstRecommendImage(String firstRecommendImage) {
		this.firstRecommendImage = firstRecommendImage;
	}
	
	public JSONObject buildAdPopularJson(){
		JSONObject adPopularJson = adInfo.buildAdJson();
		adPopularJson.put(KEY_FIRST_RECOMMEND, firstRecommend);
		adPopularJson.put(KEY_FIRST_RECOMMEND_IMAGE, firstRecommendImage);
		return adPopularJson;
	}
}
