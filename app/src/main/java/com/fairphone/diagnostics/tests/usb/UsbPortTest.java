package com.fairphone.diagnostics.tests.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorEvent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;

/**
 * Created by maarten on 19/09/16.
 */
public class UsbPortTest extends Test {

    View mTestView;
    private BroadcastReceiver receiver;
    private int mUsbConnectionChangeCount = 0;

    public UsbPortTest(Context context) {
        super(context);
    }

    @Override
    protected void runTest() {
        replaceView();
        registerConnectionMonitor();
    }

    private void registerConnectionMonitor() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equals(Intent.ACTION_POWER_CONNECTED)) {
                    mUsbConnectionChangeCount++;
                    ((TextView) findViewById(R.id.usb_port_state_text)).setText("Connected.");
                    Log.i("USB", "Cable connected");
                } else if(action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
                    mUsbConnectionChangeCount++;
                    ((TextView) findViewById(R.id.usb_port_state_text)).setText("Disconnected.");
                    Log.i("USB", "Cable disconnected");
                }
                if (mUsbConnectionChangeCount > 5) {
                    onTestSuccess();
                    Log.i("USB", "USB connection change");
                }
            }
        };
        getContext().registerReceiver(receiver, filter);
    }

    private void replaceView() {
        mTestView = LayoutInflater.from(getContext()).inflate(R.layout.view_usb_port_test, null);
        setTestView(mTestView);
    }

    @Override
    protected void onCleanUp() {
        mUsbConnectionChangeCount = 0;
        getContext().unregisterReceiver(receiver);
        receiver = null;
        super.onCleanUp();
    }

    @Override
    protected int getTestTitleID() {
        return R.string.usb_port_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.usb_port_test_description;
    }

    private void onConnectedChange(SensorEvent event) {
        //mSensorChangeCount++;
        ((TextView) findViewById(R.id.usb_port_state_text)).setText(isConnected(getContext()) ? "Connected." : "Not Connected.");
        Log.i("SENSOR", "Callback called");
        //if (mSensorChangeCount > 5) {
         //   onTestSuccess();
        //    Log.i("SENSOR", "Proximity changed");
        //}
    }

    public static boolean isConnected(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter("android.hardware.usb.action.USB_STATE"));
        return intent.getExtras().getBoolean("connected");
    }
}
