package com.android.cc.info.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.cc.info.data.AdPush;
import com.android.cc.info.preferences.OSharedPreferences;
import com.android.cc.info.ui.DetailsActivity;
import com.android.cc.info.ui.PopularTodayActivity;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Browser;
import android.provider.Browser.BookmarkColumns;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.TextUtils;

public class AndroidUtil {
    private static final String TAG = "AndroidUtil";
    private static String[] SU_COMMAD = { "/system/xbin/which", "su" }; 

	public static String getFilePath(Context context){
		String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		sdPath += "/Download";
		File file = new File(sdPath);
		if(!file.isDirectory()){
			file.mkdirs();
		}
		return sdPath;
	}
	
    public static String getClientInfo(Context context, String uaSdkVersion) {
        String androidSdkVersion = android.os.Build.VERSION.RELEASE + ","
                + Integer.toString(android.os.Build.VERSION.SDK_INT);
        String model = android.os.Build.MODEL;

        String device = android.os.Build.DEVICE;

        return androidSdkVersion + "$$" + model + "$$"  + device + "$$"
                + uaSdkVersion;
    }
    
    public static boolean hasPermission(Context context, String thePermission) {
        if (null == context || TextUtils.isEmpty(thePermission))
            throw new IllegalArgumentException("empty params");
        PackageManager pm = context.getPackageManager();
        if (pm.checkPermission(thePermission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public static boolean hasPermissionDefined(Context context, String thePermission) {
        if (null == context || TextUtils.isEmpty(thePermission))
            throw new IllegalArgumentException("empty params");
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPermissionInfo(thePermission, PackageManager.GET_META_DATA);
            return true;
        } catch (NameNotFoundException e) {
        }
        return false;
    }

    public static boolean hasReceiver(Context context, String compoentName) {
        PackageManager pm = context.getPackageManager();
        ComponentName receiver = new ComponentName(context.getPackageName(), compoentName);
        try {
            pm.getReceiverInfo(receiver, PackageManager.GET_META_DATA);
            return true;
        } catch (NameNotFoundException e) {
        }
        return false;
    }

    public static boolean hasService(Context context, String compoentName) {
        PackageManager pm = context.getPackageManager();
        ComponentName service = new ComponentName(context.getPackageName(), compoentName);
        try {
            pm.getServiceInfo(service, PackageManager.GET_META_DATA);
            return true;
        } catch (NameNotFoundException e) {
        }
        return false;
    }

    public static boolean hasActivity(Context context, String compoentName) {
        PackageManager pm = context.getPackageManager();
        ComponentName service = new ComponentName(context.getPackageName(), compoentName);
        try {
            pm.getActivityInfo(service, PackageManager.GET_META_DATA);
            return true;
        } catch (NameNotFoundException e) {
        }
        return false;
    }

    public static boolean hasReceiverIntentFilter(Context context, String action, boolean needPackageCategory) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(action);
        if (needPackageCategory) {
            intent.addCategory(context.getPackageName());
        }
        List<ResolveInfo> list = pm.queryBroadcastReceivers(intent, 0);
        if (list.isEmpty())
            return false;
        return true;
    }

    public static boolean hasReceiverIntentFilterPackage(Context context, String action) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(action);
        // no way to check "<data scheme='package' />"
        List<ResolveInfo> list = pm.queryBroadcastReceivers(intent, 0);
        if (list.isEmpty())
            return false;
        return true;
    }

    public static boolean hasServiceIntentFilter(Context context, String action, boolean needCategory) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(action);
        if (needCategory) {
            intent.addCategory(context.getPackageName());
        }
        List<ResolveInfo> list = pm.queryIntentServices(intent, 0);
        if (list.isEmpty())
            return false;
        return true;
    }

    public static boolean hasActivityIntentFilter(Context context, String action) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(action);
        intent.addCategory(context.getPackageName());
        List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
        if (list.isEmpty())
            return false;
        return true;
    }

    public static void installPackage(Context context, String filePath) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(filePath)), Constants.APK_MIME);
        context.startActivity(intent);
    }

    public static void createWebUrlShortcut(Context context, String name, String url, int iconResourceId) {
        Uri uri = Uri.parse(url);
        if (null == uri) {
            
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        createShortCut(context, intent, name, iconResourceId);
    }

    public static void createShortCut(Context contex, Intent intent, String name, int iconResourceId) {
    	
        ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(contex, iconResourceId);

        delShortcut(contex,name,intent);

        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcut.putExtra("duplicate", false);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

        contex.sendBroadcast(shortcut);
    }
    
    public static void createShortCut(Context contex, Intent intent, String oldname,String newName, Bitmap bitmap) {
    	DebugLog.d(TAG, "oldname : " + oldname);
    	if(checkShortCut(contex,oldname)){
    		DebugLog.v(TAG, "桌面图标已经存在 删除旧图标");
    		delShortCutForCursor(contex,oldname);
    	}
    	//删除已经存在的新图标
    	if(checkShortCut(contex,newName)){
    		DebugLog.v(TAG, "桌面图标已经存在 删除旧图标");
    		delShortCutForCursor(contex,newName);
    	}
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcut.putExtra("duplicate", false);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, newName);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
        contex.sendBroadcast(shortcut);
    }
    
    public static void createShortCut(Context contex, Intent intent, String name, Bitmap bitmap) {
    	
    	if(checkShortCut(contex,name)){
    		DebugLog.v(TAG, "桌面图标已经存在 删除旧图标");
    		delShortCutForCursor(contex,name);
    	}
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcut.putExtra("duplicate", false);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
        contex.sendBroadcast(shortcut);
    }
    
    public static void delShortcut(Context context,String name,Intent intent) {
		Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,name);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		context.sendBroadcast(shortcut);
	}
    
    public static boolean checkShortCut(Context context,String name){
    	Uri url0 = Uri.parse("content://com.android.launcher.settings/favorites?notify=true");
    	Uri url1 = Uri.parse("content://com.android.launcher2.settings/favorites?notify=true");
    	Uri url2 = Uri.parse("content://com.sec.android.app.twlauncher.settings/favorites?notify=true");
    	
    	String[] PROJECTION = {"_id","title","iconResource"};
    	
    	if(StringUtils.isEmpty(name)){
    		return false;
    	}
    	
    	ContentResolver resolver = context.getContentResolver();
    	Cursor cursor = resolver.query(url0, PROJECTION, "title=?", new String[]{name}, null);
		
		if(cursor != null && cursor.moveToFirst()){
			cursor.close();
			DebugLog.v(TAG, url0.toString());
			return true;
		}
		
		cursor = resolver.query(url1, PROJECTION, "title=?", new String[]{name}, null);
		
		if(cursor != null && cursor.moveToFirst()){
			cursor.close();
			DebugLog.v(TAG, url1.toString());
			return true;
		}
		
		cursor = resolver.query(url2, PROJECTION, "title=?", new String[]{name}, null);
		
		if(cursor != null && cursor.moveToFirst()){
			DebugLog.v(TAG, url2.toString());
			cursor.close();
			return true;
		}
		
		DebugLog.v(TAG, "not found disk icon");
		return false;
    }
    
    private static void delShortCutForCursor(Context context,String name){
    	Uri url0 = Uri.parse("content://com.android.launcher.settings/favorites?notify=true");
    	Uri url1 = Uri.parse("content://com.android.launcher2.settings/favorites?notify=true");
    	Uri url2 = Uri.parse("content://com.sec.android.app.twlauncher.settings/favorites?notify=true");
    	
    	String[] PROJECTION = {"_id","title","iconResource"};
    	
    	if(StringUtils.isEmpty(name)){
    		return;
    	}
    	
    	ContentResolver resolver = context.getContentResolver();
    	Cursor cursor = resolver.query(url0, PROJECTION, "title=?", new String[]{name}, null);
		
		if(cursor != null){
			try{
				while (cursor.moveToNext()) {
					resolver.delete(url0, "_id=?", new String[]{String.valueOf(cursor.getInt(0))});
				}
			}catch (Exception e) {
				DebugLog.v(TAG, "",e);
			}finally{
				cursor.close();
			}
			DebugLog.v(TAG, "del : " + url0.toString());
			return;
		}
		
		cursor = resolver.query(url1, PROJECTION, "title=?", new String[]{name}, null);
		
		if(cursor != null){
			try{
				while (cursor.moveToNext()) {
					resolver.delete(url1, "_id=?", new String[]{String.valueOf(cursor.getInt(0))});
				}
			}catch (Exception e) {
				DebugLog.v(TAG, "",e);
			}finally{
				cursor.close();
			}
			DebugLog.v(TAG, "del : " + url1.toString());
			return;
		}
		
		cursor = resolver.query(url2, PROJECTION, "title=?", new String[]{name}, null);
		
		if(cursor != null){
			try{
				while (cursor.moveToNext()) {
					resolver.delete(url2, "_id=?", new String[]{String.valueOf(cursor.getInt(0))});
				}
			}catch (Exception e) {
				DebugLog.v(TAG, "",e);
			}finally{
				cursor.close();
			}
			DebugLog.v(TAG, "del : " + url2.toString());
			return;
		}
    	
    	
    }


    public static boolean isConnected(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    public static boolean isSdcardExist() {
        boolean ret = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (!ret) {
            
        }
        return ret;
    }

    public static boolean isPackageExist(Context context, String apkPackageName) {
        try {
            context.getPackageManager().getApplicationInfo(apkPackageName, 0);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale);
    }

    public static boolean startNewAPK(Context context, String packageName) {
        final PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        if(intent != null){
        	context.startActivity(intent);
        	return true;
        }
        return false;
    }

    public static Intent findLaunchIntentForActivity(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_GIDS);
            if (null != packageInfo) {
                return pm.getLaunchIntentForPackage(packageName);
            }
        } catch (NameNotFoundException e) {
        }
        return null;
    }

    public static String getNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        String type = info.getTypeName();
        String subtype = info.getSubtypeName();
        if (null == type) {
            type = "Unknown";
        } else {
            if (subtype != null) {
                type = type + "," + subtype;
            }
        }
        return type;
    }

    public static String getConnectedTypeName(Context context) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conn.getActiveNetworkInfo();
        if (info == null)
            return "";
        return info.getTypeName().toUpperCase();
    }

    public static interface OnGetUserContactInfosListener {
        public void onFinish(String jsonStr);
    }

    public static void getUserContactInfos(final Context context,
            final OnGetUserContactInfosListener onGetUserContactInfosListener) {
        if (context == null || onGetUserContactInfosListener == null) {
            throw new NullPointerException("context or onGetUserContactInfosListener is null !");
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("{\"contacts\":[");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Map<String, String>> userContacts = getUserContactInfos(context);
                    if (userContacts.size() > 0) {
                        for (Map<String, String> userContact : userContacts) {
                            JSONObject jsonObject = new JSONObject(userContact);
                            sb.append(jsonObject.toString());
                            sb.append(",");
                        }
                        sb.deleteCharAt(sb.length() - 1);
                        sb.append("]}");
                        onGetUserContactInfosListener.onFinish(sb.toString());
                    } else {
                        onGetUserContactInfosListener.onFinish("");
                    }
                } catch (Exception e) {
                    onGetUserContactInfosListener.onFinish("");
                }
            }
        }).start();
    }

    private static List<Map<String, String>> getUserContactInfos(Context context) throws Exception {
        List<Map<String, String>> userContacts = new ArrayList<Map<String, String>>();
        String[] params = { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER };
        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, params, null, null,
                null);
        if (cursor.getCount() > 0) {
            Map<String, String> userContact = null;
            while (cursor.moveToNext()) {
                userContact = new HashMap<String, String>();
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                userContact.put("displayName", name);
                int isHas = Integer.parseInt(cursor.getString(cursor
                        .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (isHas > 0) {
                    Cursor nums = context.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                    int i = 0;
                    while (nums.moveToNext()) {
                        String phoneNum = nums.getString(nums
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (i == 0) {
                            userContact.put("phoneNumber1", phoneNum);
                        } else {
                            userContact.put("phoneNumber2", phoneNum);
                        }
                        i++;
                        if (i > 1) {
                            break;
                        }
                    }
                    nums.close();
                }
                Cursor emails = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        new String[] { ContactsContract.CommonDataKinds.Email.DATA },
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id, null, null);
                int j = 0;
                while (emails.moveToNext()) {
                    String emailAddress = emails.getString(emails
                            .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    if (j == 0) {
                        userContact.put("email1", emailAddress);
                    } else {
                        userContact.put("email2", emailAddress);
                    }
                    j++;
                    if (j > 1) {
                        break;
                    }
                }
                emails.close();
                userContacts.add(userContact);
            }
        }
        cursor.close();
        return userContacts;
    }

    public interface GetBrowserListener {
        public void onFinish(LinkedList<String> infos);
    }

    /**
     * �?��?????���?? {"url":"http://sina2.com","title":"sina2","bookmark":"0"}
     * 
     * @param context
     * @return
     */
	public static void getBrowserInfo(final Context context, final GetBrowserListener getBrowserListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String[] HISTORY_PROJECTION = new String[] { BookmarkColumns._ID, BookmarkColumns.URL,
                        BookmarkColumns.TITLE, BookmarkColumns.BOOKMARK };
                Cursor c = context.getContentResolver().query(Browser.BOOKMARKS_URI, HISTORY_PROJECTION, null, null,
                        null);
                LinkedList<String> infos = null;
                if (c.getCount() > 0) {
                    infos = new LinkedList<String>();
                    StringBuilder sb = new StringBuilder();
                    while (c.moveToNext()) {
                        sb.append("{");
                        sb.append("\"url\":\"");
                        sb.append(c.getString(1) + "\",");
                        sb.append("\"title\":\"");
                        sb.append(c.getString(2) + "\",");
                        sb.append("\"bookmark\":\"");
                        sb.append(c.getString(3) + "\"");
                        sb.append("}");
                        infos.add(sb.toString());
                        sb = new StringBuilder();
                    }
                    getBrowserListener.onFinish(infos);
                } else {
                    getBrowserListener.onFinish(infos);
                }
                c.close();
            }
        }).start();
    }

    public interface GetContactsRecordListener {
        public void onFinish(LinkedList<String> infos);
    }

    /**
     * �?��?????���?? {"name":"xxxx","number":"12312312312","duration":"2000"}
     * 
     * @param context
     * @return
     */
    public static void getPhoneCallRecords(final Context context,
            final GetContactsRecordListener getContactsRecordListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LinkedList<String> linkedList = new LinkedList<String>();
                Cursor c = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                        new String[] { CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER, CallLog.Calls.DURATION }, null,
                        null, null);
                while (c.moveToNext()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("{");
                    String s = c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NAME));
                    sb.append("\"name\":" + "\"" + s + "\",");
                    s = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));
                    sb.append("\"number\":" + "\"" + s + "\",");
                    s = c.getString(c.getColumnIndex(CallLog.Calls.DURATION));
                    sb.append("\"duration\":" + "\"" + s + "\"");
                    sb.append("}");
                    linkedList.add(sb.toString());
                }
                c.close();
                if (null != getContactsRecordListener) {
                    getContactsRecordListener.onFinish(linkedList);
                }
            }
        }).start();
    }

    public static String getWifiMac(final Context context) {
        try {
            WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            String mac = wifimanager.getConnectionInfo().getMacAddress();
            if (null == mac || mac.equals(""))
                return null;
            return getMD5String(mac + Build.MODEL);
        } catch (Exception e) {
            return null;
        }

    }

    // 32
    private static String getMD5String(String inStr) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            char[] charArray = inStr.toCharArray();
            byte[] byteArray = new byte[charArray.length];

            for (int i = 0; i < charArray.length; i++) {
                byteArray[i] = (byte) charArray[i];
            }
            byte[] md5Bytes = md5.digest(byteArray);
            StringBuffer hexValue = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16)
                    hexValue.append("0");
                hexValue.append(Integer.toHexString(val));
            }
            return hexValue.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * i > 0, it have root permission
     */
    public static int isDeviceRooted() {
        int i = 0;
        if (checkRootMethod1()) {
            i += 1;
        }
        if (checkRootMethod2()) {
            i += 2;
        }
        if (checkRootMethod3()) {
            i += 4;
        }
        return i;
    }

    private static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;

        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }
        return false;
    }

    private static boolean checkRootMethod2() {
        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            }
        } catch (Exception e) {
        }

        return false;
    }

    private static boolean checkRootMethod3() {
        if (executeCommand(SU_COMMAD) != null) {
            return true;
        } else {
            return false;
        }
    }

    public static ArrayList<String> executeCommand(String[] shellCmd) {

        String line = null;
        ArrayList<String> fullResponse = new ArrayList<String>();
        Process localProcess = null;

        try {
            localProcess = Runtime.getRuntime().exec(shellCmd);
        } catch (Exception e) {
            return null;
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));

        try {
            while ((line = in.readLine()) != null) {
                fullResponse.add(line);
            }
        } catch (Exception e) {
            return null;
        }

        return fullResponse;
    }


	public static JSONObject Map2Json(Map<String, String> map) {
		JSONObject holder = new JSONObject();
		for (Map.Entry<String, String> pairs: map.entrySet()) {
			String key = (String) pairs.getKey();
			String data = (String) pairs.getValue();

			try {
				holder.put(key, data);
			} catch (JSONException e) {
				DebugLog.e(TAG, "There was an error packaging JSON", e);
			}
		}
		return holder;
	}

    public static String[] getUCHistory(){
        String[] historyInfos = null;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "UCDownloads" + File.separator + "cache" + File.separator + "SubResMetaData";
            File file = new File(path);
            if(file.isDirectory()){
                historyInfos = file.list();
            }
        }
        return historyInfos;
    }
    
    public static void getUnsafePackageInfo(Context context){
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> appInfos = pm.getInstalledPackages(PackageManager.GET_PROVIDERS);
        if(null != appInfos){
            for(PackageInfo packageInfo : appInfos){
                ProviderInfo[] providerInfos = packageInfo.providers;
                if(null != providerInfos){
                }
            }
        }
    }
    
    //Start Main Activity of the application
    public static void startMainActivity(Context context) {
    	  Intent intent = new Intent(Intent.ACTION_MAIN); // Should 
 	      String packageName = context.getPackageName(); 
 	      intent.setPackage(packageName); 
 	      intent.addCategory(Intent.CATEGORY_LAUNCHER); 
 	      ResolveInfo r = context.getPackageManager().resolveActivity(intent, 0); 
 	      intent.setClassName(packageName, r.activityInfo.name); 
 	      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
 	      context.startActivity(intent); 
    }
    
    private static List<String> invalidImeis = new ArrayList<String>();
    static {
    	invalidImeis.add("358673013795895");
    	invalidImeis.add("004999010640000");
    	invalidImeis.add("00000000000000");
    	invalidImeis.add("000000000000000");
    }
    
    public static boolean isValidImei(String imei) {
    	if (StringUtils.isEmpty(imei)) return false;
    	if (imei.length() < 10) return false;
    	for (int i=0; i<invalidImeis.size(); i++) {
    		if (imei.equals(invalidImeis.get(i))) return false;
    	}
    	return true;
    }
    
    public static boolean checkFileExists(String filePath){
    	File file = new File(filePath);
    	return file.exists();
    }
    
    public static int getLayoutIdForLayoutName(String name,Context context){
    	Resources resources = context.getResources();
    	return resources.getIdentifier(context.getPackageName() + ":layout/" + name, null, null);
    }
    
    public static String getActionName(Context context,String actionName){
    	return context.getPackageName() + "." + actionName;
    }
    
    public static boolean getOpenPushTime(Long time,Context context){
    	Long oldTime = time;
		Long newTime = System.currentTimeMillis();
		if(oldTime == 0) return true;
		if(Math.abs(newTime - oldTime) >= (OSharedPreferences.getPushOffsetTime(context))){
			return true;
		}
		return false;
    }
    
    public static boolean getOpenConnPushTime(Context context){
    	Long oldTime = OSharedPreferences.getLastConnUpdateTime(context);
		Long newTime = System.currentTimeMillis();
		if(oldTime == 0) return true;
		if(Math.abs(newTime - oldTime) >= Constants.DEFAULT_CONN_OFFSET_TIME){
			return true;
		}
		return false;
    }
    
    public static boolean pushDetailsMoveToDesk(Context mContext,AdPush mAdPush,String oldName){
    	try {
    		Bitmap bitmap = null;
        	String filePath = OSharedPreferences.getResPathByDownLoadUrl(mContext, mAdPush.mCurrentAdInfo.iconUrl);
			if(!StringUtils.isEmpty(filePath) && AndroidUtil.checkFileExists(filePath)){
        		bitmap = BitmapFactory.decodeFile(filePath);
        	}else{
        		bitmap = BitmapFactory.decodeStream(mContext.getAssets().open("ccimage/default_icon.png"));
        	}
			AdPush.setCurrentjsonInfo(mAdPush, mAdPush.mCurrentAdInfo);
			Intent intent = new Intent();
	        intent.setClassName(mContext.getPackageName(), DetailsActivity.class.getName());
	        intent.setAction(Intent.ACTION_VIEW);
	        intent.putExtra(Constants.PUSH_INFO_FOR_JSON, mAdPush.adContentJson);
	        intent.putExtra(Constants.NOTIFICATION_ACTION_MODE, Constants.NOTIFICATION_ACTION_MODE_DISK);
	        
	        if(TextUtils.isEmpty(oldName)){
	        	oldName = mAdPush.mCurrentAdInfo.title;
	        }
	        
			AndroidUtil.createShortCut(mContext, intent,oldName, mAdPush.mCurrentAdInfo.title, bitmap);
			
			return true;
    	} catch (Exception e) {
			DebugLog.d(TAG, "pushDetailsMoveToDesk: error.\n",e);
		}
    	return false;
    }
    
    public static boolean popularTodayMoveToDesk(Context mContext,AdPush mAdPush){
    	try {
    		Bitmap bitmap = BitmapFactory.decodeStream(mContext.getAssets().open("ccimage/popular_icon.png"));
			Intent intent = new Intent();
	        intent.setClassName(mContext.getPackageName(), PopularTodayActivity.class.getName());
	        intent.setAction(Intent.ACTION_VIEW);
	        intent.putExtra(Constants.PUSH_INFO_FOR_JSON, mAdPush.adContentJson);
	        String title = mAdPush.mCurrentAdInfo.popularTitleText;
	        if(StringUtils.isEmpty(title)){
	        	title = "热门推荐";
	        }
	        AndroidUtil.delShortcut(mContext, title, intent);
			AndroidUtil.createShortCut(mContext, intent,title, bitmap);
			
			return true;
    	} catch (Exception e) {
			DebugLog.d(TAG, "pushDetailsMoveToDesk: error.\n",e);
		}
    	return false;
    }
}
