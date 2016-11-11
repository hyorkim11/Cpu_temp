package com.example.hrker.cpu_temp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    // Variable Declarations
    private TextView tvDOS, tvDAPI, tvDNumCore, tvChipSet,
            tvCPUF, tvTemperature, tvTempType, tvBatTemp;
    public String deviceOS = Build.VERSION.RELEASE;
    public int deviceAPI = Build.VERSION.SDK_INT;
    private boolean mTempType = false;
    private Intent tempIntent;
    private Switch mSwitch;
    private CpuInfo CT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        //floating button right side
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Scanning Device", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                updateTV();

            }
        });
    } // end onCreate()

    // Initial Activity Set-Up
    private void init() {

        tvTemperature = (TextView) findViewById(R.id.tvTemperature);
        tvDNumCore = (TextView) findViewById(R.id.tvDNumCores);
        tvTempType = (TextView) findViewById(R.id.tempType);
        tvChipSet = (TextView) findViewById(R.id.tvChipSet);
        tvBatTemp = (TextView) findViewById(R.id.tvBatTemp);
        tvDAPI = (TextView) findViewById(R.id.tvDAPI);
        tvCPUF = (TextView) findViewById(R.id.tvCPUF);
        tvDOS = (TextView) findViewById(R.id.tvDOS);
        CT = new CpuInfo();

        // Initialize Temperature Type Switch
        mSwitch = (Switch) findViewById(R.id.sTempType);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mTempType = b;

                if (b) {
                    // fahrenheit
                    tvTempType.setText("\u00B0F");
                } else {
                    // celsius
                    tvTempType.setText("\u00B0C");
                }
            }
        });

        // Initial Placeholder Text
        tvDOS.setText("OS: " + deviceOS);
        tvDAPI.setText("API: " + deviceAPI);
        tvDNumCore.setText("Num Cores: " + getNumCores());

        // Begin Temperature Service "TempService.java"
        tempIntent = new Intent(this, TempService.class);
    } // end init()


    // Broadcast Receiver Initialization
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateTemp(intent);
        }
    };


    @Override
    public void onResume() {
        // Re-register receiver when Activity resumes to save battery life
        super.onResume();
        startService(tempIntent);
        registerReceiver(broadcastReceiver, new IntentFilter(TempService.BROADCAST_ACTION));
    }

    @Override
    public void onPause() {
        // Unregister when this Activity Pauses to save battery life
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        stopService(tempIntent);
    }

    // Updates UI temperature text values
    private void updateTemp(Intent intent) {
        int value = intent.getIntExtra("temperature", 0);
        if (mTempType) {
            // fahrenheit
            value = (int) (value * 1.8) + 32;
        }
        tvTemperature.setText(value + " ");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Updates UI by fetching necessary values from CpuInfo.java class
    private void updateTV() {
        tvChipSet.setText(getInfo());
        CT.printCPUFreq(tvCPUF);
    }

    // Fetch Basic CPU Information
    private String getInfo() {
        StringBuffer sb = new StringBuffer();

        sb.append("abi: ").append(Build.CPU_ABI).append("n");
        if (new File("/proc/cpuinfo").exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File("/proc/cpuinfo")));
                String aLine;
                while ((aLine = br.readLine()) != null) {
                    sb.append(aLine + "n");
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    } // end getInfo()


    // Check Current Device SDK Level and fetch number of CPU Cores
    // by calling appropriate functions
    private int getNumCores() {
        if (Build.VERSION.SDK_INT >= 17) {
            return Runtime.getRuntime().availableProcessors();
        } else {
            return getNumCoresOld();
        }
    } // end getNumCores()


    // Helper Function for getNumCores() function
    // Fetch Number of CPU Cores as documented by manufacturer
    // Returns 1 if failed to get result
    private int getNumCoresOld() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches("cpu[0-9]+", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            //Default to return 1 core
            return 1;
        }
    } // end getNumCoresOld()
}
