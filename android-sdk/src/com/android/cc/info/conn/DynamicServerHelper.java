package com.android.cc.info.conn;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.android.cc.info.Config;
import com.android.cc.info.util.Constants;
import com.android.cc.info.util.DebugLog;


public class DynamicServerHelper {
	private static final String TAG = "DynamicServerHelper";
	private static final int TIME_OUT = 1000 * 5;
	private static final int RECEIVE_BUFFER_SIZE = 1024;
	
	public static List<String> mDynamicServerList = new ArrayList<String>();
	
	public static void resetServerAddresses() {
		JSONObject params = new JSONObject();
		try {
			params.put("SDK_VERSION", Config.SDK_VERSION);
		} catch (JSONException e1) {
		}
		
		JSONArray servers = null;;
		try {
				servers = getRemoteServers(params.toString(), 
						Config.mAccessServerList.get(0), Config.mAccessServerList.get(1), Config.mAccessServerIp, Config.mAccessServerPort);
		} catch (UnsupportedEncodingException e) {
		}
		if (null != servers) {
			int size = servers.length();
			try {
				JSONObject o = null;
				String tServer = null;
				for (int i = 0; i < size; i++) {
					o = servers.getJSONObject(i);
					tServer = o.getString("TCP");
					
					mDynamicServerList.add(tServer);
				}
			} catch (JSONException e) {
				DebugLog.e(TAG, "Unexptected: invalid UDP hosts JSON - " + servers,e);
			}
		} else {
			DebugLog.e(TAG, "Unexpected: all failed.");
		}
	}
	
	private static JSONArray getRemoteServers(String params, 
			String domain1, String domain2, String ip, int port) 
			throws UnsupportedEncodingException {
		DebugLog.v(TAG, "action:getRemoteServers - params:" + params
				+ ", domain1:" + domain1 + ", domain2:" + domain2 + ", ip:" + ip + ", port:" + port);
		int currentConnnectAddress = 1;
		boolean bothDomainEmpty = false;
		if (TextUtils.isEmpty(domain1)) {
			if (!TextUtils.isEmpty(domain2)) {
				domain1 = domain2;
				domain2 = null;
				currentConnnectAddress = 2;
			} else {
				bothDomainEmpty = true;
			}
		}
		
		InetAddress ipAddress = null;
		if (!bothDomainEmpty) {
			try {
				ipAddress = InetAddress.getByName(domain1);
			} catch (UnknownHostException e) {
				if (!TextUtils.isEmpty(domain2)) {
					DebugLog.e(TAG, "First domain failed to get dns");
					try {
						currentConnnectAddress = 2;
						ipAddress = InetAddress.getByName(domain2);
					} catch (UnknownHostException e1) {
						DebugLog.e(TAG, "Second domain failed to get dns");
					}
				}
			}
		}
		
		if (ipAddress == null) {
			if (!TextUtils.isEmpty(ip)) {
				currentConnnectAddress = 3;
				InetAddress[] tmp = null;
				try {
					tmp = InetAddress.getAllByName(ip);
				} catch (UnknownHostException e) {
					DebugLog.e(TAG, "Failed to get address from IP", e);
					return null;
				}
				if (null != tmp && tmp.length > 0) {
					ipAddress = tmp[0];
				} else {
					DebugLog.d(TAG, "Failed to get IP");
					return null;
				}
			} else {
				DebugLog.d(TAG, "NOTE: all 3 cannot be used to connect");
				return null;
			}
		}
		
		JSONArray servers = null;
		try {
			servers = send(params, ipAddress, port);
		} catch (IOException e) {
			DebugLog.d(TAG, "", e);
			if (currentConnnectAddress == 1) {
				servers = getRemoteServers(params, null, domain2, ip, port);
			} else if (currentConnnectAddress == 2) {
				servers = getRemoteServers(params, null, null, ip, port);
			}
		}
		
		return servers;
	}
	
	private static JSONArray send(String params, InetAddress ipAddress, int port) throws IOException {
		DatagramSocket clientSocket = new DatagramSocket();
		clientSocket.setSoTimeout(TIME_OUT);
		
		byte[] sendData = params.getBytes(Constants.DEFAULT_CHARSET);
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
		clientSocket.send(sendPacket);
		
		byte[] receiveData = new byte[RECEIVE_BUFFER_SIZE];
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);
		
		int receivedDataLength = receivePacket.getLength();
		byte[] receivedDataFinal = new byte[receivePacket.getLength()];
		System.arraycopy(receiveData, 0, receivedDataFinal, 0, receivedDataLength);
		String servers = new String(receivedDataFinal, Constants.DEFAULT_CHARSET);
		DebugLog.v(TAG, "Received KK DNS - len:" + + receivePacket.getLength() + ", string:" + servers);
		try {
			JSONArray array = new JSONArray(servers);
			if (array.length() == 0) {
				throw new IOException("Unexpected: servers is empty");
			}
			
			JSONObject first = array.getJSONObject(0);
			if (first.has("TCP") ) {
				return array;
			} else {
				throw new IOException("Unexpected: not tcp&http in first server");
			}
		} catch (JSONException e) {
			throw new IOException("Unexpected: Invalid JSON servers");
		}
	}
	
	public static void resetServerAddressesWithThread(){
		new Thread(){
			@Override
			public void run() {
				resetServerAddresses();
			}
			
		}.start();
	}
	
	public static String getBestServer(String ip,int port){
		String currentServer = ip+":"+port;
		String bestServer = Config.ip+":"+Config.port;
		if(mDynamicServerList.size()<=0){
			resetServerAddressesWithThread();
			return bestServer;
		}else{
			if(mDynamicServerList.contains(currentServer)){
				for(int i =0;i<mDynamicServerList.size();i++){
					if(mDynamicServerList.get(i).equals(currentServer)&&i!=mDynamicServerList.size()-1){
						bestServer = mDynamicServerList.get(i+1);
						break;
					}else if(mDynamicServerList.get(i).equals(currentServer)){
						bestServer = mDynamicServerList.get(0);
						break;
					}
				}
			}else{
				bestServer = mDynamicServerList.get(0);
			}
			return bestServer;
		}
	}
}

