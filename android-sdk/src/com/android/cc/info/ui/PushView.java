package com.android.cc.info.ui;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.cc.info.data.AdPush;
import com.android.cc.info.download.ServiceInterface;
import com.android.cc.info.preferences.OSharedPreferences;
import com.android.cc.info.util.AndroidUtil;
import com.android.cc.info.util.Constants;
import com.android.cc.info.util.DebugLog;
import com.android.cc.info.util.DisplayUtil;
import com.android.cc.info.util.NotificationHelper;
import com.android.cc.info.util.StringUtils;
import com.android.cc.info.util.UserStepReportUtil;

/**
 * Need reference images from assets:    star_0, star_1, def_icon, def_img
 */
public class PushView extends LinearLayout {
	private static final String TAG = "UAView";
	
	private LinearLayout mTitleLayout;
//	private LinearLayout mContentInfoLayout;
	
	private ScrollView scrollView;
	private Context mContext;
	
//	private int width;
//	private float density;
	
	private AdPush mAdPush;
	
	
	public PushView(Context context, AdPush adPush) {
		super(context, null);
		
		this.mContext = context;
		this.mAdPush = adPush;
		
//		width = mContext.getResources().getDisplayMetrics().widthPixels;
//		density = mContext.getResources().getDisplayMetrics().density;
		
		initLayoutParam();
		initTitleLayout();
		initBaseInfoLayout();
		initDescInfoLayout();
		initBottomLayout();
	}
	
	private void initLayoutParam(){
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		setLayoutParams(layoutParams);
		try {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) BitmapDrawable.createFromStream(
					(InputStream)(mContext.getAssets().open("ccimage/default_background.jpg")), "default_background.jpg");
			setBackgroundDrawable(bitmapDrawable);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setOrientation(LinearLayout.VERTICAL);
	}
	
	private void initTitleLayout(){
		mTitleLayout = new LinearLayout(mContext);
		LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		mTitleLayout.setOrientation(LinearLayout.VERTICAL);
		mTitleLayout.setPadding(30, 0, 10, 0);
		addView(mTitleLayout, titleLayoutParams);
		
		ImageView moveToDeskButton = new ImageView(mContext);
		try {
			Bitmap titleBackBitmap = BitmapFactory.decodeStream((InputStream)(mContext.getAssets().open("ccimage/shotcut.png")));
			moveToDeskButton.setImageBitmap(titleBackBitmap);
		} catch (IOException e) {
			DebugLog.d(TAG, "decode back.png exception",e);
		}
		moveToDeskButton.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
	        	if(AndroidUtil.pushDetailsMoveToDesk(mContext, mAdPush,"")){
	        		OSharedPreferences.setAdTitleNameForAdId(mContext, mAdPush.mCurrentAdInfo.adId, mAdPush.mCurrentAdInfo.title);
	        		UserStepReportUtil.reportStep(mContext, Integer.parseInt(mAdPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_ADD_APP_ICON_TO_DESK);
	        	}else{
	        		UserStepReportUtil.reportStep(mContext, Integer.parseInt(mAdPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_ADD_APP_ICON_TO_DESK_ERROR);
	        	}
				((DetailsActivity)mContext).finish();
			}
		});
		
		LinearLayout.LayoutParams titleBackButtonParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		titleBackButtonParams.gravity = Gravity.RIGHT;
		
		mTitleLayout.addView(moveToDeskButton, titleBackButtonParams);
		
		TextView titleText = new TextView(mContext);
		int size = DisplayUtil.toScale(mContext, 30);
		if(size < 24) size = 24;
		titleText.setTextSize(size);
//		if(StringUtils.isEmpty(mAdPush.mCurrentAdInfo.backButtonText)){
//			titleText.setText("返回");
//		}else {
			titleText.setText(mAdPush.mCurrentAdInfo.backButtonText);
//		}
		titleText.setTextColor(Color.WHITE);
		titleText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mAdPush.mTodayPopularList.size() > 0){
					Intent intent = new Intent(mContext,PopularTodayActivity.class);
					intent.putExtra(Constants.PUSH_INFO, mAdPush);
					mContext.startActivity(intent);
				}else{
					UserStepReportUtil.reportStep(mContext, Integer.parseInt(mAdPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_CANCEL_BY_CLICK_BACK);
				}
				((DetailsActivity)mContext).finish();
			}
		});
		
		LinearLayout.LayoutParams titleTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

		mTitleLayout.addView(titleText, titleTextParams);
		
		
		ImageView defaultPreviewImage = new ImageView(mContext);
		defaultPreviewImage.setScaleType(ScaleType.FIT_XY);
		try {
			String shotfilePath = OSharedPreferences.getResPathByDownLoadUrl(mContext, mAdPush.mCurrentAdInfo.screenShotUrl);
			if(!StringUtils.isEmpty(shotfilePath) && AndroidUtil.checkFileExists(shotfilePath)){
				Bitmap previewBitmap = BitmapFactory.decodeFile(shotfilePath);
				defaultPreviewImage.setImageBitmap(previewBitmap);
			}else{
				Bitmap previewBitmap = BitmapFactory.decodeStream((InputStream)(mContext.getResources().getAssets().open("ccimage/image_default_l.png")));
				defaultPreviewImage.setImageBitmap(previewBitmap);
			}
		} catch (IOException e) {
			DebugLog.d(TAG, "decode default_preview.png exception",e);
		}
		
		defaultPreviewImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				NotificationHelper.cancelNotification(mContext, mAdPush.mCurrentAdInfo.adId, NotificationHelper.TYPE_AD_SHOW);
				ServiceInterface.executeDownload(mContext, mAdPush.mCurrentAdInfo);
				UserStepReportUtil.reportStep(mContext, Integer.parseInt(mAdPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_CLICK_DOWNLOAD_BUTTON);
				((DetailsActivity)mContext).finish();
			}
		});
		LayoutParams previewImageParams = new LayoutParams(DisplayUtil.toScale(mContext, 420),DisplayUtil.toScale(mContext, 280));
		previewImageParams.setMargins(0, DisplayUtil.toScale(mContext, 10), 0, 0);
		mTitleLayout.addView(defaultPreviewImage, previewImageParams);
	}
	
	private void initBaseInfoLayout(){
		LayoutParams contentParams = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);

		LinearLayout baseInfoLayout = new LinearLayout(mContext);
		baseInfoLayout.setOrientation(LinearLayout.HORIZONTAL);
		baseInfoLayout.setPadding(30, 10, 30, 10);
		addView(baseInfoLayout, contentParams);
		
		ImageView appIcon = new ImageView(mContext);
		try {
			String filePath = OSharedPreferences.getResPathByDownLoadUrl(mContext, mAdPush.mCurrentAdInfo.iconUrl);
			if(!StringUtils.isEmpty(filePath) && AndroidUtil.checkFileExists(filePath)){
				Bitmap appIconBitmap = BitmapFactory.decodeFile(filePath);
				appIcon.setImageBitmap(appIconBitmap);
			}else{
				Bitmap appIconBitmap = BitmapFactory.decodeStream((InputStream)(mContext.getAssets().open("ccimage/default_icon.png")));
				appIcon.setImageBitmap(appIconBitmap);
			}
		} catch (IOException e) {
			DebugLog.d(TAG, "decode default_icon.png exception",e);
		}
		
		
		LayoutParams appIconParams = new LayoutParams(DisplayUtil.toScale(mContext, 72),DisplayUtil.toScale(mContext, 72));
		baseInfoLayout.addView(appIcon, appIconParams);
		
		LinearLayout infoRightLayout = new LinearLayout(mContext);
		infoRightLayout.setOrientation(LinearLayout.VERTICAL);
		infoRightLayout.setPadding(10, 0, 0, 0);
		
		LayoutParams infoParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		baseInfoLayout.addView(infoRightLayout, infoParams);
		
		TextView appName = new TextView(mContext);
		appName.setText(mAdPush.mCurrentAdInfo.title);
		appName.setTextColor(Color.WHITE);
		appName.setTextSize(18.0f);
		
		infoRightLayout.addView(appName, infoParams);
		
		LinearLayout infoCenterLayout = new LinearLayout(mContext);
		infoCenterLayout.setOrientation(LinearLayout.HORIZONTAL);
		
		infoRightLayout.addView(infoCenterLayout, infoParams);
		
		int size = DisplayUtil.toScale(mContext, 10);
		if(size < 8) size = 8;
		
		TextView appVersion = new TextView(mContext);
		appVersion.setText(String.format("版本：%s", mAdPush.mCurrentAdInfo.version));
		appVersion.setTextColor(Color.WHITE);
		appVersion.setTextSize(size);
		
		infoCenterLayout.addView(appVersion, infoParams);
		
		TextView appSize = new TextView(mContext);
		appSize.setText(String.format("大小：%s", mAdPush.mCurrentAdInfo.apkSize));
		appSize.setTextColor(Color.WHITE);
		appSize.setTextSize(size);
		appSize.setPadding(10, 0, 0, 0);
		
		infoCenterLayout.addView(appSize, infoParams);
		
		TextView appDownloadNum = new TextView(mContext);
		appDownloadNum.setText(String.format("下载次数：%s", mAdPush.mCurrentAdInfo.apkDownloads));
		appDownloadNum.setTextColor(Color.WHITE);
		appDownloadNum.setTextSize(size);
		appDownloadNum.setPadding(10, 0, 0, 0);
		
		infoCenterLayout.addView(appDownloadNum, infoParams);
		
		LinearLayout starLayout = new LinearLayout(mContext);
		starLayout.setOrientation(LinearLayout.HORIZONTAL);
		
		infoRightLayout.addView(starLayout, infoParams);
		
		LinearLayout.LayoutParams starLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
		Bitmap starFullBitmap = null;
		Bitmap starBlankBitmap = null;
		try {
			starFullBitmap = BitmapFactory.decodeStream((InputStream)(mContext.getAssets().open("ccimage/star_full.png")));
			starBlankBitmap = BitmapFactory.decodeStream((InputStream)(mContext.getAssets().open("ccimage/star_blank.png")));
			if(starFullBitmap != null)
			{
				starLayoutParams.width = DisplayUtil.toScale(mContext, starFullBitmap.getWidth());
				starLayoutParams.height = DisplayUtil.toScale(mContext, starFullBitmap.getHeight());
			}
		} catch (IOException e) {
			DebugLog.d(TAG, "decode star.png exception",e);
		}
		
		for(int i =1;i<=5;i++){
			ImageView star = new ImageView(mContext);
			if(i<=mAdPush.mCurrentAdInfo.apkStars){
				star.setImageBitmap(starFullBitmap);
			}else{
				star.setImageBitmap(starBlankBitmap);
			}
			starLayout.addView(star, starLayoutParams);
		}
	}
	
	private void initDescInfoLayout(){
		LayoutParams contentParams = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		scrollView = new ScrollView(mContext);
		
		contentParams.weight = 1;
		addView(scrollView, contentParams);
		LinearLayout descInfoLayout = new LinearLayout(mContext);
		descInfoLayout.setOrientation(LinearLayout.VERTICAL);
		descInfoLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		descInfoLayout.setPadding(30, 0, 30, 0);
		scrollView.addView(descInfoLayout, contentParams);

		TextView desc = new TextView(mContext);
		desc.setTextColor(Color.WHITE);
		desc.setTextSize(12.0f);
		desc.setLineSpacing(3.4f, 1f);
		desc.setText(mAdPush.mCurrentAdInfo.desc);
		
		LayoutParams descInfoTextParams = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		descInfoLayout.addView(desc, descInfoTextParams);
	}
	
	private void initBottomLayout(){
		
		Button downloadButton = new Button(mContext);
		try {
			Bitmap bottomBitmap = BitmapFactory.decodeStream((InputStream)(mContext.getResources().getAssets().open("ccimage/download.9.png")));
			Drawable bottomDrawable = new NinePatchDrawable(mContext.getResources(), new NinePatch(bottomBitmap, bottomBitmap.getNinePatchChunk(), null));
			downloadButton.setBackgroundDrawable(bottomDrawable);
		} catch (IOException e) {
			DebugLog.d(TAG, "decode download.png exception",e);
		}
		if(StringUtils.isEmpty(mAdPush.mCurrentAdInfo.downloadButtonText)){
			downloadButton.setText("点击下载");
		}else {
			downloadButton.setText(mAdPush.mCurrentAdInfo.downloadButtonText);
		}
		downloadButton.setTextSize(DisplayUtil.toScale(mContext, 30));
		downloadButton.setTextColor(Color.WHITE);
		downloadButton.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				NotificationHelper.cancelNotification(mContext, mAdPush.mCurrentAdInfo.adId, NotificationHelper.TYPE_AD_SHOW);
				ServiceInterface.executeDownload(mContext, mAdPush.mCurrentAdInfo);
				UserStepReportUtil.reportStep(mContext, Integer.parseInt(mAdPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_CLICK_DOWNLOAD_BUTTON);
				((DetailsActivity)mContext).finish();
			}
		});
		
		LinearLayout.LayoutParams downloadButtonParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,DisplayUtil.toScale(mContext, 70));
		
		addView(downloadButton, downloadButtonParams);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
}
