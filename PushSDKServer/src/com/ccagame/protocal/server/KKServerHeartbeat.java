package com.ccagame.protocal.server;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KKServerHeartbeat extends IdleStateAwareChannelHandler {
	private static Logger logger = LoggerFactory.getLogger(KKServerHeartbeat.class);

	public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {
		super.channelIdle(ctx, e);
		if (e.getState() == IdleState.ALL_IDLE) {
			logger.debug("close the connection [" + e.getChannel() + "]");
			e.getChannel().close();
		}
	}
}
