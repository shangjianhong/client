package com.android.cc.info.protocol.resp;

import com.android.cc.info.protocol.CustomException;


public class UserStepResponseCommand extends ResponseCommand {
	private static final String TAG = "UserStepResponseCommand";
	
	public UserStepResponseCommand(byte[] data) throws CustomException {
		super(data);
	}
	
	@Override
	protected void parseBody() {
		
	}
}
