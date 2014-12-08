package com.ccagame.protocal.server;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ccagame.protocal.ProtocalUtil;
import com.ccagame.protocal.model.ServerInfo;
import com.ccagame.protocal.tool.PubUtil;

public class KKServerPipeline implements ChannelPipelineFactory {
	private static Logger logger = LoggerFactory.getLogger(KKServerPipeline.class);
	private HashedWheelTimer hashedWheelTimer;
	
	
	private String serverId;
	public KKServerPipeline(String serverId) {
		this.serverId = serverId;
		this.hashedWheelTimer = new HashedWheelTimer();
	}

	public ChannelPipeline getPipeline() throws Exception {
		
		ServerInfo serverInfo = ProtocalUtil.getServerInfo(serverId);
		Integer bothIdleTime = 0;
		String bothIdleTimeStr = serverInfo.getAttributes().get("bothIdleTime");
		if (PubUtil.isDigital(bothIdleTimeStr)) {
			bothIdleTime = Integer.valueOf(bothIdleTimeStr);
		}
		Integer readIdleTime = 0;
		String readIdleTimeStr = serverInfo.getAttributes().get("readIdleTime");
		if (PubUtil.isDigital(readIdleTimeStr)) {
			readIdleTime = Integer.valueOf(readIdleTimeStr);
		}

		Integer writeIdleTime = 0;
		String writeIdleTimeStr = serverInfo.getAttributes().get("writeIdleTime");
		if (PubUtil.isDigital(writeIdleTimeStr)) {
			writeIdleTime = Integer.valueOf(writeIdleTimeStr);
		}
		
		ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("timeout", new IdleStateHandler(hashedWheelTimer, readIdleTime, writeIdleTime, bothIdleTime));
		pipeline.addLast("heartbeat", new KKServerHeartbeat());
		pipeline.addLast("handler", new KKServerHandler(serverId));
		return pipeline;
	}

}
