package com.android.cc.info.util;

import java.util.zip.Adler32;

import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

public class NotificationHelper {
	private static final String TAG = "NotificationHelper";
	
    public static final int TYPE_AD_SHOW = 0;
    public static final int TYPE_AD_DOWNLOADED = 1;
    
    public static void cleanAllNotification(Context context){
    	NotificationManager notificationManager =  (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
    	notificationManager.cancelAll();
    }
    
    public static void cancelNotification(Context context, int notifiId) {
    	DebugLog.d(TAG, "action:cleanNotification - notificationId:" + notifiId);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(notifiId);
    }
    
    public static void cancelNotification(Context context, String adId, int type) {
    	DebugLog.d(TAG, "action:cleanNotification - adId:" + adId);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notifiId = getNofiticationID(adId, type);
        nm.cancel(notifiId);
    }
    
    public static void cancelAllNotification(Context context, String adId) {
    	DebugLog.d(TAG, "action:cleanAllNotification - adId:" + adId);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int notifiId = getNofiticationID(adId, TYPE_AD_SHOW);
        nm.cancel(notifiId);
        notifiId = getNofiticationID(adId, TYPE_AD_DOWNLOADED);
        nm.cancel(notifiId);
    }
    
    public static int getNofiticationID(String adId, int idType) {
        if (TextUtils.isEmpty(adId)) {
            DebugLog.d(TAG, "action:getNofiticationID - empty adId");
            return 0;
        }
        int nId = 0;
        Adler32 adler32 = new Adler32();
        adler32.update(adId.getBytes());
        nId = (int) adler32.getValue();
        if (nId < 0) {
            nId = Math.abs(nId);
        }
        nId = nId + 13889152 * idType;
        
        if (nId < 0) {
        	nId = Math.abs(nId);
        }
        return nId;
    }
    
    public static int getNotifiIcon(int code,Context context) {
        int icon_id = android.R.drawable.ic_menu_share;
        switch (code) {
        case 0:
        	try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),
                        PackageManager.GET_GIDS);
                icon_id = packageInfo.applicationInfo.icon;
            } catch (NameNotFoundException e) {
                DebugLog.d(TAG, "", e);
                icon_id = android.R.drawable.ic_menu_share;
            }
            break;
        case 1:
            icon_id = android.R.drawable.sym_action_email;
            break;
        case 2:
            icon_id = android.R.drawable.ic_menu_share;
            break;
        case 3:
            icon_id = android.R.drawable.star_big_on;
            break;
        case 4:
            icon_id = android.R.drawable.ic_menu_gallery;
            break;
        }
        return icon_id;
    }
}
