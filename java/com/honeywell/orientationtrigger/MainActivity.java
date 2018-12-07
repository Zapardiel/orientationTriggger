package com.honeywell.orientationtrigger;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.provider.CalendarContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.honeywell.orientationtrigger.sensors.Orientation;
import com.honeywell.orientationtrigger.utils.AppConstants;
import com.honeywell.orientationtrigger.utils.OrientationSensorInterface;

import org.w3c.dom.Text;

import java.security.Key;

public class MainActivity extends AppCompatActivity implements OrientationSensorInterface{

    private static String TAG = "orientationTrigger";

    private Handler handler_unTriggerScanner;
    private int unTriggerScanner_count=-1;

    TextView txtRoll;
    TextView txtPitch;
    TextView txtSpeed;
    TextView txtTimer;
    TextView txtTrigger_Pitch;
    TextView txtTrigger_Roll;
    TextView txtTrigger_Speed;
    TextView txtTrigger_Timer;

    SeekBar seekBar_Roll;
    SeekBar seekBar_Pitch;
    SeekBar seekBar_Speed;
    SeekBar seekBar_Timer;

    CheckBox chkStrict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler_unTriggerScanner= new Handler();

        txtRoll = (TextView) findViewById(R.id.textView_Roll);
        txtPitch= (TextView) findViewById(R.id.textView_Pitch);
        txtSpeed= (TextView) findViewById(R.id.textView_Speed);
        txtTimer= (TextView) findViewById(R.id.textView_Timer);

        seekBar_Pitch = (SeekBar) findViewById(R.id.seekBar_Pitch);
        seekBar_Roll =(SeekBar) findViewById(R.id.seekBar_Roll);
        seekBar_Speed = (SeekBar) findViewById(R.id.seekBar_Speed);
        seekBar_Timer = (SeekBar) findViewById(R.id.seekBar_Timer);

        chkStrict = (CheckBox) findViewById(R.id.chkStrict);

        seekBar_Roll.setMax(90);
        seekBar_Pitch.setMax(90);
        seekBar_Speed.setMax(1000);
        seekBar_Timer.setMax(10);

        txtTrigger_Pitch=(TextView) findViewById(R.id.txtTrigger_Pitch);
        txtTrigger_Roll=(TextView) findViewById(R.id.txtTrigger_Roll);
        txtTrigger_Speed=(TextView) findViewById(R.id.txtTrigger_Speed);
        txtTrigger_Timer=(TextView) findViewById(R.id.txtTrigger_Timer);

        //region "Listeners for Seekbar changes
        seekBar_Pitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtTrigger_Pitch.setText("Trigger: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar_Roll.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtTrigger_Roll.setText("Trigger: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar_Speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtTrigger_Speed.setText("Trigger: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar_Timer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtTrigger_Timer.setText("Trigger: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //endregion

        seekBar_Pitch.setProgress(50);
        seekBar_Roll.setProgress(25);
        seekBar_Speed.setProgress(450);
        seekBar_Timer.setProgress(5);

        handler_unTriggerScanner.postDelayed(unTriggerScanner, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Orientation orientationSensor = new Orientation(this.getApplicationContext(), this);

        //------Turn Orientation sensor ON-------
        // set tolerance for any directions
        orientationSensor.init(1.0, 1.0, 1.0);

        // set output speed and turn initialized sensor on
        // 0 Normal
        // 1 UI
        // 2 GAME
        // 3 FASTEST
        orientationSensor.on(3);
        //---------------------------------------


        // turn orientation sensor off
        //orientationSensor.off();

        // return true or false
        //orientationSensor.isSupport();
    }

    @Override
    public void orientation(Double AZIMUTH, Double PITCH, Double ROLL, float SPEED) {
        txtPitch.setText("Pitch: " + String.valueOf(Math.round(PITCH)));
        txtRoll.setText("Roll: " + String.valueOf(Math.round(ROLL)));
        txtSpeed.setText("Speed: " + String.valueOf(Math.round(SPEED)));

        txtPitch.setBackgroundColor(Color.WHITE);
        txtRoll.setBackgroundColor(Color.WHITE);

//        Log.d(TAG,"AZIMUT: " + String.valueOf(AZIMUTH));
//        Log.d(TAG,"PITCH:  " + String.valueOf(PITCH));
//        Log.d(TAG,"ROLL:   " + String.valueOf(ROLL));
//        Log.d(TAG,"SPEED:  " + String.valueOf(SPEED));

        if (ROLL<seekBar_Roll.getProgress() && ROLL>-seekBar_Roll.getProgress()){
            txtRoll.setBackgroundColor(Color.CYAN);
            if (PITCH>-seekBar_Pitch.getProgress() && PITCH<seekBar_Pitch.getProgress()){
                txtPitch.setBackgroundColor(Color.CYAN);
                if (SPEED > seekBar_Speed.getProgress()) {
                    SimulateScanKey(true);
                    unTriggerScanner_count=seekBar_Timer.getProgress();
                }
            }
            else{
                if (chkStrict.isChecked()) {
                    unTriggerScanner_count =0;
                    SimulateScanKey(false);
                }
            }
        }else{
            if (chkStrict.isChecked()) {
                unTriggerScanner_count=0;
                SimulateScanKey(false);
            }
        }

    }

    private Runnable unTriggerScanner = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG,"Count Antes: " + String.valueOf(unTriggerScanner_count));
            if(unTriggerScanner_count>0) {
                unTriggerScanner_count--;
                txtTrigger_Timer.setText("Trigger: " + String.valueOf(unTriggerScanner_count));
            }else if(unTriggerScanner_count==0){
                unTriggerScanner_count--;
                txtTrigger_Timer.setText("Trigger: " + seekBar_Timer.getProgress());
                SimulateScanKey(false);
            }
            Log.d(TAG,"Count Despues: " + String.valueOf(unTriggerScanner_count));
            handler_unTriggerScanner.postDelayed(unTriggerScanner, 1000);
        }
    };

    void SimulateScanKey(boolean KeyDown) {
        KeyEvent SendKeyEvent;
        Intent sendIntentDown = new Intent("com.honeywell.intent.action.SCAN_BUTTON");
        if (KeyDown) {
            SendKeyEvent = new KeyEvent(0, 0);

        } else {
            SendKeyEvent = new KeyEvent(1, 0);
        }
        sendIntentDown.putExtra("android.intent.extra.KEY_EVENT", SendKeyEvent);
        this.sendBroadcast(sendIntentDown);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(handler_unTriggerScanner!=null) handler_unTriggerScanner.removeCallbacks(unTriggerScanner);
        } catch (Exception ex) {
            Log.e(TAG, "Exception Destroying Service (Removing Handlers): " + ex.getMessage());
        }
    }
}
