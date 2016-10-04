package com.fairphone.diagnostics.tests.buttons;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;

/**
 * Created by maarten on 22/09/16.
 */

public class ButtonsTest extends Test {

    private static final String TAG = ButtonsTest.class.getSimpleName();

    View mTestView;

    public ButtonsTest(Context context) {
        super(context);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.buttons_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.buttons_test_description;
    }

    protected void replaceView() {
        mTestView = LayoutInflater.from(getContext()).inflate(R.layout.view_buttons_test, null);
        setTestView(mTestView);
    }

    @Override
    protected void runTest() {
        replaceView();
        setupOnKeyListener();
    }

    @Override
    protected void onCleanUp() {

        super.onCleanUp();
    }

    private void setupOnKeyListener() {
        OnKeyListener onKeyListener = new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                int action = event.getAction();
                if(action == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_VOLUME_UP:
                            ((TextView)findViewById(R.id.button_volume_up_value)).setText(getResources().getString(R.string.buttons_pressed));
                            break;
                        case KeyEvent.KEYCODE_VOLUME_DOWN:
                            ((TextView)findViewById(R.id.button_volume_down_value)).setText(getResources().getString(R.string.buttons_pressed));
                            break;
                        case KeyEvent.KEYCODE_CAMERA:
                            Log.i(TAG, "Camera pressed");
                            ((TextView)findViewById(R.id.button_camera_value)).setText(getResources().getString(R.string.buttons_pressed));
                            break;
//                        case KeyEvent.KEYCODE_POWER:
//                            ((TextView)findViewById(R.id.button_power_value)).setText(getResources().getString(R.string.buttons_pressed));
//                            break;
                    }
                } else if (action == KeyEvent.ACTION_UP) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_VOLUME_UP:
                            ((TextView)findViewById(R.id.button_volume_up_value)).setText(getResources().getString(R.string.buttons_unpressed));
                            break;
                        case KeyEvent.KEYCODE_VOLUME_DOWN:
                            ((TextView)findViewById(R.id.button_volume_down_value)).setText(getResources().getString(R.string.buttons_unpressed));
                            break;
                        case KeyEvent.KEYCODE_CAMERA:
                            Log.i(TAG, "Camera unpressed");
                            ((TextView)findViewById(R.id.button_camera_value)).setText(getResources().getString(R.string.buttons_unpressed));
                            break;
//                        case KeyEvent.KEYCODE_POWER:
//                            ((TextView)findViewById(R.id.button_power_value)).setText(getResources().getString(R.string.buttons_unpressed));
//                            break;
                    }
                }
                return true;
            }
        };
        setFocusableInTouchMode(true);
        requestFocus();
        setOnKeyListener(onKeyListener);
    }
}
