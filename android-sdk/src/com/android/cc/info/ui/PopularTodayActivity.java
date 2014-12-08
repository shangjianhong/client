package com.android.cc.info.ui;

import com.android.cc.info.data.AdPush;
import com.android.cc.info.service.ServiceManager;
import com.android.cc.info.util.AndroidUtil;
import com.android.cc.info.util.Constants;
import com.android.cc.info.util.StringUtils;
import com.android.cc.info.util.UserStepReportUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

public class PopularTodayActivity extends Activity {
	
	private AdPush adPush;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Intent intent = getIntent();
        adPush = (AdPush)intent
                .getSerializableExtra(Constants.PUSH_INFO);
        if(adPush == null){
        	String jsonContent = intent.getStringExtra(Constants.PUSH_INFO_FOR_JSON);
        	if(StringUtils.isEmpty(jsonContent)){
        		finish();
            	return;
        	}
        	adPush = AdPush.parseAdPushJson(jsonContent);
        	if(adPush == null) {
        		finish();
        		return;
        	}
        	UserStepReportUtil.reportStep(this, Integer.parseInt(adPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_CLICK_OPEN_POPULAR_BY_DISK);
        	String adids = "";
        	for (int i = 0; i < adPush.mTodayPopularList.size(); i++) {
        		if(i == adPush.mTodayPopularList.size() - 1){
        			adids += adPush.mTodayPopularList.get(i).adId;
        		}else{
        			adids += adPush.mTodayPopularList.get(i).adId + ",";
        		}
			}
        	ServiceManager serviceManager = new ServiceManager(this);
        	serviceManager.sendReceiver(AndroidUtil.getActionName(this,Constants.ACTION_COMPARE_NEW_POPULAR_INFO), adids);
        }else{
        	UserStepReportUtil.reportStep(this, Integer.parseInt(adPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_CLICK_OPEN_POPULAR_BY_BACK);
        }
		setContentView(new PopularTodayView(this, adPush));
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
	        UserStepReportUtil.reportStep(this, Integer.parseInt(adPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_CLOSE_POPULAR_FOR_BACK_KEY);
		}
		return super.onKeyDown(keyCode, event);
	}
}
