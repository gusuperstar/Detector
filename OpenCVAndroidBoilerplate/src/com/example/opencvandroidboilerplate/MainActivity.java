package com.example.opencvandroidboilerplate;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.winplus.serial.SerialPortInterface;

import com.example.protocol.MyHandler;
import com.example.protocol.TcpThread;
import com.example.protocol.WifiConnect;

//import com.example.linptcptest.MainActivity.TcpThread;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements CvCameraViewListener2 {

	private static final String TAG = "Rectangle";
	private String imei = "862937024426652";
	private String l2 = "76379j098509639";
	private String l3 = "fadgafgsdfewqrjhkk";
	private String ssid = "ZCY304";
	private String pwd = "Welcome1";
	private CameraBridgeViewBase mOpenCvCameraView;
	private WifiConnect mWifiConn;
	private static boolean isWIFIConnected = false;
	private static boolean isTCPConnected = false;
	private SerialPortInterface spi;
	private long lastSendTime = 0;
	private int nframe = 0;
	//for home key
	public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;
	//Socket socket ;  
	PrintWriter out;

	private long mExitTime1 = 0;
	private long mExitTime2 = 0;
	private long mExitTime3 = 0;
		
	private final int TIMER_INVALIDATE = 51706;
	private String SOCKET_IP = "192.168.1.100";//"10.11.12.1"4001 192.168.1.102
	private int sendnum = 1;
	private String last_signal0 = "0";
	private String last_signal1 = "0";
	private String last_signal2 = "0";

//	public static final int CONNECT = 0;
//	public static final int CLOSE = 1;
//	public static final int SEND = 2;
//	public static final int RECEIVE = 3;
	boolean flag=true;
	public  long startTime;
	//Handler mTcpHandler;
//	MyHandler myHandler ;
//	TcpThread myTcpThread;
	static
	{
		if (!OpenCVLoader.initDebug()) 
	    {
	    } 
	    else 
	    {
	        	System.loadLibrary("zzz");
	    }
	}
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
	    @Override
	    public void onManagerConnected(int status) {
	        switch (status) {
	            case LoaderCallbackInterface.SUCCESS:
	            {
	            	System.loadLibrary("zzz");
	                mOpenCvCameraView.enableView();
	            } break;
	            default:
	            {
	                super.onManagerConnected(status);
	            } break;
	        }
	    }
	};

	@Override
	public void onResume()
	{
	    super.onResume();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    //
		TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);    
		Log.i(TAG, "Goose called onCreate cpu:("+tm.getDeviceId()+"),("+l2+")");
//		if(!tm.getDeviceId().toLowerCase().equals(imei))
//			finish(); 
	    super.onCreate(savedInstanceState);
//	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//	    getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//关键代码
//	    
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    
	    
	    setContentView(R.layout.activity_main);
	    mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.CameraView	);
	    mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
	    mOpenCvCameraView.setCvCameraViewListener(this);
	    mOpenCvCameraView.setFocusable(false); 
	    
	    mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
//	    mOpenCvCameraView.setMaxFrameSize(480, 320);
//	    myHandler = new MyHandler() ;
//	    myTcpThread = new TcpThread();
//	    myTcpThread.start();	
//	    Log.i(TAG, "Goose new TcpThread().start 1");

//	    connectWIFI(3);
//	    connectTCP(3);
	    Log.i(TAG, "Goose init finish");
	    
	    //spi = new SerialPortInterface();
	}
	
	public boolean connectTCP(int retries)
	{
//		for(int i = 0; i < 10000;i++)
//		{
//		try
//		{
//		Message	connectMsg = new Message();	
//		connectMsg.obj =  SOCKET_IP;
//		connectMsg.what =CONNECT;
//		this.myTcpThread.getHandler().sendMessage(connectMsg);
//		isTCPConnected = true;
//		break;
//		}
//		catch(Exception e){
//	    	
//	    	Log.i(TAG, "Goose connectTCP Exception :"+e);
//	    	continue;
//	    }
//		}
//		
//		Log.i(TAG, "Goose connectTCP isTCPConnected 2:"+isTCPConnected);
		return isTCPConnected;
	}
	
	public boolean connectWIFI(int retries)
	{
//		int cnt = 0;
//		while(!isWIFIConnected && cnt < retries)
//		{
//			try
//			{
//				Log.i(TAG, "Goose connectWIFI connect 1"); 
//				mWifiConn = new WifiConnect(this);
//				mWifiConn.openWifi();
//				isWIFIConnected = mWifiConn.addNetwork(mWifiConn.CreateWifiInfo(ssid, pwd, 3));
//				Thread.sleep(1000);
//			}
//			catch(Throwable e) 
//			{
//				Log.i(TAG, "Goose isWIFIConnected failed 1:"+cnt);
//			}
//			cnt++;
//		}
//		if(isWIFIConnected)
//			Log.i(TAG, "Goose isWIFIConnected succ!");
//		else
//		{
//			Log.i(TAG, "Goose isWIFIConnected onDestroy!");
//		}
		isWIFIConnected = true;
		return isWIFIConnected;
	}
	
	public boolean disconnect()
	{
//		Message	closeMsg = new Message();	
//		closeMsg.what =CLOSE;
//		this.myTcpThread.getHandler().sendMessage(closeMsg);
//		mWifiConn.closeWifi();
//		mWifiConn.openWifi();
		
		return true;
	}

	@Override
	public void onPause()
	{
	    super.onPause();
	    if (mOpenCvCameraView != null)
	        mOpenCvCameraView.disableView();
	}

	public void onDestroy() {
	    super.onDestroy();
	    if (mOpenCvCameraView != null)
	        mOpenCvCameraView.disableView();
//	    if(isTCPConnected)
//	    {
//	    	Message	closeMsg = new Message();	
//	    	closeMsg.what =CLOSE;
//	    	this.myTcpThread.getHandler().sendMessage(closeMsg);
//	    	isTCPConnected = false;
//	    }
	}

	public void onCameraViewStarted(int width, int height) {
		Log.i(TAG, "Goose onCameraViewStarted width:"+width+ " height:"+height);
	}

	public void onCameraViewStopped() {
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) 
	{
		
//		Log.i(TAG, "Goose onCameraFrame in 1");
//		long enterTime = System.currentTimeMillis();
		Mat mat = new Mat();
		Mat input = inputFrame.rgba();
		
//		float scale = 0.6f; 
//		Size dsize = new Size(input.width() * scale, input.height() * scale); // 设置新图片的大小  
//        Mat img2 = new Mat();// 创建一个新的Mat（opencv的矩阵数据类型）  //dsize, CvType.CV_16S, 
//        Imgproc.resize(input, img2,dsize);//调用Imgproc的Resize方法，进行图片缩放  
		//, 0.5, 0.5, INTER_AREA  
		DV dv = new DV();
		dv = zzz(input.getNativeObjAddr(), mat.getNativeObjAddr(), dv);
		
		
//		Log.i(TAG, "Goose onCameraFrame 2 succ:"
//				+" d0:"+dv.d0+" d1:"+dv.d1+" d2:"+dv.d2+" v1:"+dv.v1+" v2:"+dv.v2);
//		
//
//		if(isTCPConnected)//"10.11.12.1"4001
//		{
			Log.i(TAG, "Goose onCameraFrame ("+input.width()+","+input.height()+") sent:"+nframe++);
			String signal0 = last_signal0;
			String signal1 = last_signal1;
			String signal2 = last_signal2;
			if(dv.d0 > 0)
			{
				signal0 = "C%" + 1 + "0" ;
//				sendTCPMsg(signal0);
//				sendSerialMsg(signal0);
			}
			else if(dv.d1 > 0 || dv.d2 > 0)
			{
				if(dv.d1 > 0)
				{
					if(dv.v1 > 9)
					{
						dv.v1 = 9;
					}
					String v = String.valueOf(dv.v1);
					signal1 = "C*" + v + "0" ;
//					Log.i(TAG, "Goose onCameraFrame 2: v1:" + v + " x:" + signal1);
//					sendTCPMsg(signal1);
//					sendSerialMsg(signal1);
				}
				else
				{
					signal1 = "C*" + 0 + "0" ;
				}
				if(dv.d2 > 0)
				{
					if(dv.v2 > 9)
					{
						dv.v2 = 9;
					}
					String v = String.valueOf(dv.v2);
					signal2 = "C#" + v + "0" ;
//					Log.i(TAG, "Goose onCameraFrame 2: v1:" + v + " x:" + signal2);
//					sendTCPMsg(signal2);
//					sendSerialMsg(signal2);
				}
				else
				{
					signal2 = "C#" + 0 + "0" ;
				}
			}
			else
			{
				signal0 = "C%" + 0 + "0" ;
				//sendTCPMsg(signal);
			}
		
	
//		else
//			WifiConnect.udpSend("0000", SOCKET_IP, SOCKET_PORT);
		
		long outTime = System.currentTimeMillis();
		if(outTime - lastSendTime > 3000000)
		{
//			sendSerialMsg("C@00");
			lastSendTime = outTime;
		}
		
//		Size dsize2 = new Size(input.width() , input.height() ); // 设置新图片的大小  
//        Mat out = new Mat();// 创建一个新的Mat（opencv的矩阵数据类型）dsize2, CvType.CV_16S  
//        Imgproc.resize(mat, out, dsize2);
//		Log.i(TAG, "Goose frame cost:"+ (outTime-enterTime));
		
		return mat;//out;//mat;
		
	
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event) { 
//	    if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键 
//	        return true; 
//	    } else 
	    	if(keyCode == KeyEvent.KEYCODE_MENU) {//MENU键 
	        //监控/拦截菜单键 
	    		mExitTime1 = System.currentTimeMillis();
	    		if ((System.currentTimeMillis() - mExitTime3) < 300)
	    			finish();
		    	else
		    		return true; 
	    	
	    	
	    } else if(keyCode == KeyEvent.KEYCODE_POWER)
	    {
	    	return true; 
	    }
	    else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
	    	return true; 
	    else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP)
	    {
	    	if ((System.currentTimeMillis() - mExitTime1) < 300)
	    	{
	    		mExitTime2 = System.currentTimeMillis();
	    	}
	    	return true;
	    }
	    else if(keyCode == KeyEvent.KEYCODE_HOME)
	    {
	    	if ((System.currentTimeMillis() - mExitTime2) < 300)
	    		mExitTime3 = System.currentTimeMillis();
	    		
	    		return true;
	    		
	    }
	return super.onKeyDown(keyCode, event); 
	}
	
//	@Override 
//	public void onAttachedToWindow() //android 2.3
//	{ 
//		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD); 
//		super.onAttachedToWindow(); 
//	} 

	public void sendTCPMsg(String msg) {  
//		Message	sendMsg = new Message();	
//		sendMsg.obj = msg;
//		sendMsg.arg1 = 1;
//		sendMsg.what =SEND;
//		this.myTcpThread.getHandler().sendMessage(sendMsg);
		

    }  
	public void sendSerialMsg(String msg)
	{
		try {
			spi.getOutputStream().write(msg.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public native DV zzz(long matAddrInRGBA, long matAddrOutInRGBA, DV dv);
	
	
}