package com.example.hrker.cpu_temp;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Date;

public class TempService extends Service {
    // This class is the Service class that utilizes the ScanPath.java class
    // to retrieve the temperature values

    // Initialize variables
    public static final String BROADCAST_ACTION = "com.example.hrker.cpu_temp";
    private static final String TAG = "BroadcastService";
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

    // This Runnable sends data to MainActivity every 1 second
    // Fix time value as you see fit
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            DisplayLoggingInfo();
            handler.postDelayed(this, 1000); // 1000 = 1 second
        }
    };

    private void DisplayLoggingInfo() {
        Log.d(TAG, "entered DisplayLoggingInfo");
        intent.putExtra("temperature", scan.startScan());
        sendBroadcast(intent);
    }
}
