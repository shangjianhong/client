package com.android.cc.info.service;

import android.os.PowerManager;

public class WakelockManager {
	private static WakelockManager mWakelockManager = null;
	
	private PowerManager.WakeLock mWakelock = null;
	
	private WakelockManager() {
	
	}
	
	public static WakelockManager getInstance() {
		if (mWakelockManager == null) {
			mWakelockManager = new WakelockManager();
		}
		return mWakelockManager;
	}
	
	public PowerManager.WakeLock getWakelock() {
		return mWakelock;
	}
	
	public void setWakelock(PowerManager.WakeLock wakelock) {
		mWakelock = wakelock;
	}
}
