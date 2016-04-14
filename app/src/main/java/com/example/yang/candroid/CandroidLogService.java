package com.example.yang.candroid;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import org.isoblue.can.CanSocketJ1939;
import org.isoblue.can.CanSocketJ1939.Filter;
import org.isoblue.can.CanSocketJ1939.J1939Message;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class CandroidLogService extends Service {

    public static final String FOREGROUND_STOP =
            "com.example.yang.candroid.CandroidLogService.FOREGROUND.stop";
    public static final String FOREGROUND_START =
            "com.example.yang.candroid.CandroidLogService.FOREGROUND.start";
    public static final String BROADCAST_ACTION =
            "com.example.yang.candroid.CandroidLogService.broadcast";

    public static final int NOTIFICATION_ID = 101;
    private static final String TAG = "CandroidLogService";
    private static final String CAN0 = "can0";
    private static final String CAN1 = "can1";

    private CanSocketJ1939 mCan0Socket;
    private CanSocketJ1939 mCan1Socket;
    private ArrayList<Filter> mFilters = new ArrayList<Filter>();
    private boolean mSaveFiltered = false;
    public J1939Message mCan0Msg;
    public J1939Message mCan1Msg;
    private FileOutputStream mFos;
    private OutputStreamWriter mOsw;
    private Handler msgHandler;
    private Intent bcIntent;
    private recvThread mThread;

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

        postOldData();
        if (mCan0Socket == null && mCan1Socket == null) {
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

            mFilters = (ArrayList<Filter>) intent
                    .getSerializableExtra("filter_list");
            mSaveFiltered = intent.getExtras().getBoolean("save_option");
            setupCanSocket();
            mThread = new recvThread();
            mThread.start();
        }

        return START_STICKY;
    }

    private void postOldData() {
        Log.d(TAG, "in postOldData()");
        bcIntent = new Intent(BROADCAST_ACTION);
        Bundle b = new Bundle();
        b.putBoolean("saveOption", mSaveFiltered);
        b.putSerializable("filters", mFilters);
        bcIntent.putExtra("serviceBundle", b);
        sendBroadcast(bcIntent);
    }

    private void setupCanSocket() {
        try {
            mCan0Socket = new CanSocketJ1939(CAN0);
            mCan0Socket.setPromisc();
            mCan0Socket.setTimestamp();
            if (mSaveFiltered) {
                mCan0Socket.setJ1939Filter(mFilters);
            }
        } catch (IOException e) {
            Log.e(TAG, "socket creation on " + CAN0 + " failed");
        }

        try {
            mCan1Socket = new CanSocketJ1939(CAN1);
            mCan1Socket.setPromisc();
            mCan1Socket.setTimestamp();
            if (mSaveFiltered) {
                mCan1Socket.setJ1939Filter(mFilters);
            }
        } catch (IOException e) {
            Log.e(TAG, "socket creation on " + CAN1 + " failed");
        }
    }

    private void closeCanSocket() {
        if (mCan0Socket != null) {
            try {
                mCan0Socket.close();
                mCan0Socket = null;
            } catch (IOException e) {
                Log.e(TAG, "cannot close socket");
            }
        }

        if (mCan1Socket != null) {
            try {
                mCan1Socket.close();
                mCan1Socket = null;
            } catch (IOException e) {
                Log.e(TAG, "cannot close socket");
            }
        }
    }

    private void createFile() {
        long unixtime = System.currentTimeMillis() / 1000L;
        String timestamp = Long.toString(unixtime);
        String filename = timestamp + ".log";
        try {
            mFos = new FileOutputStream("/sdcard/Log/" + filename);
            mOsw = new OutputStreamWriter(mFos);
        } catch (Exception e) {
            Log.e(TAG, "cannot create fd");
            return;
        }
    }

    private Notification getCompatNotification() {
        Builder builder = new Builder(this);
        builder.setSmallIcon(R.drawable.computer)
                .setContentTitle("CANdroid Log")
                .setContentText("Click to return")
                .setWhen(System.currentTimeMillis());
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, 0);
        builder.setContentIntent(contentIntent);
        Notification notification = builder.build();
        return notification;
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
                    if (mCan0Socket != null && mCan1Socket != null) {
                        if (mCan0Socket.select(1) == 0) {
                            mCan0Msg = mCan0Socket.recvMsg();
                            if (mOsw == null) {
                                createFile();
                            }
                            try {
                                mOsw.append(mCan0Msg.toString() + "\n");
                            } catch (Exception e) {
                                Log.e(TAG, "cannot append to file");
                            }
                        }
                        if (mCan1Socket.select(1) == 0) {
                            mCan1Msg = mCan1Socket.recvMsg();
                            if (mOsw == null) {
                                createFile();
                            }
                            try {
                                mOsw.append(mCan1Msg.toString() + "\n");
                            } catch (Exception e) {
                                Log.e(TAG, "cannot append to file");
                            }
                        } else {
                            msgHandler.sendEmptyMessage(0);
                        }
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
            SystemClock.sleep(1000);
            try {
                if (mOsw != null) {
                    mOsw.close();
                }
                if (mFos != null) {
                    mFos.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "cannot close fd");
            }
            closeCanSocket();
        }
    }

}
