package com.ccagame.protocal.server;



import java.io.ByteArrayOutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ccagame.protocal.ProtocalUtil;
import com.ccagame.protocal.model.ServerInfo;
import com.ccagame.protocal.tool.PubUtil;


public class KKServerHandler extends SimpleChannelHandler {
	private static Logger logger = LoggerFactory.getLogger(KKServerHandler.class);

	private String serverId;
	private ByteArrayOutputStream baos;

	private ServerInfo si;

	public KKServerHandler(String serverId) {
		this.serverId = serverId;
		this.baos = new ByteArrayOutputStream();
		this.si = ProtocalUtil.getServerInfo(serverId);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		ChannelBuffer cbuffer = (ChannelBuffer) e.getMessage();
		byte[] d = cbuffer.array();
		baos.write(d);
		logger.debug("read count=" + d.length);
		if (baos.size() >= 11) {
			Server.exec.execute(new RequestProcessThread(e.getChannel(),baos));
		}
		cbuffer.clear();
	}

	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		logger.debug("channel open");

	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		logger.debug("channel connected");
		super.channelConnected(ctx, e);

		super.channelOpen(ctx, e);
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		logger.debug("channel disconnected");
		super.channelDisconnected(ctx, e);
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		logger.debug("channel close");
		super.channelClosed(ctx, e);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		logger.debug("exception");
		logger.info(e.toString());
	}

	public void processData(ByteArrayOutputStream baos, Channel channel) throws Exception {
		while (true) {
			if (baos.size() < 11) {
				break;
			}
			int totalLength = 0;
			int version = 0;
			byte[] tmpData = baos.toByteArray();
			totalLength = PubUtil.getIntData(tmpData, si.getLengthStartPos(), si.getLengthLength());

			version = PubUtil.getIntData(tmpData, si.getVersionStartPos(), si.getVersionLength());

			if (version > 256) {
				throw new Exception("the socket data is wrong");
			}

			int suffixLength = 0;
			if ("true".equalsIgnoreCase(si.getAttributes().get("hasSuffixByte"))) {
				suffixLength = 4;
			}
			if (tmpData.length >= totalLength + suffixLength) {
				byte[] data = PubUtil.copyArray(tmpData, totalLength);
				baos.reset();
				if (suffixLength > 0) {
					byte[] b = new byte[suffixLength];
					System.arraycopy(tmpData, totalLength, b, 0, suffixLength);
					baos.write(tmpData, totalLength + suffixLength, tmpData.length - totalLength - suffixLength);

					if (PubUtil.byteArrayToInt(b) == 16843009) {
						
					} else {
						logger.info("wrong packet, cannot get the last 4 byte");
						channel.close();
					}
				} else {
					baos.write(tmpData, totalLength, tmpData.length - totalLength);
					writeData(data,channel);
					break;
				}

			} else {
				break;
			}
		}
	}
	
	private void writeData(byte[] data ,Channel channel){
		RequestProcessor reqProcessor = new RequestProcessor(data, serverId);
		byte[] b = reqProcessor.process();
		ChannelBuffer cbuffer = ChannelBuffers.copiedBuffer(b);
		channel.write(cbuffer);
	}

	private class RequestProcessThread implements Runnable {
		private Channel channel;
		private ByteArrayOutputStream baos;

		public RequestProcessThread(Channel channel,ByteArrayOutputStream baos) {
			this.channel = channel;
			this.baos = baos;
		}

		public void run() {
			try {
				processData(baos, channel);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
