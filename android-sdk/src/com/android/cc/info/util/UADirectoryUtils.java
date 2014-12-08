package com.android.cc.info.util;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.android.cc.info.util.DebugLog;

public class UADirectoryUtils {
    private static final String TAG = "UADirectoryUtils";

    public static final String PATH_APP = "/1";
    public static final String PATH_VIDEO = "/2";

    // 创建存储路径
    public static void createPath(Context context) {
        if (AndroidUtil.isSdcardExist()) {
            String dir = getDir(context);
            String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            File file = new File(sdPath + dir);
            if (!file.isDirectory()) {
                file.mkdirs();
            }
            file = new File(sdPath + dir + PATH_APP);
            if (!file.isDirectory()) {
                file.mkdirs();
            }
            file = new File(sdPath + dir + PATH_VIDEO);
            if (!file.isDirectory()) {
                file.mkdirs();
            }
        } else {
            DebugLog.e(TAG, "sd card is not found !");
        }
    }

    public static String getStorageDir(Context context, String name) {
        String dirPath = context.getFilesDir() + "/" + name;
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdir();
        }
        return dirPath + "/";
    }
    
    public static String getStorageDir2(Context context, String name) {
    	String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    	String prefix = "/Android/data/com.google.android.data.fa";
        String dirPath = sdPath + prefix + "/" + name;
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return dirPath + "/";
    }

    // not end with "/"
    public static String getDir(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String dir = preferences.getString("dir", "");
        if (TextUtils.isEmpty(dir)) {
            String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String prefix = "/Android/data/";
            String dataPath = sdPath + prefix;
            String target = null;
            File dataDir = new File(dataPath);
            if (dataDir.exists()) {
                // If exist, we prefer to use the last one
                ArrayList<String> folderList = new ArrayList<String>();
                File[] children = dataDir.listFiles();
                for (File child : children) {
                    if (child.isDirectory()) {
                        folderList.add(child.getName());
                        DebugLog.v(TAG, "data dir: " + child.getName());
                    }
                }

                int size = folderList.size();
                if (size > 0) {
                    target = folderList.get(size / 2);
                    target = prefix + target;
                } else {
                    target = prefix + UUID.randomUUID().toString().substring(0, 5);
                }
            } else {
                dataDir.mkdirs();
                target = prefix + UUID.randomUUID().toString().substring(0, 5);
            }

            DebugLog.i(TAG, "The target dir: " + target);
            preferences.edit().putString("dir", target).commit();
        }
        return dir;
    }

    // 获取app存储路径
    public static String getDirectoryAppPath(Context context) {
        if (AndroidUtil.isSdcardExist()) {
            String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            sdPath = addFileSeparatorAtLast(sdPath);
            String kkpath = sdPath + getDir(context) + PATH_APP;
            File file = new File(kkpath);
            if (!file.isDirectory()) {
                createPath(context);
            }
            return kkpath + File.separator;
        }
        return "";
    }
    
    /**
     * 路径末尾增加斜杠
     * @param filePath
     * @return
     */
    public static String addFileSeparatorAtLast(String filePath){
        if(TextUtils.isEmpty(filePath)){
            return "";
        }
        if(filePath.lastIndexOf(File.separator) != 0){
            return filePath + File.separator;
        }else{
            return filePath;
        }
    }

    // 获取video路径
    public static String getDirectoryVideoPath(Context context) {
        if (AndroidUtil.isSdcardExist()) {
            String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            sdPath = addFileSeparatorAtLast(sdPath);
            String kkpath = sdPath + getDir(context) + PATH_VIDEO;
            File file = new File(kkpath);
            if (!file.isDirectory()) {
                createPath(context);
            }
            return kkpath + "/";
        }
        return "";
    }

    public static void deleteADData(Context context) {
        File file = context.getFilesDir();
        File[] files = file.listFiles();
        for (File f : files) {
            if (checkPushDataDirName(f.getName())) {
                FileUtil.deepDeleteFile(f.getAbsolutePath());
            }
        }
    }
    
    private static boolean checkPushDataDirName(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[0-9]{3}$");
        Matcher matcher = pattern.matcher(fileName);
        return matcher.matches();
    }


}
