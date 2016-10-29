package com.example.hrker.cpu_temp;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by hrker on 10/28/16.
 */

public class CalcTemp extends Activity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mTempSensor;

    private int mDeviceCores = 0;
    private String mCurFreq = "";

    public void init() {
        // init sensor managers
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mTempSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

    }

    public void printCPUFreq(TextView text) {
        // takes in text and changes text into cpu frequencies
        text.setText(Arrays.toString(getCurFrequency(getNumCores())));
    }

    public void printCPUStats(TextView text) {
        // takes in text and changes text into array printout of cpu usage
        // usage in percentages
        int[] x = getCpuUsageStatistic();
        text.setText(Arrays.toString(x));
    }

    protected void onResume() {
        super.onResume();
//        mSensorManager.registerListener(this, mTempSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
//        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
    }



    public static int getNumCores() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                if(Pattern.matches("cpu[0-9]+", pathname.getName())) {
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
        } catch(Exception e) {
            //Default to return 1 core
            return 1;
        }
    }

    private String[] getCurFrequency(int coreNum) {
        // changes mCurFreq
        // takes total number of cores
        // reads current scaling_cur_freq of each cores
        // returns String[] of all the frequencies
        // default cpu path "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq"
        String[] frequencyList= new String[coreNum];
        String x;

        for (int i = 0; i < coreNum; i++) {
            x = String.valueOf(i);
            try {
                RandomAccessFile reader = new RandomAccessFile("/sys/devices/system/cpu/cpu" + x
                        + "/cpufreq/scaling_cur_freq", "r");
                mCurFreq = reader.readLine();
                frequencyList[i] = mCurFreq;
                reader.close();
            } catch (IOException e) {
                messageBox("getCurFrequency: ", e.getMessage());
                mCurFreq = "Error fetching scaling_cur_freq";
            }
        }

        return frequencyList;
    }

    private void messageBox(String method, String message) {
        Log.d("EXCEPTION: " + method,  message);

        AlertDialog.Builder messageBox = new AlertDialog.Builder(this);
        messageBox.setTitle(method);
        messageBox.setMessage(message);
        messageBox.setCancelable(false);
        messageBox.setNeutralButton("OK", null);
        messageBox.show();
    }

    private int[] getCpuUsageStatistic() {

        String tempString = executeTop();

        tempString = tempString.replaceAll(",", "");
        tempString = tempString.replaceAll("User", "");
        tempString = tempString.replaceAll("System", "");
        tempString = tempString.replaceAll("IOW", "");
        tempString = tempString.replaceAll("IRQ", "");
        tempString = tempString.replaceAll("%", "");

        for (int i = 0; i < 10; i++) {
            tempString = tempString.replaceAll("  ", " ");
        }

        tempString = tempString.trim();
        String[] myString = tempString.split(" ");
        int[] cpuUsageAsInt = new int[myString.length];

        for (int i = 0; i < myString.length; i++) {
            myString[i] = myString[i].trim();
            cpuUsageAsInt[i] = Integer.parseInt(myString[i]);
        }
        return cpuUsageAsInt;
    }

    private String executeTop() {
        java.lang.Process p = null;
        BufferedReader in = null;
        String returnString = null;
        try {
            p = Runtime.getRuntime().exec("top -n 1");
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while (returnString == null || returnString.contentEquals("")) {
                returnString = in.readLine();
            }
        } catch (IOException e) {
            Log.e("executeTop", "error in getting first line of top");
            e.printStackTrace();
        } finally {
            try {
                in.close();
                p.destroy();
            } catch (IOException e) {
                Log.e("executeTop",
                        "error in closing and destroying top process");
                e.printStackTrace();
            }
        }
        return returnString;
    }
}
