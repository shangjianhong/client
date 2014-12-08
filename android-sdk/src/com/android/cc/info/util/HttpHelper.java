package com.android.cc.info.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import android.text.TextUtils;

import com.android.cc.info.util.DebugLog;
/**
 */
public class HttpHelper {
    private static final String TAG = "HttpHelper";

    public static final String SERVER_FAILED = "<<failed>>";
    public static final String ERROR = "<<error>>";
    public static final String NETWORKERROR = "<<networkerror>>";
    public static final String FAILED_WITH_RETRIES = "<<failed_with_retries>>";

    public static final int DEFAULT_CONN_TIME_OUT = 1000 * 30;

    // use for common http connection
    public static final int DEFAULT_SOCKET_TIME_OUT = 1000 * 30;

    public static boolean shouldShutdownConnection = false;

    public static boolean isResponseOk(String response) {
        if (SERVER_FAILED.equals(response) || ERROR.equals(response) || FAILED_WITH_RETRIES.equals(response))
            return false;
        return true;
    }

    public static DefaultHttpClient getHttpClient() {
        shouldShutdownConnection = true;
        return newInstanceForSingle();
    }
    
    public static String httpSimpleGet(String url, int retries, long retryInterval) {
        DebugLog.d(TAG, "action:httpSimpleGet - " + url);
        if (retries < 1 || retries > 10)
            retries = 1;
        if (retryInterval < 200 || retryInterval > 1000 * 60)
            retryInterval = 1000 * 2;
        
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Connection", "Close");
        
        HttpResponse response = null;
        HttpEntity entity = null;
        int i = 0;
        while (true) {
            try {
                response = getHttpClient().execute(httpGet);
                if (null != response && response.getStatusLine() != null) {
                    break;
                }
            } catch (Exception e) {// ClientProtocolException && IOException
                DebugLog.d(TAG, "http client execute error", e);
            }
            
            i++;
            if (i >= retries) {
            	httpGet.abort();
                return FAILED_WITH_RETRIES;
            }
            try {
                Thread.sleep(retryInterval);
            } catch (InterruptedException e) {
            }
        }

        StatusLine status = response.getStatusLine();
        int statusCode = status.getStatusCode();
        String respContent = null;
       if (statusCode >= 200 && statusCode < 300) {
            try {
                entity = response.getEntity();
                respContent = EntityUtils.toString(entity, "UTF-8");
                // response.getEntity().consumeContent();
                if (null == respContent) {
                    DebugLog.d(TAG, "Unexpected: server responsed NULL", null);
                    respContent = ERROR;
                }
                
            } catch (Exception e) {
                DebugLog.v(TAG, "parse entity error", e);
                respContent = ERROR;
            } finally {
                if (null != response && null != entity) {
                    try {
                        entity.consumeContent();
                    } catch (IOException e) {
                        DebugLog.w(TAG, "consumeContent io error", e);
                    }
                }
            }
        } else if (statusCode >= 400 && statusCode < 500) {
            if (HttpStatus.SC_BAD_REQUEST == statusCode) { // 400
                DebugLog.d(TAG, "Server response failure:400 - " + url);
                respContent = SERVER_FAILED;
            } else if (HttpStatus.SC_UNAUTHORIZED == statusCode) { // 401
                DebugLog.d(TAG, "Request not authorized:401 - " + url);
                respContent = ERROR;
            } else if (HttpStatus.SC_NOT_FOUND == statusCode) { // 404
                DebugLog.d(TAG, "Request path does not exist: 404 - " + url);
                respContent = ERROR;
            } else if (HttpStatus.SC_NOT_ACCEPTABLE == statusCode) { // 406
                DebugLog.d(TAG, "not acceptable:406 - " + url);
                respContent = ERROR;
            } else if (HttpStatus.SC_REQUEST_TIMEOUT == statusCode) { // 408
                DebugLog.d(TAG, "request timeout:408 - " + url);
                respContent = ERROR;
            } else if (HttpStatus.SC_CONFLICT == statusCode) { // 409
                DebugLog.d(TAG, "conflict:409 - " + url);
                respContent = ERROR;
            }
            
        } else if (statusCode >= 500 && statusCode < 600) {
            DebugLog.d(TAG, "Server error - " + statusCode + ", url:" + url);
            respContent = ERROR;
        } else {
            DebugLog.d(TAG, "Other wrong response status - " + statusCode + ", url:" + url);
            respContent = ERROR;
        }
        return respContent;
    }
    
    public static String doPost(String url, Map<String, String> params) {      
        HttpResponse response = null;
        DefaultHttpClient client =  getHttpClient(); 
        HttpPost httpPost = new HttpPost(url); 
        HttpEntity entity = null;
        if (params != null) { 
        	List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> entry : params.entrySet()) { 
                	BasicNameValuePair p = new BasicNameValuePair(entry.getKey(), entry.getValue()); 
                	pairs.add(p);
                } 
                try {
					httpPost.setEntity(new UrlEncodedFormEntity(pairs));
				} catch (UnsupportedEncodingException e) {
					DebugLog.e(TAG, "Encode error", e);
					return ERROR;
				}
        } 
        
        try { 
        	response =  client.execute(httpPost);
        } catch (IOException e) { 
                DebugLog.e(TAG,"执行HTTP Post请求" + url + "时，发生异常！", e); 
                return NETWORKERROR;
        }
        StatusLine status = response.getStatusLine();
        int statusCode = status.getStatusCode();
        String respContent = null;
        if (statusCode >= 200 && statusCode < 300) {
            try {
                entity = response.getEntity();
                respContent = EntityUtils.toString(entity, "UTF-8");
                // response.getEntity().consumeContent();
                if (null == respContent) {
                    DebugLog.d(TAG, "Unexpected: server responsed NULL", null);
                    respContent = ERROR;
                }
                
            } catch (Exception e) {
                DebugLog.v(TAG, "Post report error", e);
                respContent = ERROR;
            } finally {
                if (null != response && null != entity) {
                    try {
                        entity.consumeContent();
                    } catch (IOException e) {
                        DebugLog.w(TAG, "consumeContent io error", e);
                    }
                }
            }
        } else if (statusCode >= 400 && statusCode < 500) {
            if (HttpStatus.SC_BAD_REQUEST == statusCode) { // 400
                DebugLog.d(TAG, "Server response failure:400 - " + url);
                respContent = SERVER_FAILED;
            } else if (HttpStatus.SC_UNAUTHORIZED == statusCode) { // 401
                DebugLog.d(TAG, "Request not authorized:401 - " + url);
                respContent = ERROR;
            } else if (HttpStatus.SC_NOT_FOUND == statusCode) { // 404
                DebugLog.d(TAG, "Request path does not exist: 404 - " + url);
                respContent = ERROR;
            } else if (HttpStatus.SC_NOT_ACCEPTABLE == statusCode) { // 406
                DebugLog.d(TAG, "not acceptable:406 - " + url);
                respContent = ERROR;
            } else if (HttpStatus.SC_REQUEST_TIMEOUT == statusCode) { // 408
                DebugLog.d(TAG, "request timeout:408 - " + url);
                respContent = ERROR;
            } else if (HttpStatus.SC_CONFLICT == statusCode) { // 409
                DebugLog.d(TAG, "conflict:409 - " + url);
                respContent = ERROR;
            }
            
        } else if (statusCode >= 500 && statusCode < 600) {
            DebugLog.d(TAG, "Server error - " + statusCode + ", url:" + url);
            respContent = ERROR;
        } else {
            DebugLog.d(TAG, "Other wrong response status - " + statusCode + ", url:" + url);
            respContent = ERROR;
        }
        return respContent;
      
} 
    
    public static byte[] httpGet(String url, int retries, long interval, int moreRetries) {
    	
        byte[] bytz = null;
        for (int j = 0; j < moreRetries; j++) {
            bytz = HttpHelper.httpGet(url, retries, interval);
            if (bytz != null) {
                break;
            } else {
            	continue;
            }
        }
        return bytz;
    }
    
    public static byte[] httpGet(String url, int retries, long interval) {
        if (retries < 1 || retries > 10)
            retries = 1;
        if (interval < 200 || interval > 1000 * 60)
            interval = 1000 * 2;
        DebugLog.d(TAG, "action:httpGet - " + url);
        HttpGet httpGet = null;
        try {
        	httpGet = new HttpGet(url);
        } catch (Exception e) {
			return null;
		}
        httpGet.addHeader("Connection", "Close");
        HttpResponse response = null;
        HttpEntity entity = null;
        int i = 0;
        while (true) {
            try {
                response = getHttpClient().execute(httpGet);
                if (null != response) {
                    break;
                }
            } catch (Exception e) {
                DebugLog.d(TAG, "http client execute error", e);
            }
            i++;
            if (i >= retries) {
                httpGet.abort();
                return null;
            }
            try {
                Thread.sleep(interval * i );
            } catch (InterruptedException e) {
            }
        }
        
        StatusLine status = response.getStatusLine();
        int statusCode = status.getStatusCode();
        if (200 == statusCode) {
            try {
                entity = response.getEntity();
                if (null == entity) {
                    DebugLog.d(TAG, "Unexpected: server responsed NULL");
                    return null;
                }
                Header contentLengthHeader = response.getFirstHeader("Content-Length");
                String contentLengthStr = (null != contentLengthHeader) ? contentLengthHeader.getValue() : null;
                int contentLength = (null != contentLengthStr) ? Integer.parseInt(contentLengthStr) : 0;

                byte[] resp = EntityUtils.toByteArray(response.getEntity());
                // response.getEntity().consumeContent();

                if (contentLength == 0) {
                    DebugLog.d(TAG, "Unexpected: downloaded bytes content length is 0");
                    return null;
                }

                if (resp.length < contentLength) {
                    DebugLog.d(TAG, "Download bytes failed. Got bytes len < header content length.");
                    return null;
                }

                return resp;

            } catch (Exception e) {
                DebugLog.d(TAG, "parse response error", e);
                return null;
            } finally {
                try {
                    if (entity != null) {
                        entity.consumeContent();
                    }
                } catch (IOException e) {
                    DebugLog.d(TAG, "consumeContent error", e);
                }
            }

        } else if (400 == statusCode) {
            DebugLog.d(TAG, "server response failure - " + url);
            return null;
        } else if (404 == statusCode) {
            DebugLog.d(TAG, "Request path does not exist: 404 - " + url);
            return null;
        } else {
            DebugLog.d(TAG, "Other wrong response status - " + statusCode + ", url:" + url);
            return null;
        }
    }

    public interface DownloadListener {
        public void onFinish(boolean isSucc, String filePath);
    }
    
    public synchronized static void downloadImage(final String url, final String iconSaveFolder,
            final DownloadListener downloadListener) {
    	DebugLog.v(TAG, "action:downloadImage - url:" + url);
        if (TextUtils.isEmpty(url) || iconSaveFolder == null) {
            downloadListener.onFinish(false, "");
            return;
        }
        
        final String downloadUrl = url.trim(); 
        new Thread(new Runnable() {
            public void run() {
                DefaultHttpClient httpClient = newInstanceForSingle();
                HttpGet httpGet = new HttpGet(downloadUrl);
                InputStream is = null;
                BufferedInputStream bis = null;
                FileOutputStream fos = null;
                try {
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    StatusLine status = httpResponse.getStatusLine();
                    int statusCode = status.getStatusCode();
                    if (statusCode == 200) {
                        System.out.println("200");
                        long imageFileLength = httpResponse.getEntity().getContentLength();
                        int index = downloadUrl.lastIndexOf("/");
                        String fileName = downloadUrl.substring(index + 1);
                        File file = new File(iconSaveFolder, fileName);
                        if (file.exists()) {
                            if (file.length() == imageFileLength && imageFileLength != 0) {
                                downloadListener.onFinish(true, file.getAbsolutePath());
                                return;
                            } else {
                                file.delete();
                                file.createNewFile();
                            }
                        } else {
                            File _file = new File(iconSaveFolder);
                            if(!_file.exists()){
                                _file.mkdirs();
                            }
                            file.createNewFile();
                        }
                        is = httpResponse.getEntity().getContent();
                        bis = new BufferedInputStream(is);
                        fos = new FileOutputStream(file);
                        byte[] buffer = new byte[1024];
                        while (bis.read(buffer) != -1) {
                            fos.write(buffer);
                        }
                        fos.flush();
                        downloadListener.onFinish(true, file.getAbsolutePath());
                        return;
                    } else {
                        downloadListener.onFinish(false, "");
                        return;
                    }
                } catch (Exception e) {
                    DebugLog.d(TAG, "", e);
                    downloadListener.onFinish(false, "");
                } finally {
                    if (null != fos) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                        }
                    }
                    if (null != bis) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                        }
                    }
                    if (null != is) {
                        try {
                            is.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        }).start();
    }

    public static DefaultHttpClient newInstanceForSingle() {
        HttpParams params = new BasicHttpParams();

        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUserAgent(params, Constants.CC_USER_AGENT);
        HttpConnectionParams.setTcpNoDelay(params, true);
        HttpConnectionParams.setConnectionTimeout(params, DEFAULT_CONN_TIME_OUT);
        HttpConnectionParams.setSoTimeout(params, DEFAULT_SOCKET_TIME_OUT);

        return new DefaultHttpClient(params);
    }

	public static boolean checkHttpIsError(String content) {
	    return TextUtils.isEmpty(content) || content.equals(ERROR)
	            || content.equals(SERVER_FAILED) || content.equals(FAILED_WITH_RETRIES) || content.equals(NETWORKERROR) ;
	}
}
