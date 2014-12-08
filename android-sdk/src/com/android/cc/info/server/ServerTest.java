package com.android.cc.info.server;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class ServerTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Map<String, Object> header = new HashMap<String, Object>();
		header.put("version", 1);
		header.put("command", 1);
		JSONObject json = new JSONObject();
		json.optInt("version", 1);
		System.out.println(json.toString());
	}

}
