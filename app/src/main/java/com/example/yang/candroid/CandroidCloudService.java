package com.example.yang.candroid;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

import org.isoblue.can.CanSocketJ1939;
import org.isoblue.can.CanSocketJ1939.J1939Message;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.UnknownHostException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class CandroidCloudService extends Service {

    public static final String FOREGROUND_STOP =
            "com.example.yang.candroid.CandroidCloudService.FOREGROUND.stop";
    public static final String FOREGROUND_START =
            "com.example.yang.candroid.CandroidCloudService.FOREGROUND.start";
    public static final int NOTIFICATION_ID = 102;
    public static final String CAN_INTERFACE = "can0";
    public static OADAConfiguration mConfig;
    public static OADAAccessToken mToken;
    public static String tmpFile = "tmp.txt";
    public static int i = 0;
    private static String TAG = "CandroidCloudService";
    public CanSocketJ1939 mSocket;
    public J1939Message mMsg;
    public Handler msgHandler;
    public recvThread mThread;
    public RequestQueue mQueue;
    public int timehash7;
    public int timehash3;
    public String mResUrl;
	public MsgAdapter mMsgBuffer;

    @Override
    public void onCreate() {

        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {

        if (mThread != null) {
            mThread.stop();
            mThread = null;
        }
        if (mQueue != null) {
            mQueue.cancelAll(this);
            mQueue = null;
        }
        super.onDestroy();
        Log.d(TAG, "destroy " + TAG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mSocket == null) {
            if (FOREGROUND_START.equals(intent.getAction())) {
                Log.i(TAG, "in onStartCommmand(), start " + TAG);
                startForeground(NOTIFICATION_ID,
                        getCompatNotification());
            }

            msgHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                }
            };

            setupCanSocket();
            mQueue = Volley.newRequestQueue(getApplicationContext());

            mMsgBuffer = new MsgAdapter(1000 * 60 * 60);

            mThread = new recvThread();
            mThread.start();
        }

        return START_STICKY;
    }

    private Notification getCompatNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (this);
        builder.setSmallIcon(R.drawable.computer)
                .setContentTitle("CANdroid Cloud")
                .setWhen(System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, 0);
        builder.setContentIntent(contentIntent);
        Notification notification = builder.build();

        return notification;
    }

/*	public String buildResUrl(J1939Message msg) {

        int pgn = msg.pgn;
        int timeHash7 = (int) (msg.timestamp - (msg.timestamp % 10000000L));
        int timeHash3 = (int) (msg.timestamp - msg.timestamp % 1000L);

        String resUrl = "bookmarks/" + "candroid/" +
                "pgn/" + Integer.toString(pgn) +
                "/timehash-7/" + Integer.toString(timeHash7) +
                "/timehash-3/" + Integer.toString(timeHash3) + "/";

        return resUrl;
    }
*/
    public void setupCanSocket() {

        try {
            mSocket = new CanSocketJ1939(CAN_INTERFACE);
            mSocket.setPromisc();
            mSocket.setTimestamp();
        } catch (IOException e) {
            Log.e(TAG, "socket creation on " + CAN_INTERFACE + " failed");
        }
    }

    public void closeCanSocket() {

        if (mSocket != null) {
            try {
                mSocket.close();
                mSocket = null;
            } catch (IOException e) {
                Log.e(TAG, "cannot close socket");
            }
        }
    }

 /*   public void writeToBufferFile(MsgAdapter msgArray) {

        if (mTmpFile == null) {
            Log.d(TAG, "new tmpfile");
            mTmpFile = new File("/sdcard/Log/buffer");
        }

        try {
            FileChannel fileChannel = new RandomAccessFile(mTmpFile, "rw")
                    .getChannel();

            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode
                    .READ_WRITE, 0, 4096 * 8);

            for (int i = 0; i < msgArray.getCount(); i++) {
                buffer.put(msgArray.getItem(i).getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFromBufferFile(File file) {

        if (file == null) {
            Log.wtf(TAG, "buffer is null");
        }

        try {
            FileChannel fileChannel = new RandomAccessFile(file, "r")
                    .getChannel();

            MappedByteBuffer buffer = fileChannel.map(
                    FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());

            for (int i = 0; i < buffer.limit(); i++) {
                System.out.print((char) buffer.get());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
    public class recvThread implements Runnable {

        Thread recvThread;

        public void start() {
            if (recvThread == null) {
                recvThread = new Thread(this);
                recvThread.start();
            }
        }

        public void run() {
            while (!recvThread.interrupted()) {
                try {
                    if (mSocket.select(1) == 0) {
                        mMsg = mSocket.recvMsg();

                        if (mQueue != null) {
/*                            if (i == 1000 || i == 0) {
                                mResUrl = buildResUrl(mMsg);
                                i = 0;
                            }
*/
                            MessageRequest req = new MessageRequest(Request.Method.PUT, mConfig,
																	mToken, mMsg.toString(),
																	"bookmarks/candroid/",
																	new ErrorListener(),
																	new Listener());
//                          mResUrl);
                            mQueue.add(req);
                            i++;
                        }
                    } else {
                        msgHandler.sendEmptyMessage(0);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "cannot select on socket");
                }
            }
        }

        public void stop() {
            if (recvThread != null) {
                recvThread.interrupt();
            }
            closeCanSocket();
        }
    }

    private class ErrorListener implements Response.ErrorListener {

		@Override
		public void onErrorResponse(VolleyError error) {

			String json = null;
			String trimmedString = null;

			/* error when network is down*/
			if (error.getCause() != null && error.getCause() instanceof
					UnknownHostException) {
				Log.d(TAG, "no internet connection");
				if (mMsg != null) {
					mMsgBuffer.add(mMsg.toString());
				}
			}

			/* this part should parse out error codes from server */
			NetworkResponse response = error.networkResponse;
			if (response != null && response.data != null) {
				json = new String(response.data);
				try {
					JSONObject obj = new JSONObject(json);
					trimmedString = obj.getString("message");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			if (json != null) {
				VolleyLog.d("Error: " + trimmedString);
			}
		}
	}

    private class Listener implements Response.Listener<NetworkResponse> {

        @Override
        public void onResponse(NetworkResponse response) {
			if ((!mMsgBuffer.isEmpty()) && (mMsg != null)) {
				for (int i = 0; i < mMsgBuffer.getCount(); i++) {
					MessageRequest req = new MessageRequest(Request.Method.POST, mConfig, mToken,
							makeJson(mMsgBuffer.getItem(i)).toString(), "bookmarks/candroid/",
							new ErrorListener(), new Listener());
					mQueue.add(req);
				}
				mMsgBuffer.clear();
			}
            VolleyLog.d("onResponse: " + response.toString());

        }
    }

    public static JSONObject makeJson(String msg) {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", msg.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
