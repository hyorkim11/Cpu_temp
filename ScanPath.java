package com.example.hrker.cpu_temp;

import android.app.Activity;
import android.util.Log;

import java.io.IOException;
import java.io.RandomAccessFile;


public class ScanPath extends Activity {


    private String[] temp_urls = new String[]{
            "/sys/devices/system/cpu/cpu0/cpufreq/cpu_temp",
            "/sys/devices/system/cpu/cpu0/cpufreq/FakeShmoo_cpu_temp",
            "/sys/devices/system/cpu/cpufreq/cput_attributes/cur_temp",
            "/sys/devices/platform/tegra_tmon/temp1_input",
            "/sys/devices/platform/tegra-i2c.3/i2c-4/4-004c/temperature",
            "/sys/devices/platform/omap/omap_temp_sensor.0/temperature",
            "/sys/devices/platform/s5p-tmu/temperature",
            "/sys/devices/platform/s5p-tmu/curr_temp",
            "/sys/devices/virtual/thermal/thermal_zone0/temp",
            "/sys/devices/virtual/thermal/thermal_zone1/temp",
            "/sys/devices/virtual/K3G_GYRO-dev/k3g/gyro_get_temp",
            "/sys/kernel/debug/tegra_thermal/temp_tj",
            "/sys/class/thermal/thermal_zone0/temp",
            "/sys/class/thermal/thermal_zone1/temp",
            "/sys/class/thermal/thermal_zone2/temp",
            "/sys/class/thermal/thermal_zone3/temp",
            "/sys/class/thermal/thermal_zone4/temp",
            "/sys/class/hwmon/hwmon0/device/temp1_input",
            "/sys/class/i2c-adapter/i2c-4/4-004c/temperature",
    };


    public int startScan() {
        // takes the urls and scans them
        // returns 0 if none found
        // default value retrieved in celsius
        String x;
        int temp = 0, temp2 = 0;
        boolean foundAlready = false;

        for (int i = 0; i < temp_urls.length; i++) {

            try {
                RandomAccessFile reader = new RandomAccessFile(temp_urls[i], "r");
                x = reader.readLine();

                if (foundAlready) {
                    // if temp value already found
                    // take new one and average it with existing
                    temp2 = Integer.parseInt(x);

                    if (temp2 > 100) {
                        temp2 = (temp2 / 1000);
                    }

                    temp = (temp + temp2)/2;
                    Log.d("EXCEPTION: ", "value " + temp2 + " found at p" + i);
                    temp2 = 0;
                } else {
                    // if temp value hasn't been found yet, but found one!
                    temp = Integer.parseInt(x);
                    if (temp > 100) {
                        temp = (temp/1000);
                    }
                    foundAlready = true;
                }
                reader.close();
            } catch (IOException e) {
                Log.d("EXCEPTION: ", "file not at p"+i);
            }
        }
        // this converts into Fahrenheit
        temp = (int)(temp*1.8)+32;

        // just return the celsius value
        return temp;

    }
}
