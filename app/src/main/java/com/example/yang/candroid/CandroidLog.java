package com.example.yang.candroid;

import java.io.IOException;

import android.app.Service;
import android.os.IBinder;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.widget.Toast;

import de.greenrobot.event.EventBus;

import org.isoblue.can.CanSocket;
import org.isoblue.can.CanSocketJ1939;
import org.isoblue.can.CanSocketJ1939.J1939Message;

public class CandroidLog extends Service {
	
	private CanSocketJ1939 mSocket;
	private EventBus bus = EventBus.getDefault();
	private Handler msgHandler;
	private recvThread mThread;
	public J1939MsgEvent event = null;
	boolean mAllowRebind;

	public void openJ1939Socket() {
		try {
			mSocket = new CanSocketJ1939("can0");
			mSocket.setPromisc();
			mSocket.setTimestamp();
		} catch (final IOException e) {
			Toast.makeText(this, "socket creation failed", Toast.LENGTH_LONG).show();
		}
	}
	
	public void closeJ1939Socket() {
		try {
			mSocket.close();
		} catch (final IOException e) {
			Toast.makeText(this, "socket closure failed", Toast.LENGTH_LONG).show();
		}
	}
	
	/* Called when service is created */
	@Override
	public void onCreate() {
	
	}

	/* Called after startService() */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "Start logging...", Toast.LENGTH_LONG).show();
		
		msgHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Toast.makeText(CandroidLog.this,
					"10 secs passed, no J1939 msg", Toast.LENGTH_SHORT).show();
			}
		};

		openJ1939Socket();
		
		mThread = new recvThread();
		mThread.start();

		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/* Called when all clients have unbound with unbindService() */
   	@Override
   	public boolean onUnbind(Intent intent) {
      	return mAllowRebind;
   	}

   	/* Called when a client is binding to the service with bindService()*/
   	@Override
   	public void onRebind(Intent intent) {

   	}

   	/* Called when The service is no longer used and is being destroyed */
   	@Override
   	public void onDestroy() {
		mThread.stop();	
		closeJ1939Socket();
		super.onDestroy();
		Toast.makeText(this, "Logging stopped", Toast.LENGTH_LONG).show();
   	}

	public class recvThread implements Runnable {
		Thread recvThread;
		
		public void start() {
			if (recvThread == null) {
          		recvThread = new Thread(this);
          		recvThread.start();
       		}
    	}

		public void stop() {
       		if (recvThread != null) {
          		recvThread.interrupt();
      		}
    	}
			
		public void run() {
			while (!recvThread.interrupted()) {
				try {
					if (mSocket.select(10) == 0) {
						event = new J1939MsgEvent(mSocket.recvMsg());
						bus.post(event);
					} else {
						msgHandler.sendEmptyMessage(0);	
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
