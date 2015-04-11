package com.example.protocol;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MyHandler extends Handler 
{
	public static final int CONNECT = 0;
	public static final int CLOSE = 1;
	public static final int SEND = 2;
	public static final int RECEIVE = 3;
		@Override  
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CONNECT:
				//textView_result.setText(msg.obj.toString());
//				Log.i(TAG, "Goose MyHandler CONNECT:"+msg.obj);
//				if((Integer)msg.obj == 1)
//					isTCPConnected = true;
//				else
//					isTCPConnected = false;
//				Log.i(TAG, "Goose MyHandler set isTCPConnected:"+isTCPConnected);
				break;
			case SEND:
				//textView_result.setText("用了"+msg.arg1+"ms");
//				Log.i(TAG, "Goose MyHandler SEND:"+msg.arg1);
				break;
			case RECEIVE:
				//textView_result.setText("收到了信息："+msg.obj.toString());
//				Log.i(TAG, "Goose MyHandler RECEIVE:"+msg.obj.toString());
				break;	
			default:break;
			}
			super.handleMessage(msg);
		}
	}