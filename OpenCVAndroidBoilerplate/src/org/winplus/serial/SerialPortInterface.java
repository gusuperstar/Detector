package org.winplus.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import org.winplus.serial.utils.SerialPort;

import com.example.opencvandroidboilerplate.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;

public class SerialPortInterface //extends Activity {
{
	protected Application mApplication;
	protected SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	private String mReception;
	
	public OutputStream getOutputStream()
	{
		return this.mOutputStream;
	}

	private class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
				int size;
				try {
					byte[] buffer = new byte[64];
					if (mInputStream == null)
						return;
					
					/**
					 * �����readҪ����ע�⣬����һֱ�ȴ����ݣ��ȵ���ĵ��ϣ�����ʯ�á����Ҫ�ж��Ƿ������ɣ�ֻ�����ý�����ʶ��������������Ĵ���
					 */
					size = mInputStream.read(buffer);
					if (size > 0) {
						onDataReceived(buffer, size);
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

//	private void DisplayError(int resourceId) {
//		AlertDialog.Builder b = new AlertDialog.Builder(this);
//		b.setTitle("Error");
//		b.setMessage(resourceId);
//		b.setPositiveButton("OK", new OnClickListener() {
//			public void onClick(DialogInterface dialog, int which) {
//				SerialPortActivity.this.finish();
//			}
//		});
//		b.show();
//	}

//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
	public SerialPortInterface()
	{
		Log.i("xxxxx", "Goose before onCreate1");
		mApplication = new Application();//(Application) getApplication();
		Log.i("xxxxx", "Goose after getApplication");
		try {
			Log.i("xxxxx", "Goose before getSerialPort");
			mSerialPort = mApplication.getSerialPort();
			Log.i("xxxxx", "Goose after getSerialPort");
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
			Log.i("xxxxx", "Goose before ReadThread");
			/* Create a receiving thread */
			mReadThread = new ReadThread();
			mReadThread.start();
			Log.i("xxxxx", "Goose after ReadThread");
		} catch (SecurityException e) {
			Log.i("", "Goose SerialPortInterface exception:" + R.string.error_security);
//			DisplayError(R.string.error_security);
		} catch (IOException e) {
			Log.i("", "Goose SerialPortInterface exception:" + R.string.error_unknown);
//			DisplayError(R.string.error_unknown);
		} catch (InvalidParameterException e) {
			Log.i("", "Goose SerialPortInterface exception:" + R.string.error_configuration);
//			DisplayError(R.string.error_configuration);
		}
	}
	
    
    protected void onDataReceived(final byte[] buffer, final int size) 
    {
    	if (mReception != null) {
    		mReception = new String(buffer, 0, size);
        }
                   
    }

//	protected abstract void onDataReceived(final byte[] buffer, final int size);

//	@Override
//	protected void onDestroy() {
//		if (mReadThread != null)
//			mReadThread.interrupt();
//		mApplication.closeSerialPort();
//		mSerialPort = null;
//		super.onDestroy();
//	}
}
