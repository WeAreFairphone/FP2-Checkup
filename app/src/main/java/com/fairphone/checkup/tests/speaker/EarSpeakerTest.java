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

public class EarSpeakerTest extends SimpleTest {

    private static final String TAG = EarSpeakerTest.class.getSimpleName();

    public static final Details DETAILS = new NewTest.Details(R.string.ear_speaker_test_title, R.string.ear_speaker_test_summary, R.string.ear_speaker_test_description, R.string.ear_speaker_test_instructions) {
        @Override
        public Fragment getFragment() {
            return new EarSpeakerTest();
        }
    };

    /**
     * The ratio to apply to the maximum stream volume.
     */
    private static final float MAX_STREAM_VOLUME_RATIO = 0.75f;

    private AudioManager mAudioManager;
    private MediaPlayer mMediaPlayer;

    private int mOldStreamVolume;

    public EarSpeakerTest() {
        super(true);
    }

    @Override
    protected Details getDetails() {
        return DETAILS;
    }

    @Override
    protected void onCreateTest() {
        super.onCreateTest();

        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        mMediaPlayer = MediaPlayer.create(
                getActivity(),
                R.raw.sunbeam,
                new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                        .build(),
                0);

        if (mMediaPlayer == null) {
            // TODO cleanly fail
            cancelTest();
        } else {
            mMediaPlayer.setLooping(true);
        }
    }

    @Override
    protected void onResumeTest(boolean firstResume) {
        super.onResumeTest(firstResume);

        if (mMediaPlayer == null) {
            if (!isCancelled()) {
                Log.e(TAG, "Missing MediaPlayer instance");
            }
            return;
        }

        final int maxStreamVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        final int localStreamVolume = Math.min(Math.round(maxStreamVolume * MAX_STREAM_VOLUME_RATIO), maxStreamVolume);

        mOldStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, localStreamVolume, 0);
        mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL, true);

        mMediaPlayer.start();
    }

    @Override
    protected void onPauseTest() {
        super.onPauseTest();

        if (mMediaPlayer == null) {
            if (!isCancelled()) {
                Log.e(TAG, "Missing MediaPlayer instance");
            }
            return;
        }

        mMediaPlayer.pause();

        mAudioManager.setStreamSolo(AudioManager.STREAM_VOICE_CALL, false);
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, mOldStreamVolume, 0);
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
    }

    @Override
    protected void onFinishTest() {
        super.onFinishTest();

        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
