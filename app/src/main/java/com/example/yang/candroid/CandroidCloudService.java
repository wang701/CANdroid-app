package com.example.yang.candroid;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.isoblue.can.CanSocketJ1939;
import org.isoblue.can.CanSocketJ1939.J1939Message;

import java.io.IOException;

public class CandroidCloudService extends Service {

    private static String TAG = "CandroidCloudService";

    public static final String FOREGROUND_STOP =
            "com.example.yang.candroid.CandroidCloudService.FOREGROUND.stop";
    public static final String FOREGROUND_START =
            "com.example.yang.candroid.CandroidCloudService.FOREGROUND.start";

    public static final int NOTIFICATION_ID = 102;
    public static final String CAN_INTERFACE = "can0";

    public CanSocketJ1939 mSocket;
    public J1939Message mMsg;

    public Handler msgHandler;
    public recvThread mThread;

    public static OADAConfiguration mConfig;
    public static OADAAccessToken mToken;

    private RequestQueue mQueue;

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

            mThread = new recvThread();
            mThread.start();
        }

        return START_STICKY;
    }

    private Notification getCompatNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (this);
        builder.setSmallIcon(R.drawable.computer)
                .setContentTitle("CANdroid is pushing data to OADA ...")
                .setWhen(System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, 0);
        builder.setContentIntent(contentIntent);
        Notification notification = builder.build();
        return notification;
    }

    public void setupCanSocket() {

        try {
            mSocket = new CanSocketJ1939(CAN_INTERFACE);
            mSocket.setPromisc();
            mSocket.setTimestamp();
        } catch (IOException e) {
            Log.e(TAG, "socket creation on " + CAN_INTERFACE + " failed");
        }
    }

    private void closeCanSocket() {

        if (mSocket != null) {
            try {
                mSocket.close();
                mSocket = null;
            } catch (IOException e) {
                Log.e(TAG, "cannot close socket");
            }
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

        public void run() {
            while (!recvThread.interrupted()) {
                try {
                    if (mSocket.select(1) == 0) {
                        mMsg = mSocket.recvMsg();
                        if (mQueue != null) {
                            mQueue.add(new MessageRequest(Request.Method.PUT,
                                    mConfig, mToken, mMsg.toString()));
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
}
