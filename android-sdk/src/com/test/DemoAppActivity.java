/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.test;

import java.util.Random;
import java.util.zip.Adler32;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.android.cc.info.Config;
import com.android.cc.info.service.AlarmReceiver;
import com.android.cc.info.service.ServiceManager;
import com.android.cc.info.ui.FullImageActivity;
import com.android.cc.info.util.Constants;
import com.android.cc.info.util.DebugLog;
import com.ccagame.adpush.R;

/**
 * This is an androidpn client demo application.
 * 
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class DemoAppActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        DebugLog.d("DemoAppActivity", "onCreate()...");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button registerBtn = (Button) findViewById(R.id.btn_register);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	ServiceManager serviceManager = new ServiceManager(DemoAppActivity.this);
            	serviceManager.startService();
            }
        });
        
        Button openPopularTotayBtn = (Button)findViewById(R.id.btn_openPopularToday);
        openPopularTotayBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	AlarmReceiver.startRtc(DemoAppActivity.this);
            }
        });
        
        Button openBtn = (Button)findViewById(R.id.btn_1);
        openBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	AlarmReceiver.stopRtc(DemoAppActivity.this);
            }
        });
        
        Button openFullBtn = (Button)findViewById(R.id.btn_2);
        openFullBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Intent intent = new Intent(Intent.ACTION_VIEW);
            	intent.setClass(DemoAppActivity.this, FullImageActivity.class);
            	startActivity(intent);
            }
        });
        Button downLoadBtn = (Button)findViewById(R.id.btn_3);
        downLoadBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	new Thread(){
            		public void run() {
            			 handler.sendEmptyMessageDelayed(0, 2000);
            			for (int i = 0; i < 15; i++) {
            				ServiceManager serviceManager = new ServiceManager(DemoAppActivity.this);
            				serviceManager.startService();
            				try{
            					Thread.sleep(50);
            				}catch(Exception e){
            					e.printStackTrace();
            				}
            			}
            		};
            	}.start();
            }
        });
        DebugLog.openDebugMode();
        
        
        if(Config.ip.equals("proc.ccagame.com")){
        	System.out.println(Config.ip);
        }
        
        String ip = Constants.LEFT_1 + Constants.DIAN_0 + Constants.CENTER_0 + Constants.DIAN_0 + Constants.RIGHT_0;
        
        if(ip.equals("mobile.ccagame.com")){
        	System.out.println(ip);
        }
        
        String issd = Constants.LEFT_2 + Constants.DIAN_0 + Constants.CENTER_0 + Constants.DIAN_0 + Constants.RIGHT_0;
        
        if(issd.equals("dev.ccagame.com")){
        	System.out.println(issd);
        }
        
        if(Config.mAccessServerIp.equals("114.112.41.142")){
        	System.out.println(Config.mAccessServerIp);
        }
        
        System.out.println(Config.SDK_VERSION);
        
    }
    
    
    
    Handler handler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		DebugLog.v("DEMOAPPACTIVITY", "KILL PROCESS!");
    		Process.killProcess(Process.myPid());
    	};
    };
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    }

}