package com.android.cc.info.protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;

import com.android.cc.info.Config;
import com.android.cc.info.conn.Connection;
import com.android.cc.info.conn.ConnectionFactory;
import com.android.cc.info.conn.DynamicServerHelper;
import com.android.cc.info.preferences.OSharedPreferences;
import com.android.cc.info.protocol.req.BatchUserStepRequestCommand;
import com.android.cc.info.protocol.req.RequestCommand;
import com.android.cc.info.protocol.req.UserStepRequestCommand;
import com.android.cc.info.protocol.resp.ResponseProcesser;
import com.android.cc.info.util.AndroidUtil;
import com.android.cc.info.util.DebugLog;
import com.android.cc.info.util.ProtocolUtil;

/**
 * the thread used to send the request
 * 
 * @author martin
 * 
 */
public class RequestThread implements Runnable {

	private static final String TAG = "RequestThread";
	private static BlockingQueue<RequestCommand> requestQueue = new LinkedBlockingQueue<RequestCommand>();
	private boolean isStop = false;
	private static boolean startThread = false;
	private Context mContext;
	public RequestThread(Context context)
	{
		mContext = context;
	}
	
	@Override
	public void run() {
		DebugLog.d(TAG, "Request Thread start");
		startThread = true;
		String ip = Config.ip;
		int port = Config.port;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while (!isStop) {
			Connection conn = null;
			RequestCommand request = null;
			
			try {
				request = requestQueue.take();
				
				DebugLog.d(TAG, "request--command:"+request.mCommand+",ip:"+ip+",port:"+port);
				conn = ConnectionFactory.getInstance().getConnection(ip,port);

				if(DynamicServerHelper.mDynamicServerList.size()<=0){
					DynamicServerHelper.resetServerAddressesWithThread();
				}
				
				OutputStream os = conn.getOutputStream();

				request.buildBody();
				request.buildDataBeforeAdd();
				byte[] outData = request.mData;

				if (outData != null && outData.length >= 11) {
					os.write(outData);
					InputStream is = conn.getInputStream();
					byte[] data = new byte[10240];
					int readCount = 0;
					readCount = is.read(data);
					while (readCount > 0) {
						baos.write(data, 0, readCount);
						if (baos.size() >= 4) {
							byte[] bLen = new byte[4];
							System.arraycopy(baos.toByteArray(), 0, bLen, 0, bLen.length);
							int dataLength = ProtocolUtil.byteArrayToInt(bLen);
							if(dataLength<=baos.size()){
								processData(baos);
								break;
							}
						}
						readCount = is.read(data);
					}
					baos.reset();
				}
			} catch (UnknownHostException e) {
				DebugLog.d(TAG, "Unkown host error", e);
				if(request != null && request.mCommand == Command.CMD_USER_STEP)
				{
					String requestInfo = ((UserStepRequestCommand)request).getStringData();
					OSharedPreferences.setRequestFailInfo(mContext, requestInfo);
				}else if(request != null && request.mCommand == Command.CMD_BATCH_USER_STEP){
					String requestInfo = ((BatchUserStepRequestCommand)request).getBatchStepData();
					OSharedPreferences.setRequestFailInfo(mContext, requestInfo);
				}
				if(AndroidUtil.isConnected(mContext)){
					String server = DynamicServerHelper.getBestServer(ip,port);
					int tempPort = Config.port;
					String tempIp = Config.ip;
					int index = server.indexOf(":");
					if (index > 0) {
						String sPort = server.substring(index + 1);
						try {
							tempPort = Integer.parseInt(sPort);
						} catch (Exception e1) {
						}
						tempIp = server.substring(0, index);
					}
					//ip = tempIp;
					port = tempPort;
				}
			} catch (IOException e) {
				DebugLog.d(TAG, "Connect error", e);
				if(request != null && request.mCommand == Command.CMD_USER_STEP)
				{
					String requestInfo = ((UserStepRequestCommand)request).getStringData();
					OSharedPreferences.setRequestFailInfo(mContext, requestInfo);
				}else if(request != null && request.mCommand == Command.CMD_BATCH_USER_STEP){
					String requestInfo = ((BatchUserStepRequestCommand)request).getBatchStepData();
					OSharedPreferences.setRequestFailInfo(mContext, requestInfo);
				}
				if(AndroidUtil.isConnected(mContext)){
					if (e instanceof SocketTimeoutException
							||(e instanceof SocketException && e.getMessage().contains("Host is unresolved"))
							||(e instanceof SocketException && e.getMessage().contains("No route to host"))
							||(e instanceof ConnectException && e.getMessage().contains("Connection refused"))) {
						String server = DynamicServerHelper.getBestServer(ip,port);
						int tempPort = Config.port;
						String tempIp = Config.ip;
						int index = server.indexOf(":");
						if (index > 0) {
							String sPort = server.substring(index + 1);
							try {
								tempPort = Integer.parseInt(sPort);
							} catch (Exception e1) {
							}
							tempIp = server.substring(0, index);
						}
						//ip = tempIp;
						port = tempPort;
					} 
				}
			} catch(Exception e){
				DebugLog.d(TAG, "Connect other error", e);
			} finally {
				if(conn!=null){
					conn.close();
				}
			}
		}
	}

	public void stop() {
		isStop = true;
	}

	public static void addRequest(RequestCommand requestCommand) {
		requestQueue.add(requestCommand);
	}

	public static void processData(ByteArrayOutputStream baos) {
		byte[] data = baos.toByteArray();
		ResponseProcesser.process(data);
	}
	
	
}
