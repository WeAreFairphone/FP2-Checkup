package com.fairphone.checkup.tests.ambientlight;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fairphone.checkup.R;
import com.fairphone.checkup.tests.Test;

/**
 * Created by dirk on 19-10-15.
 */
public class AmbientLightTest extends Test {

    private static final String TAG = AmbientLightTest.class.getSimpleName();

    SensorEventListener mSensorEventListener;
    SensorManager mSensorManager;
    Sensor mAmbientLightSensor;
    View mTestView;

    public AmbientLightTest(Context context) {
        super(context);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.ambient_light_sensor_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.ambient_light_sensor_test_description;
    }

    private void replaceView() {
        mTestView = LayoutInflater.from(getContext()).inflate(R.layout.view_ambientlight_test, null);
        setTestView(mTestView);
    }

    @Override
    protected void runTest() {
        replaceView();
        getLightSensor();
        setupSensorListener();
    }

    @Override
    protected void onCleanUp() {
        mSensorManager.unregisterListener(mSensorEventListener);
        mSensorEventListener = null;
        super.onCleanUp();
    }

    private void getLightSensor() {
        mSensorManager = (SensorManager)
                getContext().getSystemService(Context.SENSOR_SERVICE);
        mAmbientLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (mAmbientLightSensor == null) {
            onTestFailure();
            return;
        }
    }

    private void setupSensorListener() {
        mSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                    onSensorChange(event);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        mSensorManager.registerListener(mSensorEventListener, mAmbientLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void onSensorChange(SensorEvent event) {
        ((TextView) findViewById(R.id.ambient_light_state_text)).setText(event.values[0] + " " + getResources().getString(R.string.illuminance_unit));
    }
}
