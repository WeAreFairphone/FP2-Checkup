package com.fairphone.diagnostics.tests.proximity;

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
 * Created by dirk on 16-10-15.
 */
public class ProximityTest extends Test {

    private static final String TAG = ProximityTest.class.getSimpleName();

    public ProximityTest(Context context) {
        super(context);
    }

    SensorEventListener mSensorEventListener;
    SensorManager mSensorManager;
    Sensor mProximitySensor;
    View mTestView;

    @Override
    protected int getTestTitleID() {
        return R.string.proximity_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.proximity_test_description;
    }

    private void replaceView() {
        mTestView = LayoutInflater.from(getContext()).inflate(R.layout.view_proximity_test, null);
        setTestView(mTestView);
    }

    @Override
    protected void runTest() {
        replaceView();
        getProximitySensor();
        setupSensorListener();
    }

    @Override
    protected void onCleanUp() {
        mSensorManager.unregisterListener(mSensorEventListener);
        mSensorEventListener = null;
        super.onCleanUp();
    }

    private void getProximitySensor() {
        mSensorManager = (SensorManager)
                getContext().getSystemService(Context.SENSOR_SERVICE);
        mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (mProximitySensor == null) {
            onTestFailure();
            return;
        }
    }

    private void setupSensorListener() {
        mSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                    onSensorChange(event);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        mSensorManager.registerListener(mSensorEventListener, mProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void onSensorChange(SensorEvent event) {
        ((TextView) findViewById(R.id.proximity_sensor_state_text)).setText(event.values[0] < 0.1 ? "Triggered." : "Not Triggered.");
    }
}

