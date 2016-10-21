package com.fairphone.diagnostics.tests.magnetometer;

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

import java.text.DecimalFormat;

public class MagnetometerTest extends Test {

    private static final String TAG = MagnetometerTest.class.getSimpleName();

    SensorEventListener mSensorEventListener;
    SensorManager mSensorManager;
    Sensor mMagnetometer;
    View mTestView;

    public MagnetometerTest(Context context) {
        super(context);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.magnetometer_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.magnetometer_test_description;
    }

    private void replaceView() {
        mTestView = LayoutInflater.from(getContext()).inflate(R.layout.view_magnetometer_test, null);
        setTestView(mTestView);
    }

    @Override
    protected void runTest() {
        replaceView();
        getMagnetometer();
        setupSensorListener();
    }

    @Override
    protected void onCleanUp() {
        mSensorManager.unregisterListener(mSensorEventListener);
        mSensorEventListener = null;
        super.onCleanUp();
    }

    private void getMagnetometer() {
        mSensorManager = (SensorManager)
                getContext().getSystemService(Context.SENSOR_SERVICE);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (mMagnetometer == null) {
            onTestFailure();
            return;
        }
    }

    private void setupSensorListener() {
        mSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    onSensorChange(event);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        mSensorManager.registerListener(mSensorEventListener, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void onSensorChange(SensorEvent event) {
        ((TextView)findViewById(R.id.x_value)).setText(format(event.values[0]));
        ((TextView)findViewById(R.id.y_value)).setText(format(event.values[1]));
        ((TextView)findViewById(R.id.z_value)).setText(format(event.values[2]));
    }

    private String format(float value) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(value);
    }
}
