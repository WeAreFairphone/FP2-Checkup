package com.fairphone.checkup.tests.loopback;

import android.app.Fragment;

import com.fairphone.checkup.R;
import com.fairphone.checkup.tests.Test;

public class PrimaryMicLoopbackTest extends MicLoopbackTest {

    public static final Details DETAILS = new Test.Details(R.string.primary_mic_loopback_test_title, R.string.primary_mic_loopback_test_summary, R.string.primary_mic_loopback_test_description, R.string.primary_mic_loopback_test_instructions) {
        @Override
        public Fragment getFragment() {
            return new PrimaryMicLoopbackTest();
        }
    };

    public PrimaryMicLoopbackTest() {
        super(MIC_PRIMARY, 0.5f);
    }

    @Override
    protected Details getDetails() {
        return DETAILS;
    }
}
