package com.fairphone.diagnostics.tests.proximity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;

/**
 * Created by dirk on 16-10-15.
 */
public class ProximityTest extends Test {

    public ProximityTest(Context context) {
        super(context);
    }

    private int mSensorChangeCount = 0;
    SensorEventListener mSensorEventListener;
    SensorManager mSensorManager;
    Sensor mProximitySensor;
    View mTestView;

    private void onSensorChange(SensorEvent event) {
        mSensorChangeCount++;
        ((TextView) findViewById(R.id.proximity_sensor_state_text)).setText(event.values[0] < 0.1 ? "Triggered." : "Not Triggered.");
        Log.i("SENSOR", "Callback called");
        if (mSensorChangeCount > 5) {
            onTestSuccess();
            Log.i("SENSOR", "Proximity changed");
        }
    }

    private void replaceView() {
        mTestView = LayoutInflater.from(getContext()).inflate(R.layout.view_proximity_test, null);
        setTestView(mTestView);
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
        mSensorManager.registerListener(mSensorEventListener, mProximitySensor, 100);
    }

    @Override
    protected void runTest() {
        replaceView();
        getProximitySensor();
        setupSensorListener();
    }

    @Override
    protected void onCleanUp() {
        mSensorChangeCount = 0;
        mSensorManager.unregisterListener(mSensorEventListener);
        mSensorEventListener = null;
        super.onCleanUp();
    }

    @Override
    protected int getTestTitleID() {
        return R.string.proximity_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.proximity_test_description;
    }
}

