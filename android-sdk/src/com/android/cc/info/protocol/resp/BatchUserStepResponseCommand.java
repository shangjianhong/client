package com.android.cc.info.protocol.resp;

import com.android.cc.info.protocol.CustomException;


public class BatchUserStepResponseCommand extends ResponseCommand {
	private static final String TAG = "BatchUserStepResponseCommand";
	
	public BatchUserStepResponseCommand(byte[] data) throws CustomException {
		super(data);
	}
	
	@Override
	protected void parseBody() {
		
	}
}
