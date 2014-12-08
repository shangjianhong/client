package com.android.cc.info.util;

import android.content.Context;

/**
 * Android大小单位转换工具类
 * 
 * @author wader
 * 
 */
public class DisplayUtil {
 /**
  * 将px值转换为dip或dp值，保证尺寸大小不变
  * 
  * @param pxValue
  * @param scale（DisplayMetrics类中属性density）
  * @return
  */
 public static int px2dip(float pxValue, float scale) {
  return (int) (pxValue / scale + 0.5f);
 }
 /**
  * 将dip或dp值转换为px值，保证尺寸大小不变
  * 
  * @param dipValue
  * @param scale（DisplayMetrics类中属性density）
  * @return
  */
 public static int dip2px(float dipValue, float scale) {
  return (int) (dipValue * scale + 0.5f);
 }
 /**
  * 将px值转换为sp值，保证文字大小不变
  * 
  * @param pxValue
  * @param fontScale（DisplayMetrics类中属性scaledDensity）
  * @return
  */
 public static int px2sp(float pxValue, float fontScale) {
  return (int) (pxValue / fontScale + 0.5f);
 }
 /**
  * 将sp值转换为px值，保证文字大小不变
  * 
  * @param spValue
  * @param fontScale（DisplayMetrics类中属性scaledDensity）
  * @return
  */
 public static int sp2px(float spValue, float fontScale) {
  return (int) (spValue * fontScale + 0.5f);
 }
 
 public static int toScale(Context context,int width){
	 int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
	 int heightPixels = context.getResources().getDisplayMetrics().heightPixels;
	 float widthScale = widthPixels/480.0f;
	 float heightScale  = heightPixels/800.0f;
	 return  widthScale<heightScale ? (int)(width*widthScale) : (int)(width*heightScale);
 }
 
}
