package com.fairphone.diagnostics.tests.vibrator;


import android.content.Context;
import android.os.Vibrator;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;

public class VibratorTest extends Test {

    private static final String TAG = VibratorTest.class.getSimpleName();

    Vibrator mVibrator;

    public VibratorTest(Context context) {
        super(context);
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
    protected void runTest() {
        getVibrator();
        mVibrator.vibrate(new long[]{128, 256, 512, 1024, 512, 256, 128}, 1);
    }

    @Override
    protected void onCleanUp() {
        mVibrator.cancel();
        super.onCleanUp();
    }

    private void getVibrator() {
        mVibrator = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if(!mVibrator.hasVibrator()) {
            onTestFailure();
        }
    }
}
