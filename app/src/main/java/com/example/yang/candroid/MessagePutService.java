package com.example.yang.candroid;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MessagePutService extends Service {

    private static String TAG = "MessagePutService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "create " + TAG);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "destroy " + TAG);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }
}
