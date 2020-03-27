package com.fairphone.checkup.tests.speaker;

import com.fairphone.checkup.R;
import com.fairphone.checkup.tests.SimpleTest;

import androidx.fragment.app.Fragment;

public class LoudSpeakerTest extends SpeakerTest {

    public static final SimpleDetails DETAILS = new SimpleTest.SimpleDetails(R.string.loud_speaker_test_title, R.string.loud_speaker_test_summary, R.string.loud_speaker_test_description, R.string.loud_speaker_test_instructions) {
        @Override
        public Fragment getFragment() {
            return new LoudSpeakerTest();
        }
    };

    public LoudSpeakerTest() {
        super(R.raw.sunbeam, SPEAKER_LOUD, 0.5f);
    }

    @Override
    protected SimpleDetails getDetails() {
        return DETAILS;
    }

}
