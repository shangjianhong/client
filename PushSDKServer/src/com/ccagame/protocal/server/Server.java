package com.ccagame.protocal.server;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ccagame.protocal.ProtocalUtil;
import com.ccagame.protocal.ProtocalXmlParser;
import com.ccagame.protocal.model.ServerInfo;

public class Server {
	private static final Logger logger = LoggerFactory.getLogger(Server.class);
	
//	public static ExecutorService exec = Executors.newCachedThreadPool();
	public static ExecutorService exec = Executors.newFixedThreadPool(2000);

	public Server() {

	}

	public void start() throws Exception {

		List<ServerInfo> serverList = ProtocalUtil.getServerInfo(true);
		if (serverList.size() > 0) {
			for (int i = 0; i < serverList.size(); i++) {
				final ServerInfo server = serverList.get(i);

				String ip = server.getAttributes().get(ProtocalXmlParser.XML_SERVER_ATTR_IP);
				final InetSocketAddress address;
				String portStr = server.getAttributes().get(ProtocalXmlParser.XML_SERVER_ATTR_PORT);
				int port = 9001;
				if (portStr != null && !portStr.trim().equals("")) {
					port = Integer.valueOf(portStr);
				}

				if (ip != null) {
					address = new InetSocketAddress(ip, port);
				} else {
					address = new InetSocketAddress(port);
				}
				// Configure the server.
				ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

				// Set up the event pipeline factory.
				bootstrap.setPipelineFactory(new KKServerPipeline(server.getServerId()));
				bootstrap.setOption("child.tcpNoDelay", true);
				bootstrap.setOption("child.keepAlive", true);
				bootstrap.setOption("reuseAddress", true);

				// Bind and start to accept incoming connections.
				bootstrap.bind(address);

				logger.debug("server ip=" + ip + ",port=" + port + " is started");
			}
		}

	}

	public static void main(String[] args) {
		Server server = new Server();
		try {
			server.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}