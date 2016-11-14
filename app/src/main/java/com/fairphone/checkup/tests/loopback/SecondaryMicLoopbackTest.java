package com.fairphone.checkup.tests.loopback;

import android.app.Fragment;

import com.fairphone.checkup.R;

public class SecondaryMicLoopbackTest extends MicLoopbackTest {

    public static final Details DETAILS = new Details(R.string.secondary_mic_loopback_test_title, R.string.secondary_mic_loopback_test_summary, R.string.secondary_mic_loopback_test_description, R.string.secondary_mic_loopback_test_instructions) {
        @Override
        public Fragment getFragment() {
            return new SecondaryMicLoopbackTest();
        }
    };

    public SecondaryMicLoopbackTest() {
        super(MIC_SECUNDARY, 0.6f);
    }

    @Override
    protected Details getDetails() {
        return DETAILS;
    }
}
