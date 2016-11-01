package com.example.hrker.cpu_temp;

import android.os.Build;

import static android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE;

/**
 * Created by hrker on 10/30/16.
 */

public class ScanAPI {

/**
 *
 * */
    final int x = Build.VERSION.SDK_INT;

    private void check() {
        if (x >= Build.VERSION_CODES.N) {
            /** given and above Nougat
             * able to use ScanSensors Class
             */

        }
        if (x >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // given and above Ice Cream Sandwich API 14
//            TYPE_AMBIENT_TEMPERATURE available here
        }

        else {
            /** must utilize the deprecated < API 14 temperature call
             *
             * */

        }
    }


}
