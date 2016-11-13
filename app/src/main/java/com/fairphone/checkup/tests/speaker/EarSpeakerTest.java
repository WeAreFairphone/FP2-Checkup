package com.fairphone.checkup.tests.speaker;

import android.app.Fragment;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.fairphone.checkup.R;
import com.fairphone.checkup.tests.NewTest;
import com.fairphone.checkup.tests.SimpleTest;

public class EarSpeakerTest extends SpeakerTest {

    public static final Details DETAILS = new NewTest.Details(R.string.ear_speaker_test_title, R.string.ear_speaker_test_summary, R.string.ear_speaker_test_description, R.string.ear_speaker_test_instructions) {
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
