package com.honeywell.orientationtrigger.responseProvider;

import android.util.Log;

import com.honeywell.orientationtrigger.utils.AppConstants;
import com.honeywell.orientationtrigger.utils.OrientationSensorInterface;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * To change this template use File | Settings | File Templates.
 */
public class orientationResponseProvider {

    private ArrayList<Double> sensorValueLog = new ArrayList<Double>();
    private ArrayList<Double> tolerance = new ArrayList<Double>();
    private JSONObject response = new JSONObject();
    private OrientationSensorInterface observer;

    // Variable for Speed calculation
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private float speed=0;

    public orientationResponseProvider(OrientationSensorInterface osi){
        sensorValueLog.add(0, 0.0);
        sensorValueLog.add(1, 0.0);
        sensorValueLog.add(2, 0.0);

        tolerance.add(0, 0.0);
        tolerance.add(1, 0.0);
        tolerance.add(2, 0.0);

        this.observer = osi;
    }

    public void init(Double azimtuhTol, Double pitchTol, Double rollTol){
        tolerance.add(0, azimtuhTol);
        tolerance.add(1, pitchTol);
        tolerance.add(2, rollTol);
    }

    public void dispatcher(float[] gyroOrientation, float[] accel){

        Double azimuth = gyroOrientation[0] * 180/Math.PI;
        if ( azimuth < 0)
        	azimuth += 360;
        Double pitch = gyroOrientation[1] * 180/Math.PI;
        Double roll = gyroOrientation[2] * 180/Math.PI;

        // Calculate Speed based on Accel sensor
        ///////////////////////////////////////////////////////////////////
        float x = accel[0];
        float y = accel[1];
        float z = accel[2];

        long curTime = System.currentTimeMillis();

        if ((curTime - lastUpdate) > 100) {
            long diffTime = (curTime - lastUpdate);
            lastUpdate = curTime;

            speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

//            Log.d("orientationResponseProvider", "Last Update:  " + String.valueOf(lastUpdate));
//            Log.d("orientationResponseProvider", "Current Time: " + String.valueOf(curTime));
//            Log.d("orientationResponseProvider", "Diference T:  " + String.valueOf(diffTime));
//            Log.d("orientationResponseProvider", "X:            " + String.valueOf(x));
//            Log.d("orientationResponseProvider", "Y:            " + String.valueOf(y));
//            Log.d("orientationResponseProvider", "Z:            " + String.valueOf(z));
//            Log.d("orientationResponseProvider", "SPEED:        " + String.valueOf(speed));

            last_x = x;
            last_y = y;
            last_z = z;
        }
        //////////////////////////////////////////////////////////////////

        if ( Math.abs(sensorValueLog.get(0) - azimuth) > tolerance.get(0)
                || Math.abs(sensorValueLog.get(1) - pitch) > tolerance.get(1)
                || Math.abs(sensorValueLog.get(2) - roll) > tolerance.get(2))
        {
            sensorValueLog.set(0, azimuth);
            sensorValueLog.set(1, pitch);
            sensorValueLog.set(2, roll);

            observer.orientation(sensorValueLog.get(0), sensorValueLog.get(1), sensorValueLog.get(2),speed);
        }
    }

}
