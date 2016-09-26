package com.fairphone.diagnostics.tests.gyroscope;


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

public class GyroscopeTest extends Test implements SensorEventListener {

    private static final String TAG = GyroscopeTest.class.getSimpleName();

    View mTestView;

    private SensorManager mSensorManager;
    private Sensor mGyroscope;

    private long lastupdate = 0L;

    TextView xAxis;
    TextView yAxis;
    TextView zAxis;

    public GyroscopeTest(Context context) {
        super(context);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.gyroscope_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.gyroscope_test_description;
    }

    private void replaceView() {
        mTestView = LayoutInflater.from(getContext()).inflate(R.layout.view_gyroscope_test, null);
        setTestView(mTestView);
    }

    @Override
    protected void onPrepare() {

    }


    @Override
    protected void runTest() {
        replaceView();

        this.xAxis = ((TextView)findViewById(R.id.xAxis));
        this.yAxis = ((TextView)findViewById(R.id.yAxis));
        this.zAxis = ((TextView)findViewById(R.id.zAxis));

        mSensorManager = (SensorManager)getContext().getSystemService(Context.SENSOR_SERVICE);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        //askIfSuccess(getContext().getString(R.string.vibrator_test_finish_question));
    }

    @Override
    protected void onCleanUp() {
        mSensorManager.unregisterListener(this);
        super.onCleanUp();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long l = System.currentTimeMillis();
        if (l - this.lastupdate > 100L) {
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                xAxis.setText("X-axis: " + event.values[0]);
                yAxis.setText("Y-axis: " + event.values[1]);
                zAxis.setText("Z-axis: " + event.values[2]);
            }
            this.lastupdate = l;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
