package com.example.hrker.cpu_temp;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.HardwarePropertiesManager;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by hrker on 10/30/16.
 */

public class ScanSensors extends Activity implements SensorEventListener {


    /**
    *   This class requires API 24+ Nougat
    *   to be able to function
    */
    private SensorManager mSensorManager;
    private HardwarePropertiesManager mHardwareManager;
    private List<Sensor> mDeviceSensors;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        mHardwareManager.getDeviceTemperatures(HardwarePropertiesManager.DEVICE_TEMPERATURE_CPU,HardwarePropertiesManager.TEMPERATURE_CURRENT);


        mDeviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        if (mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null){
            // Success! There's an ambient thermometer.
        }
        else {
            // Failure! No magnetometer.
        }
    }

    public void printSensorList (TextView tv) {
        tv.setText("Device Sensors: " + mDeviceSensors);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        float lux = event.values[0];
        // Do something with this sensor value.
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
