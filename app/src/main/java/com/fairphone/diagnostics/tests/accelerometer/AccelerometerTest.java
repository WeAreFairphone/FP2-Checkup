package com.fairphone.diagnostics.tests.accelerometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;

/**
 * Created by maarten on 23-09-16.
 */
public class AccelerometerTest extends Test implements SensorEventListener {

    private static final String TAG = AccelerometerTest.class.getSimpleName();

    View mTestView;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private long lastupdate = 0L;

    public AccelerometerTest(Context context) {
        super(context);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.accelerometer_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.accelerometer_test_description;
    }

    protected void onPrepare() {

    }

    @Override
    protected void runTest() {
        replaceView();

        mSensorManager = ((SensorManager)getContext().getSystemService(Context.SENSOR_SERVICE));
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        askIfSuccess(getContext().getString(R.string.accelerometer_test_finish_question));
    }

    @Override
    protected void onCleanUp() {
        mSensorManager.unregisterListener(this);
        super.onCleanUp();
    }

    private void replaceView() {
        mTestView = LayoutInflater.from(getContext()).inflate(R.layout.view_accelerometer_test, null);
        setTestView(mTestView);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (System.currentTimeMillis() - this.lastupdate > 100L) {
                ((TextView)findViewById(R.id.tvXaxis)).setText("X: " + event.values[0]);
                ((TextView)findViewById(R.id.tvYaxis)).setText("Y: " + event.values[1]);
                ((TextView)findViewById(R.id.tvZaxis)).setText("Z: " + event.values[2]);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
