package com.android.cc.info.util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import android.content.Context;
import android.text.TextUtils;

import com.android.cc.info.util.DebugLog;



public class FileUtil {
	private static final String TAG = "FileUtil";

    public static void deepDeleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                	deepDeleteFile(f.getAbsolutePath());
                    f.delete();
                }
            }
            file.delete();
        }
    }
    
	public static boolean createHtmlFile(String filePath, String content, Context context) {
		if (null == context) throw new IllegalArgumentException("NULL context");
		DebugLog.v(TAG, "action:createHtmlFile - filePath:" + filePath + ", content:" + content);
		if (!AndroidUtil.isSdcardExist()) {
			DebugLog.d(TAG, "SDCard is not valid. Give up.");
			return false;
		}
		
	    if (!TextUtils.isEmpty(filePath) && !TextUtils.isEmpty(content)) {
	    	try {
		        File file = new File(filePath);
		        if (!file.exists()) {
		            file.createNewFile();
		        }
		        FileOutputStream fos = null;
		        try {
		            fos = new FileOutputStream(file);
		            fos.write(content.getBytes("UTF-8"));
		            fos.flush();
		        } finally {
		            if (fos != null) {
		                fos.close();
		            }
		        }
		        
		        return true;
	    	} catch (IOException e) {
	    		DebugLog.d(TAG, "", e);
	    	}
	    }
	    return false;
	}

	public static boolean createImgFile(String filePath, byte[] buffer, Context context) throws IOException {
	    UADirectoryUtils.createPath(context);
	    if (!TextUtils.isEmpty(filePath) && buffer.length > 0 && AndroidUtil.isSdcardExist()) {
	        File file = new File(filePath);
	        if (!file.exists()) {
	            file.createNewFile();
	        }
	        FileOutputStream fos = null;
	        try {
	            fos = new FileOutputStream(file);
	            fos.write(buffer);
	            fos.flush();
	        } finally {
	            if (fos != null) {
	                fos.close();
	            }
	        }
	        return true;
	    }
	    return false;
	}
}
