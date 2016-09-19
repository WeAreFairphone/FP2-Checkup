package com.fairphone.diagnostics.tests.vibrator;


import android.content.Context;
import android.os.Vibrator;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;

public class VibratorTest extends Test {
    Vibrator vibratorService;

    public VibratorTest(Context context) {
        super(context);
    }

    @Override
    protected void runTest() {
        vibratorService.vibrate(new long[]{128, 256, 512, 1024, 512, 256, 128}, 1);
        askIfSuccess(getContext().getString(R.string.vibrator_test_finish_question));
    }

    @Override
    protected void onCleanUp() {
        vibratorService.cancel();
        super.onCleanUp();
    }


    @Override
    protected void onPrepare() {
        vibratorService = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.vibrator_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.vibrator_test_description;
    }
}
