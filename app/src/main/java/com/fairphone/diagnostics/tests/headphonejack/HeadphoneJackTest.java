package com.fairphone.diagnostics.tests.headphonejack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;

/**
 * Created by maarten on 19/09/16.
 */
public class HeadphoneJackTest extends Test {

    private static final String TAG = HeadphoneJackTest.class.getSimpleName();

    View mTestView;
    private BroadcastReceiver receiver;
    private int mHeadphoneJackChangeCount = 0;

    public HeadphoneJackTest(Context context) {
        super(context);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.headphone_jack_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.headphone_jack_test_description;
    }

    @Override
    protected void runTest() {
        replaceView();
        registerHeadphoneJackMonitor();
    }

    @Override
    protected void onCleanUp() {
        mHeadphoneJackChangeCount = 0;
        getContext().unregisterReceiver(receiver);
        receiver = null;
        super.onCleanUp();
    }

    private void replaceView() {
        mTestView = LayoutInflater.from(getContext()).inflate(R.layout.view_headphone_jack_test, null);
        setTestView(mTestView);
    }

    private void registerHeadphoneJackMonitor() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equals(Intent.ACTION_HEADSET_PLUG)) {
                    int state = intent.getIntExtra("state", -1);
                    switch (state) {
                        case 0:
                            ((TextView) findViewById(R.id.headphone_jack_state_text)).setText("Unplugged.");
                            Log.i(TAG, "Headset is unplugged");
                            break;
                        case 1:
                            ((TextView) findViewById(R.id.headphone_jack_state_text)).setText("Plugged.");
                            Log.i(TAG, "Headset is plugged");
                            break;
                        default:
                            Log.i(TAG, "I have no idea what the headset state is");
                    }
                    mHeadphoneJackChangeCount++;
                }
                if (mHeadphoneJackChangeCount > 5) {
                    onTestSuccess();
                    Log.i(TAG, "Test passed");
                }
            }
        };
        getContext().registerReceiver(receiver, filter);
    }
}
