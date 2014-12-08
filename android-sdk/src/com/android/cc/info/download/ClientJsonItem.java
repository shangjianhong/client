package com.android.cc.info.download;

public class ClientJsonItem {
	
	/**
	 * 手机基本信息 
	 * 包名、手机唯一标示号、IMSE、sim序列号、sim供应商、sim网络状态、应用版本、系统版本、联网状态、sdk版本、消息类型
	 */
	public final static String[] startJsonKey = {
		"appid","imei","imsi","simSerial","simOperator","simNetType","appVersion","sysVersion","currentNet","sdkVersion","msgType",
	};
	/**
	 * 用户ID 
	 * 返回码、用户ID、消息类型
	 */
	public final static String[] getUserIdJsonKey = {
		"recvCode","user_id","msgType",
	};
	/**
	 * 位置信息上报
	 * 包名、时间、纬度、精度、辐射范围、地址信息、消息类型、手机唯一标示号、IMSE、用户id
	 */
	public final static String[] locationJsonKey = {
		"appid","time","lat","lon","rad","address","msgType","imei","imsi","userId",
	};
	/**
	 * 手机应用列表上报
	 * 包名、手机应用列表(不包括系统列表)、消息类型、手机唯一标示号、IMSE、用户id
	 */
	public final static String[] apklistJsonKey = {
		"appid","apklist","msgType","imei","imsi","userId",
	};
	/**
	 * 动作信息
	 * 包名、类名、动作、时间、消息类型、手机唯一标示号、IMSE、用户id
	 */
	public final static String[] activityActionJsonKey = {
		"appid","className","action","time","msgType","imei","imsi","userId",
	};
	
	/**
	 * 获取上报服务器地址和端口
	 * 返回码、地址、端口
	 */
	public final static String[] getHostAndPortJsonKey = {
		"recvCode","ip","port",
	};
	
	/**
	 * 获取推广墙地址
	 * 返回码、地址、消息类型
	 */
	public final static String[] getMoreListUrlJsonKey = {
		"recvCode","list_url","msgType",
	};
	
	/**
	 * 请求消息
	 * 消息类型、包名、当前版本号、手机唯一标示号、IMSE、用户id
	 */
	public final static String[] requestInfoJsonKey = {
		"msgType","appid","appVersion","imei","imsi","userId",
	};
	
	/**
	 * 升级检测
	 * 返回码、最新版本、最新版本编号、最新版本介绍、消息类型
	 */
	public final static String[] updateInfoJsonKey = {
		"recvCode","appVersion","verSionInfo","appVersionCode","msgType",
	};
	
	public final static String[] adWallItemJsonKey = {
		"id","name","url",
	};
}
