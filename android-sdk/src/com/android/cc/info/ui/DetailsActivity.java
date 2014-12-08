/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.cc.info.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.cc.info.data.AdPush;
import com.android.cc.info.preferences.OSharedPreferences;
import com.android.cc.info.service.ServiceManager;
import com.android.cc.info.util.AndroidUtil;
import com.android.cc.info.util.Constants;
import com.android.cc.info.util.DebugLog;
import com.android.cc.info.util.LogUtil;
import com.android.cc.info.util.StringUtils;
import com.android.cc.info.util.UserStepReportUtil;

/** 
 * Activity for displaying the notification details view.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class DetailsActivity extends Activity {
    private static final String TAG = LogUtil
            .makeDebugLogTag(DetailsActivity.class);

    private AdPush adPush;
    
    boolean actionMode = false;

    public DetailsActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        DebugLog.d(TAG, "onCreate");

        Intent intent = getIntent();
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        int actionMode = intent.getIntExtra(Constants.NOTIFICATION_ACTION_MODE, 0);
        String jsonContent = intent.getStringExtra(Constants.PUSH_INFO);
        if(!StringUtils.isEmpty(jsonContent)){
        	adPush = AdPush.parseAdPushJson(jsonContent);
        }
        if(adPush == null){
        	jsonContent = intent.getStringExtra(Constants.PUSH_INFO_FOR_JSON);
        	if(StringUtils.isEmpty(jsonContent)){
        		UserStepReportUtil.reportStep(this, UserStepReportUtil.ERROR_CODE_ID, UserStepReportUtil.AD_PUSH_NOTIFICATION_OPEN_TIGGER_ERROR);
        		finish();
            	return;
        	}
        	adPush = AdPush.parseAdPushJson(jsonContent);
        	if(adPush == null) {
        		UserStepReportUtil.reportStep(this, UserStepReportUtil.ERROR_CODE_ID, UserStepReportUtil.AD_PUSH_NOTIFICATION_OPEN_TIGGER_ERROR);
        		finish();
        		return;
        	}
        }
    	View pushView = new PushView(this,adPush);
        if(actionMode == Constants.NOTIFICATION_ACTION_MODE_DISK && adPush != null && AndroidUtil.isPackageExist(this, adPush.mCurrentAdInfo.apkPackageName)
        		&& (adPush.mCurrentAdInfo.updateApk && OSharedPreferences.getDownloadSuccessFlagForPackageName(this, adPush.mCurrentAdInfo.apkPackageName) || !adPush.mCurrentAdInfo.updateApk)){
        	try {
        		if(AndroidUtil.startNewAPK(this, adPush.mCurrentAdInfo.apkPackageName)){
        			UserStepReportUtil.reportStep(this, Integer.parseInt(adPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_CLICK_BY_DISK_ACTION_FOR_OPEN_APP);
        			finish();
        		}else{
        			setContentView(pushView);
        			UserStepReportUtil.reportStep(this, Integer.parseInt(adPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_CLICK_BY_DISK_ACTION);
        			ServiceManager sm = new ServiceManager(this);
            		sm.sendReceiver(AndroidUtil.getActionName(this,Constants.ACTION_COMPARE_NEW_AD_INFO), adPush.mCurrentAdInfo.adId);
        		}
			} catch (Exception e) {
	        	setContentView(pushView);
	        	UserStepReportUtil.reportStep(this, Integer.parseInt(adPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_CLICK_BY_DISK_ACTION);
	        	ServiceManager sm = new ServiceManager(this);
        		sm.sendReceiver(AndroidUtil.getActionName(this,Constants.ACTION_COMPARE_NEW_AD_INFO), adPush.mCurrentAdInfo.adId);
			}
        }else {
        	setContentView(pushView);
        	if(actionMode == Constants.NOTIFICATION_ACTION_MODE_DISK){
        		UserStepReportUtil.reportStep(this, Integer.parseInt(adPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_CLICK_BY_DISK_ACTION);
        		ServiceManager sm = new ServiceManager(this);
        		sm.sendReceiver(AndroidUtil.getActionName(this,Constants.ACTION_COMPARE_NEW_AD_INFO), adPush.mCurrentAdInfo.adId);
        	}else if (actionMode == Constants.NOTIFICATION_ACTION_MODE_POPULAR){
        		UserStepReportUtil.reportStep(this, Integer.parseInt(adPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_CLICK_BY_POPULAR);
        	}else{
        		Notifier notifier = new Notifier(this);
                notifier.notifyForDefault(adPush,0);
        		UserStepReportUtil.reportStep(this, Integer.parseInt(adPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_CLICK);
        	}
		}
        super.onCreate(savedInstanceState);
    }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
	        Intent intent = new Intent(this,PopularTodayActivity.class);
			intent.putExtra(Constants.PUSH_INFO, adPush);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
