package com.fairphone.checkup.tests.speaker;

import android.app.Fragment;

import com.fairphone.checkup.R;

public class LoudSpeakerTest extends SpeakerTest {

    public static final Details DETAILS = new Details(R.string.loud_speaker_test_title, R.string.loud_speaker_test_summary, R.string.loud_speaker_test_description, R.string.loud_speaker_test_instructions) {
        @Override
        public Fragment getFragment() {
            return new LoudSpeakerTest();
        }
    };

    public LoudSpeakerTest() {
        super(R.raw.sunbeam, SPEAKER_LOUD, 0.5f);
    }

    @Override
    protected Details getDetails() {
        return DETAILS;
    }

}
