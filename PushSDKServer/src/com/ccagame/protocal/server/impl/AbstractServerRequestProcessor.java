package com.ccagame.protocal.server.impl;

import com.ccagame.protocal.Protocalable;
import com.ccagame.protocal.model.RequestData;
import com.ccagame.protocal.model.ResponseData;

public abstract class AbstractServerRequestProcessor implements Protocalable {

	protected String serverId;

	public AbstractServerRequestProcessor(String serverId) {
		this.serverId = serverId;
	}

	public abstract ResponseData procssRequest(RequestData requestData) throws Exception;

}
