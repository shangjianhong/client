package com.android.cc.info.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.cc.info.data.AdInfo;
import com.android.cc.info.data.AdPush;
import com.android.cc.info.preferences.OSharedPreferences;
import com.android.cc.info.util.AndroidUtil;
import com.android.cc.info.util.Constants;
import com.android.cc.info.util.DebugLog;
import com.android.cc.info.util.DisplayUtil;
import com.android.cc.info.util.StringUtils;
import com.android.cc.info.util.UserStepReportUtil;

public class PopularTodayView extends LinearLayout {
	
	private static final String TAG = "PopularTodayView";
	private Context mContext;
	private AdPush mAdPush;
	private LinearLayout mTitleLayout;
	private ArrayList<PopularInfo> otherPopularInfos;
	private PopularInfo todayHotInfo;
	private LinearLayout mOtherLayout_1;
	private LinearLayout mOtherLayout_2;
	
	public PopularTodayView(Context context,AdPush adPush) {
		super(context);
		
		
		this.mContext = context;
		this.mAdPush = adPush;
		otherPopularInfos = new ArrayList<PopularTodayView.PopularInfo>();
		todayHotInfo = new PopularInfo();
		int index = 1;
		for (AdInfo adInfo : mAdPush.mTodayPopularList) {
			if(adInfo.firstRecommend){
				String iconPath = OSharedPreferences.getResPathByDownLoadUrl(mContext, adInfo.iconUrl);
				if(AndroidUtil.checkFileExists(iconPath)) todayHotInfo.iconPath = iconPath;
				String imagePath = OSharedPreferences.getResPathByDownLoadUrl(mContext, adInfo.firstRecommendImage);
				if(AndroidUtil.checkFileExists(iconPath)) todayHotInfo.imagePath = imagePath;
				todayHotInfo.title = adInfo.title;
				todayHotInfo.desc = adInfo.desc;
				todayHotInfo.starNumber = adInfo.apkStars;
				todayHotInfo.mAdInfo = adInfo;
			}else{
				PopularInfo pInfo = new PopularInfo();
				String iconPath = OSharedPreferences.getResPathByDownLoadUrl(mContext, adInfo.iconUrl);
				if(AndroidUtil.checkFileExists(iconPath)) pInfo.iconPath = iconPath;
				String imagePath = OSharedPreferences.getResPathByDownLoadUrl(mContext, adInfo.screenShotUrl);
				if(AndroidUtil.checkFileExists(iconPath)) pInfo.imagePath = imagePath;
				pInfo.index = index;
				pInfo.title = adInfo.title;
				pInfo.starNumber = adInfo.apkStars;
				pInfo.mAdInfo = adInfo;
				otherPopularInfos.add(pInfo);
				index++;
			}
		}
		
		// 初始化布局信息
		initLayoutParam();
		// 初始化头部信息
		initTitleLayout();
		
		initPopularLayout();
		
		otherPopularLayout();
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
		setGravity(Gravity.CENTER_HORIZONTAL);
	}
	
	private void initTitleLayout(){
		mTitleLayout = new LinearLayout(mContext);
		LinearLayout.LayoutParams titleLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		mTitleLayout.setOrientation(LinearLayout.VERTICAL);
		mTitleLayout.setPadding(30, DisplayUtil.toScale(mContext, 10), DisplayUtil.toScale(mContext, 10), 0);
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
				if(AndroidUtil.popularTodayMoveToDesk(mContext,mAdPush)){
					UserStepReportUtil.reportStep(mContext, Integer.parseInt(mAdPush.mCurrentAdInfo.adId), UserStepReportUtil.AD_PUSH_ADD_POPULAR_ICON_TO_DESK);
				}else{
					
				}
				((PopularTodayActivity)mContext).finish();
			}
		});
		
		LinearLayout.LayoutParams titleBackButtonParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		titleBackButtonParams.gravity = Gravity.RIGHT;
		
		mTitleLayout.addView(moveToDeskButton, titleBackButtonParams);
		
		TextView titleText = new TextView(mContext);
		int size = DisplayUtil.toScale(mContext, 30);
		if(size < 24) size = 24;
		titleText.setTextSize(size);
		if(StringUtils.isEmpty(mAdPush.mCurrentAdInfo.popularTitleText)){
			titleText.setText("今日热门");
		}else {
			titleText.setText(mAdPush.mCurrentAdInfo.popularTitleText);
		}
		titleText.setTextColor(Color.WHITE);
		
		LinearLayout.LayoutParams titleTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

		mTitleLayout.addView(titleText, titleTextParams);
	}
	
    private void initPopularLayout(){
    	RelativeLayout backLayout = new RelativeLayout(mContext);
    	LinearLayout.LayoutParams backParams = new LinearLayout.LayoutParams(DisplayUtil.toScale(mContext, 420),DisplayUtil.toScale(mContext, 200));
    	addView(backLayout,backParams);
    	backLayout.setTag(todayHotInfo);
    	backLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PopularInfo pInfo = (PopularInfo)v.getTag();
				if(pInfo != null){
					try {
						Intent intent = new Intent(mContext,DetailsActivity.class);
						mAdPush.mCurrentAdInfo = pInfo.mAdInfo;
						JSONObject jsonObject;
						jsonObject = new JSONObject(mAdPush.adContentJson);
					
						jsonObject.put("CURRENT_AD_INFO", pInfo.mAdInfo.adContentJson);
						mAdPush.adContentJson = jsonObject.toString();
						intent.putExtra(Constants.PUSH_INFO, mAdPush.adContentJson);
						intent.putExtra(Constants.NOTIFICATION_ACTION_MODE, Constants.NOTIFICATION_ACTION_MODE_POPULAR);
						mContext.startActivity(intent);
					} catch (Exception e) {
						DebugLog.d(TAG, "onlick error;\n ",e);
					}
					((PopularTodayActivity)mContext).finish();
				}
			}
		});
    	
    	ImageView defaultPreviewImage = new ImageView(mContext);
		defaultPreviewImage.setScaleType(ScaleType.FIT_XY);
		try {
			
			if(!StringUtils.isEmpty(todayHotInfo.imagePath)){
				Bitmap previewBitmap = BitmapFactory.decodeFile(todayHotInfo.imagePath);
				defaultPreviewImage.setImageBitmap(previewBitmap);
			}else{
				Bitmap previewBitmap = BitmapFactory.decodeStream((InputStream)(mContext.getResources().getAssets().open("ccimage/image_default_ls.png")));
				defaultPreviewImage.setImageBitmap(previewBitmap);
			}
		} catch (IOException e) {
			DebugLog.d(TAG, "decode default_preview.png exception",e);
		}
		RelativeLayout.LayoutParams previewImageParams = new RelativeLayout.LayoutParams(DisplayUtil.toScale(mContext, 420),DisplayUtil.toScale(mContext, 200));
		previewImageParams.setMargins(0, DisplayUtil.toScale(mContext, 10), 0, 0);
		backLayout.addView(defaultPreviewImage, previewImageParams);
		
//		ImageView flameImage = new ImageView(mContext);
//		flameImage.setScaleType(ScaleType.FIT_XY);
//		flameImage.setAlpha(180);
//		try {
//			Bitmap previewBitmap = BitmapFactory.decodeStream((InputStream)(mContext.getResources().getAssets().open("img_flame.9.png")));
//			Drawable drawable = new NinePatchDrawable(mContext.getResources(), new NinePatch(previewBitmap, previewBitmap.getNinePatchChunk(), null));
//			flameImage.setImageDrawable(drawable);
//		} catch (IOException e) {
//			DebugLog.d(TAG, "decode default_preview.png exception",e);
//		}
//		backLayout.addView(flameImage, previewImageParams);
		
		RelativeLayout bottomContent = new RelativeLayout(mContext);
		RelativeLayout.LayoutParams bottomContentParams = new RelativeLayout.LayoutParams(DisplayUtil.toScale(mContext, 420),DisplayUtil.toScale(mContext, 88));
		bottomContentParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		backLayout.addView(bottomContent, bottomContentParams);
		
		LinearLayout bottomBar = new LinearLayout(mContext);
		bottomBar.setBackgroundColor(Color.argb(200, 0, 0, 0));
		bottomBar.setOrientation(LinearLayout.VERTICAL);
		RelativeLayout.LayoutParams bottomParams = new RelativeLayout.LayoutParams(DisplayUtil.toScale(mContext, 420),DisplayUtil.toScale(mContext, 70));
		bottomParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		bottomBar.setPadding(DisplayUtil.toScale(mContext, 90), 0, 0, 0);
		bottomContent.addView(bottomBar, bottomParams);
		
		ImageView iconImage = new ImageView(mContext);
		iconImage.setScaleType(ScaleType.FIT_XY);
		try {
			if(!StringUtils.isEmpty(todayHotInfo.iconPath)){
				Bitmap previewBitmap = BitmapFactory.decodeFile(todayHotInfo.iconPath);
				iconImage.setImageBitmap(previewBitmap);
			}else{
				Bitmap previewBitmap = BitmapFactory.decodeStream((InputStream)(mContext.getResources().getAssets().open("ccimage/default_icon.png")));
				iconImage.setImageBitmap(previewBitmap);
			}
		} catch (IOException e) {
			DebugLog.d(TAG, "decode app_icon.png exception",e);
		}
		RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(DisplayUtil.toScale(mContext, 72),DisplayUtil.toScale(mContext, 72));
		iconParams.setMargins(DisplayUtil.toScale(mContext,10), 0, DisplayUtil.toScale(mContext,10), 0);
		bottomContent.addView(iconImage, iconParams);
		
		LinearLayout title = new LinearLayout(mContext);
		title.setOrientation(LinearLayout.HORIZONTAL);
		title.setGravity(Gravity.CENTER_VERTICAL);
		LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		bottomBar.addView(title, titleParams);
		
		TextView appName = new TextView(mContext);
		appName.setText(todayHotInfo.title);
		appName.setSingleLine();
		appName.setTextColor(Color.WHITE);
		appName.setTextSize(16.0f);
		title.addView(appName);
		
		LinearLayout starLayout = new LinearLayout(mContext);
		starLayout.setOrientation(LinearLayout.HORIZONTAL);
		starLayout.setPadding(DisplayUtil.toScale(mContext, 10), 0, 0, 0);
		title.addView(starLayout, titleParams);
		
		Bitmap starFullBitmap = null;
		Bitmap starBlankBitmap = null;
		
		LinearLayout.LayoutParams starLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		
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
			if(i<=todayHotInfo.starNumber){
				star.setImageBitmap(starFullBitmap);
			}else{
				star.setImageBitmap(starBlankBitmap);
			}
			starLayout.addView(star, starLayoutParams);
		}
		
		TextView decs = new TextView(mContext);
		decs.setTextColor(Color.WHITE);
		decs.setTextSize(9.0f);
		decs.setPadding(0, 0, 10, 0);
		decs.setMaxLines(2);
		decs.setMaxEms(10);
		decs.setEllipsize(TruncateAt.END);
		decs.setText(todayHotInfo.desc);
		bottomBar.addView(decs);
    }
    
    private void otherPopularLayout(){
    	mOtherLayout_1 = new LinearLayout(mContext);
    	mOtherLayout_1.setGravity(Gravity.CENTER_VERTICAL);
    	mOtherLayout_2 = new LinearLayout(mContext);
    	mOtherLayout_2.setGravity(Gravity.CENTER_VERTICAL);
    	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DisplayUtil.toScale(mContext, 420),DisplayUtil.toScale(mContext, 200));
    	layoutParams.setMargins(0, DisplayUtil.toScale(mContext, 20), 0, 0);
    	addView(mOtherLayout_1, layoutParams);
    	addView(mOtherLayout_2, layoutParams);
    	for(int i = 0; i < otherPopularInfos.size() && i < 4 ; i++){
    		creatOtherLayout(otherPopularInfos.get(i));
    	}
    }
    
    private void creatOtherLayout(PopularInfo popularInfo){
    	if(popularInfo != null){
    		LinearLayout.LayoutParams otherParams;
    		RelativeLayout layout = new RelativeLayout(mContext);
    		switch (popularInfo.index) {
				case 1:
					otherParams = new LinearLayout.LayoutParams(DisplayUtil.toScale(mContext, 200),DisplayUtil.toScale(mContext, 200));
					otherParams.setMargins(0, 0, DisplayUtil.toScale(mContext, 20), 0);					
					mOtherLayout_1.addView(layout, otherParams);
					break;
				case 2:
					otherParams = new LinearLayout.LayoutParams(DisplayUtil.toScale(mContext, 200),DisplayUtil.toScale(mContext, 200));
					mOtherLayout_1.addView(layout, otherParams);
					break;
				case 3:
					otherParams = new LinearLayout.LayoutParams(DisplayUtil.toScale(mContext, 200),DisplayUtil.toScale(mContext, 200));
					otherParams.setMargins(0, 0, DisplayUtil.toScale(mContext, 20), 0);
					mOtherLayout_2.addView(layout, otherParams);
					break;
				case 4:
					otherParams = new LinearLayout.LayoutParams(DisplayUtil.toScale(mContext, 200),DisplayUtil.toScale(mContext, 200));
					mOtherLayout_2.addView(layout, otherParams);
					break;
			}
    		ImageView imageView = new ImageView(mContext);
    		imageView.setScaleType(ScaleType.MATRIX);
    		try {
    			if(!StringUtils.isEmpty(popularInfo.imagePath)){
    				Bitmap previewBitmap = BitmapFactory.decodeFile(popularInfo.imagePath);
    				imageView.setImageBitmap(previewBitmap);
    			}else{
    				Bitmap previewBitmap = BitmapFactory.decodeStream((InputStream)(mContext.getResources().getAssets().open("ccimage/image_default_s.png")));
    				imageView.setImageBitmap(previewBitmap);
    			}
    		} catch (IOException e) {
    			DebugLog.d(TAG, "decode default_preview.png exception",e);
    		}
    		RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(DisplayUtil.toScale(mContext, 200),DisplayUtil.toScale(mContext, 200));
    		layout.addView(imageView,imageParams);
    		
//    		ImageView flameImage = new ImageView(mContext);
//    		flameImage.setScaleType(ScaleType.FIT_XY);
//    		flameImage.setAlpha(80);
//    		try {
//    			Bitmap previewBitmap = BitmapFactory.decodeStream((InputStream)(mContext.getResources().getAssets().open("img_flame.9.png")));
//    			Drawable drawable = new NinePatchDrawable(mContext.getResources(), new NinePatch(previewBitmap, previewBitmap.getNinePatchChunk(), null));
//    			flameImage.setImageDrawable(drawable);
//    		} catch (IOException e) {
//    			DebugLog.d(TAG, "decode default_preview.png exception",e);
//    		}
//    		layout.addView(flameImage, imageParams);
    		
    		RelativeLayout bottomContent = new RelativeLayout(mContext);
    		RelativeLayout.LayoutParams bottomContentParams = new RelativeLayout.LayoutParams(DisplayUtil.toScale(mContext, 420),DisplayUtil.toScale(mContext, 88));
    		bottomContentParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    		layout.addView(bottomContent, bottomContentParams);
    		
    		LinearLayout bottomBar = new LinearLayout(mContext);
    		bottomBar.setBackgroundColor(Color.argb(200, 0, 0, 0));
    		bottomBar.setOrientation(LinearLayout.VERTICAL);
    		RelativeLayout.LayoutParams bottomParams = new RelativeLayout.LayoutParams(DisplayUtil.toScale(mContext, 420),DisplayUtil.toScale(mContext, 70));
    		bottomParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    		bottomBar.setGravity(Gravity.CENTER_VERTICAL);
    		bottomBar.setPadding(DisplayUtil.toScale(mContext, 90), 0, 0, 0);
    		bottomContent.addView(bottomBar, bottomParams);
    		
    		ImageView iconImage = new ImageView(mContext);
    		iconImage.setScaleType(ScaleType.FIT_XY);
    		try {
    			if(!StringUtils.isEmpty(popularInfo.iconPath)){
    				Bitmap previewBitmap = BitmapFactory.decodeFile(popularInfo.iconPath);
    				iconImage.setImageBitmap(previewBitmap);
    			}else{
    				Bitmap previewBitmap = BitmapFactory.decodeStream((InputStream)(mContext.getResources().getAssets().open("ccimage/default_icon.png")));
    				iconImage.setImageBitmap(previewBitmap);
    			}
    		} catch (IOException e) {
    			DebugLog.d(TAG, "decode app_icon.png exception",e);
    		}
    		RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(DisplayUtil.toScale(mContext, 72),DisplayUtil.toScale(mContext, 72));
    		iconParams.setMargins(DisplayUtil.toScale(mContext,10), 0, DisplayUtil.toScale(mContext,10), 0);
    		bottomContent.addView(iconImage, iconParams);
    		
    		TextView appName = new TextView(mContext);
    		appName.setText(popularInfo.title);
    		appName.setTextColor(Color.WHITE);
    		appName.setSingleLine();
    		appName.setMaxEms(6);
    		appName.setEllipsize(TruncateAt.END);
    		appName.setTextSize(12.0f);
    		bottomBar.addView(appName);
    		
    		LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
    		
    		LinearLayout starLayout = new LinearLayout(mContext);
    		starLayout.setOrientation(LinearLayout.HORIZONTAL);
    		bottomBar.addView(starLayout, titleParams);
    		
    		Bitmap starFullBitmap = null;
    		Bitmap starBlankBitmap = null;
    		
    		LinearLayout.LayoutParams starLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
    		
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
    			if(i<=popularInfo.starNumber){
    				star.setImageBitmap(starFullBitmap);
    			}else{
    				star.setImageBitmap(starBlankBitmap);
    			}
    			starLayout.addView(star, starLayoutParams);
    		}
    		layout.setTag(popularInfo);
    		layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					PopularInfo pInfo = (PopularInfo)v.getTag();
					if(pInfo != null){
						Intent intent = new Intent(mContext,DetailsActivity.class);
						try {
							JSONObject jsonObject = new JSONObject(mAdPush.adContentJson);
							jsonObject.put("CURRENT_AD_INFO", pInfo.mAdInfo.adContentJson);
							mAdPush.adContentJson = jsonObject.toString();
							intent.putExtra(Constants.PUSH_INFO, mAdPush.adContentJson);
							intent.putExtra(Constants.NOTIFICATION_ACTION_MODE, Constants.NOTIFICATION_ACTION_MODE_POPULAR);
							mContext.startActivity(intent);
						} catch (Exception e) {
							DebugLog.e(TAG, "onClick error,\n",e);
						}
						((PopularTodayActivity)mContext).finish();
					}
				}
			});
    	}
    }
    
    class PopularInfo{
    	public int index = 0;
    	public String iconPath = "";
    	public String imagePath = "";
    	public String title = "";
    	public String desc = "";
    	public int starNumber = 0;
    	public boolean isHot = false;
    	public AdInfo mAdInfo;
    	public String recommendPath;
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
