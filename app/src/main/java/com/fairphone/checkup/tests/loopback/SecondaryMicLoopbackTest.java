package com.fairphone.checkup.tests.loopback;

import android.Manifest;
import android.app.Fragment;

import community.fairphone.checkup.R;
import com.fairphone.checkup.tests.SimpleTest;

public class SecondaryMicLoopbackTest extends MicLoopbackTest {

    public static final SimpleDetails DETAILS = new SimpleTest.SimpleDetails(R.string.secondary_mic_loopback_test_title, R.string.secondary_mic_loopback_test_summary, R.string.secondary_mic_loopback_test_description, R.string.secondary_mic_loopback_test_instructions, R.string.mic_loopback_test_permissions_rationale, Manifest.permission.RECORD_AUDIO) {
        @Override
        public Fragment getFragment() {
            return new SecondaryMicLoopbackTest();
        }
    };

    public SecondaryMicLoopbackTest() {
        super(MIC_SECONDARY, 0.6f);
    }

    @Override
    protected SimpleDetails getDetails() {
        return DETAILS;
    }
}
