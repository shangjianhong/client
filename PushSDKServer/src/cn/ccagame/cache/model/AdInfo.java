package cn.ccagame.cache.model;

import org.json.simple.JSONObject;

import cn.ccagame.cache.Cache;

public class AdInfo {
	public static final String KEY_CURRENT_AD_INFO = "CURRENT_AD_INFO";
	public static final String KEY_AD_ID = "AD_ID";
	public static final String KEY_AD_BODY_CONTENT = "AD_BODY_CONTENT";
	public static final String KEY_AD_TYPE = "AD_TYPE";
	public static final String KEY_NOTIFICATION_ICON_URL = "NOTIFICATION_ICON_URL";
	public static final String KEY_NOTIFICATION_TITLE = "NOTIFICATION_TITLE";
	public static final String KEY_NOTIFICATION_CONTENT = "NOTIFICATION_CONTENT";
	public static final String KEY_AD_CONTENT = "AD_CONTENT";
	public static final String KEY_APK_PACKAGE_NAME = "APK_PACKAGE_NAME";
	public static final String KEY_APK_DOWNLOAD_URL = "APK_DOWNLOAD_URL";
	public static final String KEY_APK_SHOW_INFO = "APK_SHOW_INFO";
	public static final String KEY_TITLE = "TITLE";
	public static final String KEY_ICON_URL = "ICON_URL";
	public static final String KEY_VERSION = "VERSION";
	public static final String KEY_APK_SIZE = "APK_SIZE";
	public static final String KEY_DESC = "DESC";
	public static final String KEY_SCREEN_SHOT_URL = "SCREEN_SHOT_URL";
	public static final String KEY_APK_DOWNLOADS = "APK_DOWNLOADS";
	public static final String KEY_APK_STARS = "APK_STARS";
	public static final String KEY_UPDATE_APK = "UPDATE_APK";
	public static final String KEY_DOWNLOAD_MODE = "DOWNLOAD_MODE";
	public static final String KEY_BAR_ICON_ID = "NOTIFICATION_BAR_ICON_ID";
	public static final String KEY_NOTIFICATION_ICON_UPDATE_MODE = "NOTIFICATION_CONTENT_ICON_UPDATE_MODE";
	public static final String KEY_OPEN_INSTALL_VIEW = "APK_DOWNLOAD_SUCCESS_AUTO_OPEN_INSTALL_VIEW";

	
	private int adId;
	private int channelId;
	private int adType;
	private String adName;
	private String adTitle;
	private String adContent;
	private String adImgUrl;
	private String adUrl;
	private String apkName;
	private String apkTitle;
	private String apkIconUrl;
	private String apkVersion;
	private String apkSize;
	private String apkInfo;
	private String apkType;
	private long createDate;
	private long lastUpdateDate;
	private String createUser;
	private String lastUpdateUser;
	private int status;
	private float adPrice;
	private int apkDownloads;
	private int apkStars;
	private int updateApk;
	private int downloadMode;
	private int barIconId;
	private int notificationIconUpdateMode;
	private int openInstallView;
	
	
	public int getBarIconId() {
		return barIconId;
	}

	public void setBarIconId(int barIconId) {
		this.barIconId = barIconId;
	}

	public int getNotificationIconUpdateMode() {
		return notificationIconUpdateMode;
	}

	public void setNotificationIconUpdateMode(int notificationIconUpdateMode) {
		this.notificationIconUpdateMode = notificationIconUpdateMode;
	}

	public int getOpenInstallView() {
		return openInstallView;
	}

	public void setOpenInstallView(int openInstallView) {
		this.openInstallView = openInstallView;
	}

	public int getDownloadMode() {
		return downloadMode;
	}

	public void setDownloadMode(int downloadMode) {
		this.downloadMode = downloadMode;
	}

	public int getUpdateApk() {
		return updateApk;
	}

	public void setUpdateApk(int updateApk) {
		this.updateApk = updateApk;
	}

	public int getApkDownloads() {
		return apkDownloads;
	}

	public void setApkDownloads(int apkDownloads) {
		this.apkDownloads = apkDownloads;
	}

	public int getApkStars() {
		return apkStars;
	}

	public void setApkStars(int apkStars) {
		this.apkStars = apkStars;
	}

	public int getAdId(){
		return adId;
	}
	
	public void setAdId(int adId){
		this.adId = adId;
	}
	
	public int getChannelId(){
		return channelId;
	}
	
	public void setChannelId(int channelId){
		this.channelId = channelId;
	}
	
	public int getAdType(){
		return adType;
	}
	
	public void setAdType(int adType){
		this.adType = adType;
	}
	
	public String getAdName(){
		return adName;
	}
	
	public void setAdName(String adName){
		this.adName = adName;
	}

	public String getAdTitle() {
		return adTitle;
	}

	public void setAdTitle(String adTitle) {
		this.adTitle = adTitle;
	}

	public String getAdContent() {
		return adContent;
	}

	public void setAdContent(String adContent) {
		this.adContent = adContent;
	}

	public String getAdImgUrl() {
		return adImgUrl;
	}

	public void setAdImgUrl(String adImgUrl) {
		this.adImgUrl = adImgUrl;
	}

	public String getAdUrl() {
		return adUrl;
	}

	public void setAdUrl(String adUrl) {
		this.adUrl = adUrl;
	}

	public String getApkName() {
		return apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
	}

	public String getApkTitle() {
		return apkTitle;
	}

	public void setApkTitle(String apkTitle) {
		this.apkTitle = apkTitle;
	}

	public String getApkIconUrl() {
		return apkIconUrl;
	}

	public void setApkIconUrl(String apkIconUrl) {
		this.apkIconUrl = apkIconUrl;
	}

	public String getApkVersion() {
		return apkVersion;
	}

	public void setApkVersion(String apkVersion) {
		this.apkVersion = apkVersion;
	}

	public String getApkSize() {
		return apkSize;
	}

	public void setApkSize(String apkSize) {
		this.apkSize = apkSize;
	}

	public String getApkInfo() {
		return apkInfo;
	}

	public void setApkInfo(String apkInfo) {
		this.apkInfo = apkInfo;
	}

	public String getApkType() {
		return apkType;
	}

	public void setApkType(String apkType) {
		this.apkType = apkType;
	}

	public long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}

	public long getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(long lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getLastUpdateUser() {
		return lastUpdateUser;
	}

	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public float getAdPrice() {
		return adPrice;
	}

	public void setAdPrice(float adPrice) {
		this.adPrice = adPrice;
	}

	public JSONObject buildAdJson(){
		JSONObject adJson = new JSONObject();
		adJson.put(KEY_AD_ID, adId);
		adJson.put(TextConfig.KEY_BACK_TEXT, Cache.getInstance().getTextConfig().getBackText());
		adJson.put(TextConfig.KEY_DOWNLOAD_TEXT, Cache.getInstance().getTextConfig().getDownloadText());
		adJson.put(TextConfig.KEY_POPULAR_TITLE, Cache.getInstance().getTextConfig().getPopularTitle());
		adJson.put(KEY_AD_BODY_CONTENT, buildAdBodyContentJson());
		return adJson;
	}
	
	private JSONObject buildAdBodyContentJson(){
		JSONObject adBodyContentJson = new JSONObject();
		adBodyContentJson.put(KEY_AD_TYPE,adType);
		adBodyContentJson.put(KEY_NOTIFICATION_TITLE,adTitle);
		adBodyContentJson.put(KEY_NOTIFICATION_CONTENT,adContent);
		adBodyContentJson.put(KEY_BAR_ICON_ID,barIconId);
		adBodyContentJson.put(KEY_NOTIFICATION_ICON_UPDATE_MODE, notificationIconUpdateMode);
		adBodyContentJson.put(KEY_AD_CONTENT,buildAdContentJson());
		return adBodyContentJson;
	}
	
	private JSONObject buildAdContentJson(){
		JSONObject adContentJson = new JSONObject();
		adContentJson.put(KEY_APK_PACKAGE_NAME,apkName);
		adContentJson.put(KEY_APK_DOWNLOAD_URL,adUrl);
		adContentJson.put(KEY_UPDATE_APK, updateApk);
		adContentJson.put(KEY_DOWNLOAD_MODE, downloadMode);
		adContentJson.put(KEY_OPEN_INSTALL_VIEW, openInstallView);
		adContentJson.put(KEY_APK_SHOW_INFO,buildApkShowInfoJson());
		return adContentJson;
	}
	
	private JSONObject buildApkShowInfoJson(){
		JSONObject apkShowInfoJson = new JSONObject();
		apkShowInfoJson.put(KEY_ICON_URL,apkIconUrl);
		apkShowInfoJson.put(KEY_TITLE,apkTitle);
		apkShowInfoJson.put(KEY_VERSION,apkVersion);
		apkShowInfoJson.put(KEY_APK_SIZE,apkSize);
		apkShowInfoJson.put(KEY_DESC,apkInfo);
		apkShowInfoJson.put(KEY_APK_DOWNLOADS,apkDownloads);
		apkShowInfoJson.put(KEY_APK_STARS, apkStars);
		apkShowInfoJson.put(KEY_SCREEN_SHOT_URL,adImgUrl);
		return apkShowInfoJson;
	}
}
