/**
 * 
 */
package com.android.cc.info.conn;



/**
 * @author Martin
 * 
 */
public class ConnectionFactory {

	private static ConnectionFactory instance;
	
	private ConnectionFactory() {

	}

	public synchronized static ConnectionFactory getInstance() {
		if (instance == null) {
			instance = new ConnectionFactory();
		}
		return instance;
	}

	/**
	 * get the connection
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Connection getConnection(String ip,int port) throws Exception {
		return new TcpConnection(ip,port);
	}
}
