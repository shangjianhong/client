package com.android.cc.info;

import java.util.ArrayList;
import java.util.List;

import com.android.cc.info.util.Constants;


public class Config {
//	public static final String ip = "117.135.160.254";
	public static final String ip = "192.168.0.113";
//	public static final String ip = "proc.ccagame.com";
//	public static final String ip = Constants.LEFT_0 + Constants.DIAN_0 + Constants.CENTER_0 + Constants.DIAN_0 + Constants.RIGHT_0;
	public static final int port = 6100;
	public static final String SDK_VERSION = Constants.VERSION_CODE;
	
	public static List<String> mAccessServerList = new ArrayList<String>();
	
//	public static String  mAccessServerIp = "114.112.41.142";
	public static String  mAccessServerIp = Constants.ADDRESS_0 + Constants.DIAN_0 + 
			Constants.ADDRESS_1 + Constants.DIAN_0 +
			Constants.ADDRESS_2 + Constants.DIAN_0 +
			Constants.ADDRESS_3;
			
	public static int mAccessServerPort = 8443;
	
	static{
//		mAccessServerList.add("mobile.ccagame.com");
		mAccessServerList.add(Constants.LEFT_1 + Constants.DIAN_0 + Constants.CENTER_0 +  Constants.DIAN_0 + Constants.RIGHT_0);
//		mAccessServerList.add("dev.ccagame.com");
		mAccessServerList.add(Constants.LEFT_2 + Constants.DIAN_0 + Constants.CENTER_0 +  Constants.DIAN_0 + Constants.RIGHT_0);
	}
}
