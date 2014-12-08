/**
 * 
 */
package com.android.cc.info.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import com.android.cc.info.util.DebugLog;

/**
 * @author martin
 * 
 */

public class TcpConnection extends Connection {
	
	protected static int maxConnectionTime = 30*1000;
	private final static String TAG = "TcpConnection";
	private Socket conn;

	public TcpConnection(String ip,int port) throws Exception{
		try{
			SocketAddress address = new InetSocketAddress(ip, port);
			conn = new Socket();
			conn.setTcpNoDelay(true);
			conn.connect(address, maxConnectionTime);
			conn.setSoTimeout(30 * 1000);
		} catch (UnknownHostException e) {
			DebugLog.d(TAG, "UnknownHostException",e);
			throw e;
		} catch (IOException e) {
			DebugLog.d(TAG, "IOException",e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cn.kktalk.imclientplugin.core.connection.Connection#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		return conn.getInputStream();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cn.kktalk.imclientplugin.core.connection.Connection#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() throws IOException {
		return conn.getOutputStream();
	}


	@Override
	public boolean isConnected() {
		return conn.isConnected() && !conn.isClosed() && !conn.isInputShutdown() && !conn.isOutputShutdown();
	}

	@Override
	public void close() {
		try {
			conn.close();
		} catch (IOException e) {

		}
	}

}
