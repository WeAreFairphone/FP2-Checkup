package com.fairphone.diagnostics.tests.speaker;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;

/**
 * Created by maarten on 10-12-15.
 */
public class EarSpeakerTest extends Test {

    private static final String TAG = EarSpeakerTest.class.getSimpleName();

    View mTestView;

    private AudioManager audioManager;
    private int initVoiceCallVolume;
    private int maxVoiceCallVolume;

    private MediaPlayer mediaPlayer;

    public EarSpeakerTest(Context context) {
        super(context);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.ear_speaker_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.ear_speaker_test_description;
    }

    protected void onPrepare() {
        audioManager = (AudioManager)getContext().getSystemService(Context.AUDIO_SERVICE);
        initVoiceCallVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);              // store current volume to restore later
        maxVoiceCallVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        audioManager.setMode(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVoiceCallVolume, 0);             // fix volume to 100%
        audioManager.setSpeakerphoneOn(false);

        mediaPlayer = MediaPlayer.create(getContext(), R.raw.sunbeam);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(true);
    }

    @Override
    protected void runTest() {
        mediaPlayer.start();
        askIfSuccess(getContext().getString(R.string.ear_speaker_test_finish_question));
    }

    @Override
    protected void onCleanUp() {
        if (mediaPlayer != null) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, initVoiceCallVolume, 0);        // restore original volume
        }
        mediaPlayer.release();
        mediaPlayer = null;
        super.onCleanUp();
    }
}
