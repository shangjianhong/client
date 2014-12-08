package com.android.cc.info.ui;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.android.cc.info.util.DebugLog;
import com.android.cc.info.util.LogUtil;

public class FullImageActivity extends Activity {
	
	private final static String TAG = LogUtil.makeDebugLogTag(FullImageActivity.class);
	private ImageView fullImageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
		createFullView();
	}
	
	private void createFullView(){
		RelativeLayout relativeLayout = new RelativeLayout(this);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		setContentView(relativeLayout,params);
		
		ImageView backButton = new ImageView(this);
		LayoutParams backParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		backParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		backButton.setPadding(0, 10, 10, 0);
		try {
			Bitmap titleBackBitmap = BitmapFactory.decodeStream((InputStream)(getAssets().open("ccimage/shotcut.png")));
			backButton.setImageBitmap(titleBackBitmap);
		} catch (IOException e) {
			DebugLog.d(TAG, "decode back.png exception",e);
		}
		
		backButton.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
//				Intent deleIntent = new Intent(getPackageName() + "." + Constants.ACTION_NOTIFICATION_CLEARED);
//		        deleIntent.putExtra(Constants.PUSH_INFO, mAdPush);
//		        sendBroadcast(deleIntent);
//		        
//		        try {
//		        	UserStepReportUtil.reportStep(FullImageActivity.this, Integer.parseInt(mAdPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_ADD_APP_ICON_TO_DESK);
//		        	Bitmap bitmap = null;
//		        	String filePath = OSharedPreferences.getResPathByDownLoadUrl(FullImageActivity.this, mAdPush.mCurrentAdInfo.iconUrl);
//					if(!StringUtils.isEmpty(filePath) && AndroidUtil.checkFileExists(filePath)){
//		        		bitmap = BitmapFactory.decodeFile(filePath);
//		        	}else{
//		        		bitmap = BitmapFactory.decodeStream(getAssets().open("default_icon.png"));
//		        	}
//					AdPush.setCurrentjsonInfo(mAdPush, mAdPush.mCurrentAdInfo);
//					Intent intent = new Intent();
//			        intent.setClassName(getPackageName(), FullImageActivity.class.getName());
//			        intent.setAction(Intent.ACTION_VIEW);
//			        intent.putExtra(Constants.PUSH_INFO_FOR_JSON, mAdPush.adContentJson);
//			        intent.putExtra(Constants.NOTIFICATION_ACTION_MODE, Constants.NOTIFICATION_ACTION_MODE_DISK);
//					AndroidUtil.createShortCut(FullImageActivity.this, intent,mAdPush.mCurrentAdInfo.title, bitmap);
//				} catch (IOException e) {
//					DebugLog.e(TAG, "",e);
//				}
				finish();
			}
		});
		
		
		fullImageView = new ImageView(this);
		fullImageView.setScaleType(ScaleType.FIT_XY);
		LayoutParams fullParams = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		mHandler.sendEmptyMessage(0);
		
		relativeLayout.addView(fullImageView, fullParams);
		relativeLayout.addView(backButton, backParams);
	}
	
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case 0:
					try {
						Bitmap titleBackBitmap = BitmapFactory.decodeStream((InputStream)(getAssets().open("ccimage/retry.jpg")));
						fullImageView.setImageBitmap(titleBackBitmap);
						fullImageView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								
							}
						});
					} catch (IOException e) {
						DebugLog.d(TAG, "decode back.png exception",e);
					}
					break;
				case 1:
					
				    break;
			}
		};
	};
}
