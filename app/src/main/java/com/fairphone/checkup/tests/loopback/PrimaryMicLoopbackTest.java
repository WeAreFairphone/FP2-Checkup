package com.fairphone.checkup.tests.loopback;

import android.Manifest;

import community.fairphone.checkup.R;
import com.fairphone.checkup.tests.SimpleTest;

import androidx.fragment.app.Fragment;

public class PrimaryMicLoopbackTest extends MicLoopbackTest {

    public static final SimpleDetails DETAILS = new SimpleTest.SimpleDetails(R.string.primary_mic_loopback_test_title, R.string.primary_mic_loopback_test_summary, R.string.primary_mic_loopback_test_description, R.string.primary_mic_loopback_test_instructions, R.string.mic_loopback_test_permissions_rationale, Manifest.permission.RECORD_AUDIO) {
        @Override
        public Fragment getFragment() {
            return new PrimaryMicLoopbackTest();
        }
    };

    public PrimaryMicLoopbackTest() {
        super(MIC_PRIMARY, 0.5f);
    }

    @Override
    protected SimpleDetails getDetails() {
        return DETAILS;
    }
}
