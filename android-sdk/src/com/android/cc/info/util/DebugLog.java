package com.android.cc.info.util;

import android.util.Log;

public class DebugLog {
	
	private static boolean isDebug = true;
	
	private static final String TAG = "CC-INFO";
	
	public static void openDebugMode(){
		isDebug = true;
	}
	
	public static boolean getDebugMode(){
		return isDebug;
	}
	// TODO : i
	public static void i(String tag,String msg){
		if (isDebug)
			Log.i(TAG, "[" + tag + "] : " + msg);
	}
	
	public static void i(String tag,String msg,Throwable e){
		if (isDebug)
			Log.i(TAG, "[" + tag + "] : " + msg, e);
	}
	// TODO : v
	public static void v(String tag,String msg){
		if (isDebug)
			Log.v(TAG, "[" + tag + "] : " + msg);
	}
	
	public static void v(String tag,String msg,Throwable e){
		if (isDebug)
			Log.v(TAG, "[" + tag + "] : " + msg, e);
	}
	// TODO : d
	public static void d(String tag,String msg){
		if (isDebug)
			Log.d(TAG, "[" + tag + "] : " + msg);
	}
	
	public static void d(String tag,String msg,Throwable e){
		if (isDebug)
			Log.d(TAG, "[" + tag + "] : " + msg, e);
	}
	// TODO : w
	public static void w(String tag,String msg){
		if (isDebug)
			Log.w(TAG, "[" + tag + "] : " + msg);
	}
	
	public static void w(String tag,String msg,Throwable e){
		if (isDebug)
			Log.w(TAG, "[" + tag + "] : " + msg, e);
	}
	// TODO : e
	public static void e(String tag,String msg){
		if (isDebug)
			Log.e(TAG, "[" + tag + "] : " + msg);
	}
	
	public static void e(String tag,String msg,Throwable e){
		if (isDebug)
			Log.e(TAG, "[" + tag + "] : " + msg, e);
	}
	
	// TODO : ve
	public static void ve(String tag,String msg){
		if (isDebug)
			Log.e(TAG, "[" + tag + "] : " + msg);
	}
	
	public static void ve(String tag,String msg,Throwable e){
		if (isDebug)
			Log.e(TAG, "[" + tag + "] : " + msg, e);
	}
	
}
