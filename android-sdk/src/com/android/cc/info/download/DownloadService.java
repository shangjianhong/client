package com.android.cc.info.download;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.android.cc.info.data.AdInfo;
import com.android.cc.info.download.DownloadControl.DownloadListener;
import com.android.cc.info.preferences.OSharedPreferences;
import com.android.cc.info.ui.Notifier;
import com.android.cc.info.util.Constants;
import com.android.cc.info.util.DebugLog;
import com.android.cc.info.util.UserStepReportUtil;

import android.widget.Toast;


/**
 * 必须确保保存路径有足够的剩余空间且可读写
 */
public class DownloadService extends IntentService {

    private static final String TAG = "DownloadService";
    
    private static Bundle mDownloadInfos;
    public static ConcurrentLinkedQueue<AdInfo> mDownladTasks = new ConcurrentLinkedQueue<AdInfo>();
    
    
    private NotificationManager mNotificationManager;
    private AdInfo adEntity;
    private ToastHandler mToastHandler;
    
    public DownloadService() {
        super("DownloadService");
    }
    
    @Override
    public void onCreate() {
        DebugLog.d(TAG, "onCreate()");
        super.onCreate();
        mToastHandler = new ToastHandler(getApplicationContext());
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (mDownloadInfos == null) {
            mDownloadInfos = new Bundle();
        }
    }
    
    private class ToastHandler extends Handler{
        private Context context;
        public ToastHandler(Context context){
            super(context.getMainLooper());
            this.context = context;
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(context, "SDCard不可用", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
    	DebugLog.v(TAG, "------------------------------");
    	DebugLog.v(TAG, "download service is onDestroy!");
    	DebugLog.v(TAG, "------------------------------");
        super.onDestroy();
    }
    
    @Override
    protected void onHandleIntent(Intent intent) {
    	DebugLog.d(TAG, "action:onHandleIntent");
        adEntity = (AdInfo) intent.getSerializableExtra(Constants.AD_INFO);
        if (null == adEntity) {
        	DebugLog.w(TAG, "NULL ad entity");
        	return;
        }
        
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
        	DebugLog.w(TAG, "SDCard没有装载好");
            mToastHandler.sendEmptyMessage(0);
            return;
        }
        
        if (adEntity._isDownloadFinisehd) {
        	DebugLog.d(TAG, "The AD download is already finished.");
        	return;
        }

        if(!mDownladTasks.contains(adEntity)&&checkTask(adEntity)){
        	mDownladTasks.offer(adEntity);
        }
        
        final int notifiId = adEntity.notifiId;
        
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        
        new DownloadControl(this, adEntity, mDownloadInfos, new DownloadListener() {
        	public void onDownloading(long downloadLenth, long totalLength) {
                int percent = (int) (((float) downloadLenth / (float) totalLength) * 100);
                DebugLog.d(TAG, "pecent:" + percent + ", downloaded:" + downloadLenth + ", total:" + totalLength);
                downloadingNotification(adEntity, notifiId, downloadLenth, totalLength);
            }
            
            public void onDownloadSucceed(String fileSaveTotalPath, boolean existed) {
                adEntity._isDownloadFinisehd = true;
                mDownladTasks.remove(adEntity);
                adEntity.savePath = fileSaveTotalPath;
                mNotificationManager.cancel(notifiId);
                downloadSucceed(adEntity);
                OSharedPreferences.setNeedMonitoringForPackageName(DownloadService.this, adEntity);
                UserStepReportUtil.reportStep(DownloadService.this, Integer.parseInt(adEntity.adId), UserStepReportUtil.AD_PUSH_DOWNLOAD_SUCCESS);
                //下载完成清除数据队列信息
                ServiceInterface.cleanDownloadInfo(DownloadService.this, adEntity.adContentJson);
            }
            
            public void onDownloadFailed(int failType) {
            	mNotificationManager.cancel(notifiId);
                DebugLog.d(TAG, "download failed !! the clean notifiId -" + notifiId + ",failTyle - " + failType + ", ad_id - " + adEntity.adId);
                if (DownloadControl.isRealFailed(failType)) {
                	adEntity._isEverDownloadFailed = true;
                	UserStepReportUtil.reportStep(DownloadService.this, Integer.parseInt(adEntity.adId), UserStepReportUtil.AD_PUSH_DOWNLOAD_FAIL);
                }
            	adEntity._isDownloadInterrupted = true;
                downFailNotification(notifiId, adEntity, failType);
            }
        }, 3000);

    }
    
    private boolean checkTask(AdInfo adInfo){
    	try{
	        int size = mDownladTasks.size();
	        DebugLog.d(TAG,"Execute old download task - size:" + size);
	    	if(size == 0){
	    		return true;
	    	}
	    	boolean checkFalg = true;
	        AdInfo entity = null;
	    	ArrayList<AdInfo> taskList = new ArrayList<AdInfo>();
	        while ((entity = mDownladTasks.poll()) != null) {
	        	taskList.add(entity);
	        	if(entity.adContentJson.equals(adInfo.adContentJson)){
	        		checkFalg = false;
	        		break;
	        	}
	        }
	        
	        for (AdInfo task : taskList) {
	        	mDownladTasks.offer(task);
	        }
	        return checkFalg;
		}catch(Exception e){
			DebugLog.e(TAG, "startDownloadTasks:\n" + e.getMessage());
			return true;
		}
    }

    private Notification notification;
    
    private void downloadingNotification(AdInfo entity, int notifiId, long downloadLenth, long totalLength) {
        
        if(null == notification){
            notification = new Notification();
            notification.icon = android.R.drawable.stat_sys_download;
            notification.when = System.currentTimeMillis();
            notification.flags = Notification.FLAG_ONGOING_EVENT|Notification.FLAG_AUTO_CANCEL;
            notification.defaults = Notification.DEFAULT_LIGHTS;

            Intent intent = new Intent();
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), notifiId, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            
            notification.contentIntent = pendingIntent;
        }
        
        String title = entity.title;
        String content = "下载中... ";
        
        int percent = (int) (((float) downloadLenth / (float) totalLength) * 100f);
        
        if (totalLength > 0) {
        	content +=  percent + "%";
        }
        
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), notifiId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setLatestEventInfo(this, title, content, pendingIntent);
        mNotificationManager.notify(notifiId, notification);
    }

    private void downloadSucceed(AdInfo entity) {
        String filePath = entity.savePath;
        if (!TextUtils.isEmpty(filePath)) {
            Notifier notifier = new Notifier(this);
            notifier.downloadEndNotification(entity,false);
        } else {
        	DebugLog.d(TAG, "No end notification. is filePath empty ? - " + filePath);
        }
    }
    
    private void downFailNotification(int notifiId, AdInfo entity, int failType) {
    	if (DownloadControl.FAIL_TYPE_NOALERT_AUTO_CONTINUE == failType) return;
    	
        String content = null;
        int flags = Notification.DEFAULT_LIGHTS;
        if (DownloadControl.FAIL_TYPE_ALERT_CLICK_CONTINUE_NORMAL == failType) {
            content = "下载失败。请稍后点击重新下载！";
        } else if (DownloadControl.FAIL_TYPE_ALERT_CLICK_CONTINUE_404 == failType) {
        	content = "下载资源失效。请稍后点击重新下载！";
        } else if (DownloadControl.FAIL_TYPE_ALERT_AUTO_CONTINUE == failType) {
        	content = "当前网络不可用。稍后会继续下载！";
        	flags = Notification.FLAG_ONGOING_EVENT;
        } else {
        	return;
        }
        
        String title = adEntity.title;
        Intent intent = new Intent();
        if (DownloadControl.isRealFailed(failType)) {
        	intent.setClass(getApplicationContext(), DownloadService.class);
        	entity._downloadRetryTimes = AdInfo.DOWNLOAD_RETRY_TIMES_NOT_SET;
            intent.putExtra(Constants.AD_INFO, entity);
            //下载失败将清除队列重的数据，等待用户再次操作时再保存到队列中
            ServiceInterface.cleanDownloadInfo(DownloadService.this, entity.adContentJson);
        }
        
        Notification notification = new Notification();
        notification.icon = android.R.drawable.stat_sys_download_done;
        notification.when = System.currentTimeMillis();
        notification.flags = flags;
        
        PendingIntent pendingIntent = PendingIntent.getService(this, notifiId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        
        notification.setLatestEventInfo(getApplicationContext(), title, content, pendingIntent);
        mNotificationManager.notify(notifiId, notification);
    }
    
    public static boolean hasDownladTask() {
    	return (mDownladTasks.size() > 0);
    }
    
	public static void startDownloadTasks(Context context) {
		try{
	        int size = mDownladTasks.size();
	        DebugLog.d(TAG,"Execute old download task - size:" + size);
	    	
	        AdInfo entity = null;
	    	ArrayList<AdInfo> taskList = new ArrayList<AdInfo>();
	        while ((entity = mDownladTasks.poll()) != null) {
	            if (entity._isDownloadInterrupted) {
	            	DebugLog.d(TAG, "Starting to download - adId:" + entity.adId);
	            	ServiceInterface.executeDownload(context, entity);
	            } else {
	            	DebugLog.v(TAG, "Downloading is still there.");
	            	taskList.add(entity);
	            }
	        }
	        
	        for (AdInfo task : taskList) {
	        	mDownladTasks.offer(task);
	        }
		}catch(Exception e){
			DebugLog.e(TAG, "startDownloadTasks:\n" + e.getMessage());
		}
    }
}
