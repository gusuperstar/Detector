package com.example.protocol;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;



public class TcpThread extends Thread {
	
	Handler mTcpHandler;
	Socket socket = null;
	
	private int SOCKET_PORT = 4001;
	public static final int CONNECT = 0;
	public static final int CLOSE = 1;
	public static final int SEND = 2;
	public static final int RECEIVE = 3;
	long startTime = 0;
	public TcpThread()
	{
		
	}
	
	public Handler getHandler()
	{
		return this.mTcpHandler;
	}
	
	public void run() {			
		Looper.prepare();
		mTcpHandler = new Handler() {
			 public void handleMessage(Message msg) {
                  // 处理收到的消息
				 Log.i("Rectangle", "Goose TcpThread handleMessage 1");
				 switch(msg.what)
					{
						case CONNECT:
							Log.i("Rectangle", "Goose TcpThread connect 1");
							connect(msg.obj.toString());
							Log.i("Rectangle", "Goose TcpThread connect 2");
							break;
						case CLOSE:
							close();
							break;
						case SEND:
							long time = send(msg.obj.toString(),msg.arg1);
							Message	timeMsg = new Message();	
							timeMsg.arg1 = (int) time;
							timeMsg.what =SEND;
//							myHandler.sendMessage(timeMsg);
							break;
						
						default:break;
					}
              }
		};
	   Looper.loop();
	}
	
	 public void connect(String SOCKET_IP){
	    	Log.i("Rectangle", "Goose Tcp connect 1:"+SOCKET_IP +" port:"+SOCKET_PORT);
	    	try { 
	    		Log.i("Rectangle", "Goose Tcp connect 1.1");
	    		InetAddress serverAddr = InetAddress.getByName(SOCKET_IP);//TCPServer.SERVERIP 
	    		Log.i("Rectangle", "Goose Tcp connect 1.2");
	    		//Log.d("TCP", "C: Connecting...SOCKET_IP"); 
	    		startTime = System.nanoTime();
	        	socket = new Socket(serverAddr, SOCKET_PORT); 
	        	Log.i("Rectangle", "Goose Tcp connect 1.3");
	    	} catch(Exception e) { 
	    		long consumingTime = System.nanoTime()-startTime; 
//	    	    Log.e("TCP", "S: Error,,consumingTime"+consumingTime/1000/1000); 
//	    	    /e.get
	    	    Log.i("Rectangle", "Goose Tcp connect 1.5:"+e.toString());
	    	} 
	    	Log.i("Rectangle", "Goose Tcp connect 2");
	        	Message connectMsg = new Message();								        	
	        	connectMsg.what = CONNECT;	
	        	Log.i("Rectangle", "Goose Tcp connect 3");
	        	if(!socket.isConnected()){
	        		connectMsg.obj = 0;
	        	}
	        	Log.i("Rectangle", "Goose Tcp connect 4");
	        	connectMsg.obj = 1;
//	        	myHandler.sendMessage(connectMsg);
//	        	Log.i(TAG, "Goose Tcp connect 5");
//	    	    PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),true); 
//	    	    out.println(message);
//	    	    out.flush();
//	        	receive();

	    }
	 
	 private long send(String message,int sendnum){

	    	try{ 
			    Log.i("TCP", "C: Sending: '" + message + "'"); 			    
	    		startTime = System.nanoTime();		    
			    for(int i =sendnum;i>0;i--){
//			    mStrShow = "C: Sending: '" + message + "'";

		    	PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(socket.getOutputStream())),true); 
			    out.println(message);
			    }
			    long consumingTime = System.nanoTime()-startTime; 
			    System.out.println(consumingTime/1000/1000+"ms");
			    return consumingTime/1000/1000;
			    //out.flush();
		    
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    	 return 0;
	    }
	 
//   public void receive(){
// 	new Thread(){
//			public void run(){
//				//while(flag){
//				try{
//					while(!socket.isClosed()){
//					//while(socket.isConnected()){							
//						DataInputStream is = new DataInputStream(socket.getInputStream());
//						byte buffer [] = new byte[4*1024];
//						int temp = is.read(buffer);
//						String str = new String(buffer, 0, temp);
//						Message receiveMsg = new Message();							
//						receiveMsg.obj = str;
//						receiveMsg.what = RECEIVE;
//						myHandler.sendMessage(receiveMsg);
//					    //textView_result.setText(str);
//						Log.d("TCP","receive mStrShow-->"+str);
//						
//					//	sleep(300);
//					}
//				}
//				catch(Exception e){
//
//				}
//			//}
// 	}
//	}.start();
//}
	 
	 private void close(){
	    	try {
//	    		Log.d("TCP","socket close");
		    	socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				 Log.e("TCP", "S: Error", e); 
			} 
	    	
	    }
}