package com.example.yang.candroid;

import org.isoblue.can.CanSocket;
import org.isoblue.can.CanSocketJ1939;
import org.isoblue.can.CanSocketJ1939.Message;

import java.io.IOException;
import android.app.Service;
import android.os.IBinder;
import android.content.Intent;
import android.widget.Toast;

public class CandroidLog extends Service {
	
	private CanSocketJ1939 mSocket;
	private Message mMsg;
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
		Toast.makeText(this, "CANDroid rocks", Toast.LENGTH_LONG).show();
		openJ1939Socket();
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
		super.onDestroy();
		closeJ1939Socket();
		Toast.makeText(this, "CANDroid sucks", Toast.LENGTH_LONG).show();
   	}
}
