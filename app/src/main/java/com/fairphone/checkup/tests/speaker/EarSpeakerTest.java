package com.fairphone.checkup.tests.speaker;

import android.app.Fragment;

import com.fairphone.checkup.R;
import com.fairphone.checkup.tests.Test;

public class EarSpeakerTest extends SpeakerTest {

    public static final Details DETAILS = new Test.Details(R.string.ear_speaker_test_title, R.string.ear_speaker_test_summary, R.string.ear_speaker_test_description, R.string.ear_speaker_test_instructions) {
        @Override
        public Fragment getFragment() {
            return new EarSpeakerTest();
        }
    };

    public EarSpeakerTest() {
        super(R.raw.fiesta, SPEAKER_EAR, 0.6f);
    }

    @Override
    protected Details getDetails() {
        return DETAILS;
    }

}
