package com.fairphone.checkup.tests.vibrator;


import android.app.Fragment;
import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;

import com.fairphone.checkup.R;
import com.fairphone.checkup.tests.Test;
import com.fairphone.checkup.tests.SimpleTest;

public class VibratorTest extends SimpleTest {

    private static final long VIBRATION_DURATION_MS = 2000;

    public static final Details DETAILS = new Test.Details(R.string.vibrator_test_title, R.string.vibrator_test_summary, R.string.vibrator_test_description, R.string.vibrator_test_instructions) {
        @Override
        public Fragment getFragment() {
            return new VibratorTest();
        }

        @Override
        public String getInstructions(Context context) {
            return String.format(context.getString(mInstructionsId), Math.round(VIBRATION_DURATION_MS / 1000));
        }
    };

    private Handler mHandler;
    private Vibrator mVibrator;
    private Thread mVibratingWaiter;

    public VibratorTest() {
        super(true);

        mHandler = new Handler();
    }

    @Override
    protected Details getDetails() {
        return DETAILS;
    }

    @Override
    protected void onCreateTest() {
        super.onCreateTest();

        mVibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        if (!mVibrator.hasVibrator()) {
            // TODO abort test
            cancelTest();

            Log.e(TAG, "Could not retrieve an instance of the vibration motor.");
        }
    }

    @Override
    protected void onResumeTest(boolean firstResume) {
        super.onResumeTest(firstResume);

        mVibrator.vibrate(VIBRATION_DURATION_MS);

        mVibratingWaiter = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(VIBRATION_DURATION_MS);
                } catch (InterruptedException e) {
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        finishTest(true);
                    }
                });
            }
        });
        mVibratingWaiter.start();
    }

    @Override
    protected void onPauseTest() {
        super.onPauseTest();

        if (mVibrator != null) {
            mVibrator.cancel();

            if (mVibratingWaiter != null) {
                mVibratingWaiter.interrupt();
                mVibratingWaiter = null;
            }
        }
    }

    @Override
    protected void onCancelTest() {
        super.onCancelTest();

        mVibrator = null;
    }
}
