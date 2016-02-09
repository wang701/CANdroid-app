package com.example.yang.candroid;

import android.app.Service;
import android.os.IBinder;
import android.os.Handler;
import android.os.Message;
import android.content.Intent;
import android.widget.Toast;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import org.isoblue.can.CanSocket;
import org.isoblue.can.CanSocketJ1939;
import org.isoblue.can.CanSocketJ1939.J1939Message;

//TODO:make it a foreground service

public class CandroidLog extends Service {
	private CanSocketJ1939 mSocket;
	private EventBus bus = EventBus.getDefault();
	private EventBus mFileBus = EventBus.getDefault();
	private FileOutputStream mFos;
	private OutputStreamWriter mOsw;
	private Handler msgHandler;
	private recvThread mThread;
	public J1939MsgEvent event = null;
	private boolean mAllowRebind;
	private boolean mWriteToFile = false;

	public void openJ1939Socket() {
		try {
			mSocket = new CanSocketJ1939("can0");
			mSocket.setPromisc();
			mSocket.setTimestamp();
		} catch (final IOException e) {
			Toast.makeText(this, "socket creation failed",
							Toast.LENGTH_LONG).show();
		}
	}

	public void closeJ1939Socket() {
		try {
			mSocket.close();
		} catch (final IOException e) {
			Toast.makeText(this, "socket closure failed",
							Toast.LENGTH_LONG).show();
		}
	}

	/* Called when service is created */
	@Override
	public void onCreate() {
		mFileBus.register(this);
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

	/* Called when the service heard FileOutEvent */
	@Subscribe(threadMode = ThreadMode.BACKGROUND)
	public void onEvent(FileOutEvent event) {
		mWriteToFile = event.WriteToFile;
		System.out.println("event received");
	}

	/* Called when The service is no longer used and is being destroyed */
	@Override
	public void onDestroy() {
		mThread.stop();
		closeJ1939Socket();
		mFileBus.unregister(this);
		super.onDestroy();
		Toast.makeText(this, "Logging stopped", Toast.LENGTH_LONG).show();
	}

	private void createFile() {
		long unixtime = System.currentTimeMillis() / 1000L;
		String timestamp = Long.toString(unixtime);
		String filename = timestamp + ".log";
		try {
			mFos = new FileOutputStream("/sdcard/Log/" + filename);
			mOsw = new OutputStreamWriter(mFos);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
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
			try {
				if (mOsw != null) {
					mOsw.close();
				}
				if (mFos != null) {
					mFos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			while (!recvThread.interrupted()) {
				try {
					if (mSocket.select(10) == 0) {
						event = new J1939MsgEvent(mSocket.recvMsg());
						bus.post(event);
						if (mOsw == null) {
							createFile();
						}
						try {
							mOsw.append(event.toString() + "\n");
						} catch (Exception e) {
							e.printStackTrace();
						}
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
