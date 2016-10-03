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

public class AccelerometerTest extends Test {

    private static final String TAG = AccelerometerTest.class.getSimpleName();

    SensorEventListener mSensorEventListener;
    SensorManager mSensorManager;
    Sensor mAccelerometer;
    View mTestView;

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

    private void replaceView() {
        mTestView = LayoutInflater.from(getContext()).inflate(R.layout.view_accelerometer_test, null);
        setTestView(mTestView);
    }

    @Override
    protected void runTest() {
        replaceView();
        getAccelerometer();
        setupSensorListener();
    }

    @Override
    protected void onCleanUp() {
        mSensorManager.unregisterListener(mSensorEventListener);
        mSensorEventListener = null;
        super.onCleanUp();
    }

    private void getAccelerometer() {
        mSensorManager = (SensorManager)
                getContext().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (mAccelerometer == null) {
            onTestFailure();
            return;
        }
    }

    private void setupSensorListener() {
        mSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    onSensorChange(event);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        mSensorManager.registerListener(mSensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void onSensorChange(SensorEvent event) {
        ((TextView)findViewById(R.id.x_value)).setText(event.values[0] + " " + getResources().getString(R.string.acceleration_unit));
        ((TextView)findViewById(R.id.y_value)).setText(event.values[1] + " " + getResources().getString(R.string.acceleration_unit));
        ((TextView)findViewById(R.id.z_value)).setText(event.values[2] + " " + getResources().getString(R.string.acceleration_unit));
    }
}
