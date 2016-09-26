package com.fairphone.diagnostics.tests.speaker;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;

/**
 * Created by maarten on 10-12-15.
 */
public class RearSpeakerTest extends Test {

    private static final String TAG = RearSpeakerTest.class.getSimpleName();

    private AudioManager audioManager;
    private int initMediaVolume;
    private int maxMediaVolume;

    private MediaPlayer mediaPlayer;

    public RearSpeakerTest(Context context) {
        super(context);
    }

    @Override
    protected void runTest() {
        audioManager = (AudioManager)getContext().getSystemService(Context.AUDIO_SERVICE);
        initMediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);                  // store current volume to restore later
        maxMediaVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxMediaVolume / 2, 0);             // fix volume to 50%

        mediaPlayer = MediaPlayer.create(getContext(), R.raw.fiesta);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        //askIfSuccess(getContext().getString(R.string.rear_speaker_test_finish_question));
    }

    @Override
    protected void onCleanUp() {
        mediaPlayer.stop();
        mediaPlayer.release();
        if (mediaPlayer != null) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, initMediaVolume, 0);            // restore original volume
        }
        mediaPlayer = null;
        super.onCleanUp();
    }

    @Override
    protected int getTestTitleID() {
        return R.string.rear_speaker_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.rear_speaker_test_description;
    }
}
