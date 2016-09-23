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

/**
 * Created by maarten on 23/09/16.
 */

public class MagnetometerTest extends Test implements SensorEventListener {

    private static final String TAG = MagnetometerTest.class.getSimpleName();

    View mTestView;
    private TextView tvHeading;

    private SensorManager mSensorManager;
    private Sensor mMagnetometer;
    private Sensor mAccelerometer;

    private float[] mMagnetometerData;
    private float[] mAccelerometerData;
    private float[] mOrientation = new float[3];

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

    protected void onPrepare() {
        mSensorManager = ((SensorManager)getContext().getSystemService(Context.SENSOR_SERVICE));
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void runTest() {
        replaceView();
        tvHeading = (TextView)findViewById(R.id.tvHeading);

        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

        askIfSuccess(getContext().getString(R.string.magnetometer_test_finish_question));
    }

    @Override
    protected void onCleanUp() {
        mSensorManager.unregisterListener(this);
        super.onCleanUp();
    }

    private void replaceView() {
        mTestView = LayoutInflater.from(getContext()).inflate(R.layout.view_magnetometer_test, null);
        setTestView(mTestView);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAccelerometerData = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mMagnetometerData = event.values;
        }
        if ((mAccelerometerData != null) && (mMagnetometerData != null)) {
            float R[] = new float[9];
            float I[] = new float[9];
            if (SensorManager.getRotationMatrix(R, I, mAccelerometerData, mMagnetometerData)) {
                SensorManager.getOrientation(R, mOrientation);
                float azimuth = mOrientation[0] * 57.29578F;
                tvHeading.setText("Heading: " + azimuth);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
