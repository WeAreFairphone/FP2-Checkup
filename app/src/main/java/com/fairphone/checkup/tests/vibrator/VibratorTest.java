package com.fairphone.checkup.tests.vibrator;


import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;

import com.fairphone.checkup.R;
import com.fairphone.checkup.tests.Test;

public class VibratorTest extends Test {

    private static final String TAG = VibratorTest.class.getSimpleName();

    private static final long VIBRATION_DURATION_MS = 2000;

    Handler mHandler;
    Vibrator mVibrator;

    public VibratorTest(Context context) {
        super(context);

        mHandler = new Handler();
    }

    @Override
    protected int getTestTitleID() {
        return R.string.vibrator_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.vibrator_test_description;
    }

    @Override
    protected String getTestInstructions(Context context) {
        return String.format(context.getString(getTestInstructionsID()), Math.round(VIBRATION_DURATION_MS / 1000));
    }

    @Override
    protected int getTestInstructionsID() {
        return R.string.vibrator_test_instructions;
    }


    @Override
    protected void runTest() {
        getVibrator();
        mVibrator.vibrate(VIBRATION_DURATION_MS);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(VIBRATION_DURATION_MS);
                } catch (InterruptedException e) {
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onTestSuccess();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onPrepare() {
        displayInstructions();
    }

    @Override
    protected void onCleanUp() {
        mVibrator.cancel();
        super.onCleanUp();
    }

    private void getVibrator() {
        mVibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (!mVibrator.hasVibrator()) {
            onTestFailure();
        }
    }
}
