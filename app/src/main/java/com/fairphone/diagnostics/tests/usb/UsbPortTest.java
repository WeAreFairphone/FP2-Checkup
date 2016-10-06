package com.fairphone.diagnostics.tests.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;

/**
 * Created by maarten on 19/09/16.
 */
public class UsbPortTest extends Test {

    private static final String TAG = UsbPortTest.class.getSimpleName();

    View mTestView;
    private BroadcastReceiver receiver;

    public UsbPortTest(Context context) {
        super(context);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.usb_port_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.usb_port_test_description;
    }

    private void replaceView() {
        mTestView = LayoutInflater.from(getContext()).inflate(R.layout.view_usb_port_test, null);
        setTestView(mTestView);
    }

    @Override
    protected void runTest() {
        replaceView();
        registerConnectionMonitor();
    }

    @Override
    protected void onCleanUp() {
        getContext().unregisterReceiver(receiver);
        receiver = null;
        super.onCleanUp();
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
                    ((TextView) findViewById(R.id.usb_port_state_text)).setText(getResources().getString(R.string.usb_port_plugged));
                } else if(action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
                    ((TextView) findViewById(R.id.usb_port_state_text)).setText(getResources().getString(R.string.usb_port_unplugged));
                }
            }
        };
        getContext().registerReceiver(receiver, filter);
    }
}
