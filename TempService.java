package com.example.hrker.cpu_temp;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Date;

public class TempService extends Service {

    private static final String TAG = "BroadcastService";
    public static final String BROADCAST_ACTION = "com.example.hrker.cpu_temp";

    private final Handler handler = new Handler();
    Intent intent;
    ScanPath scan;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
        scan = new ScanPath();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("LOG", "TempService Started");
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("LOG", "TempService Destroyed");
        super.onDestroy();

    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            DisplayLoggingInfo();
            handler.postDelayed(this, 1000); // 5 seconds
        }
    };

    private void DisplayLoggingInfo() {
        Log.d(TAG, "entered DisplayLoggingInfo");
        intent.putExtra("temperature", scan.startScan() + "F");
        sendBroadcast(intent);
    }
}
