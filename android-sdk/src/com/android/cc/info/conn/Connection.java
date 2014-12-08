package com.android.cc.info.conn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Connection {
	/**
	 * get the input stream
	 * 
	 * @return
	 * @throws IOException
	 */
	public abstract InputStream getInputStream() throws IOException;

	/**
	 * get the input stream;
	 * 
	 * @return
	 * @throws IOException
	 */
	public abstract OutputStream getOutputStream() throws IOException;


	public abstract boolean isConnected();

	public abstract void close();

}
