package com.fairphone.checkup.tests.speaker;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.fairphone.checkup.tests.SimpleTest;

/**
 * A {@link SimpleTest} dedicated to the device's speakers.
 */
public abstract class SpeakerTest extends SimpleTest {

    protected static final int SPEAKER_EAR = 0;
    protected static final int SPEAKER_LOUD = 1;

    protected AudioManager mAudioManager;
    protected MediaPlayer mMediaPlayer;

    private int mAudioSessionId;
    private int mOldStreamVolume;
    private int mLocalStreamVolume;

    private final int mRawMediaId;
    private final float mMaxVolumeRatio;
    private final int mStreamType;
    private final int mAudioMode;
    private final int mUsage;
    private final int mContentType;

    /**
     * @param rawMediaId     The raw media resource id to play through the speaker.
     * @param speaker        The speaker to use: {@link #SPEAKER_EAR} or {@link #SPEAKER_LOUD}.
     * @param maxVolumeRatio The ratio to apply to the maximum stream volume.
     */
    protected SpeakerTest(int rawMediaId, int speaker, float maxVolumeRatio) {
        super(true);

        mRawMediaId = rawMediaId;
        mMaxVolumeRatio = maxVolumeRatio;

        switch (speaker) {
            case SPEAKER_EAR:
                mStreamType = AudioManager.STREAM_VOICE_CALL;
                mAudioMode = AudioManager.MODE_IN_COMMUNICATION;
                mUsage = AudioAttributes.USAGE_VOICE_COMMUNICATION;
                mContentType = AudioAttributes.CONTENT_TYPE_SPEECH;
                break;
            default:
                Log.e(TAG, "Unknown speaker, falling back to the loud speaker.");
                // fall-through to the next case
            case SPEAKER_LOUD:
                mStreamType = AudioManager.STREAM_MUSIC;
                mAudioMode = AudioManager.MODE_RINGTONE;
                mUsage = AudioAttributes.USAGE_MEDIA;
                mContentType = AudioAttributes.CONTENT_TYPE_MUSIC;
                break;
        }
    }

    @Override
    protected void onCreateTest() {
        super.onCreateTest();

        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        mAudioSessionId = mAudioManager.generateAudioSessionId();

        if (mAudioSessionId == AudioManager.ERROR) {
            Log.e(TAG, "Audio session id was not generated");

            // TODO graceful failure
            cancelTest();
            return;
        }

        mMediaPlayer = MediaPlayer.create(
                getActivity(),
                mRawMediaId,
                new AudioAttributes.Builder()
                        .setLegacyStreamType(mStreamType)
                        .setUsage(mUsage)
                        .setContentType(mContentType)
                        .build(),
                mAudioSessionId);

        if (mMediaPlayer == null) {
            Log.e(TAG, "MediaPlayer instance was not created");

            // TODO graceful failure
            cancelTest();
            return;
        }

        mMediaPlayer.setLooping(true);

        final int maxStreamVolume = mAudioManager.getStreamMaxVolume(mStreamType);
        mLocalStreamVolume = Math.min(Math.round(maxStreamVolume * mMaxVolumeRatio), maxStreamVolume);
    }

    @Override
    protected void onResumeTest(boolean firstResume) {
        super.onResumeTest(firstResume);

        if (mMediaPlayer == null) {
            Log.e(TAG, "Missing MediaPlayer instance");
            return;
        }

        mOldStreamVolume = mAudioManager.getStreamVolume(mStreamType);

        mAudioManager.setMode(mAudioMode);
        mAudioManager.setStreamVolume(mStreamType, mLocalStreamVolume, 0);
        mAudioManager.setStreamSolo(mStreamType, true);

        mMediaPlayer.start();
    }

    @Override
    protected void onPauseTest() {
        super.onPauseTest();

        if (mMediaPlayer == null) {
            Log.e(TAG, "Missing MediaPlayer instance");
            return;
        }

        mMediaPlayer.pause();

        mAudioManager.setStreamSolo(mStreamType, false);
        mAudioManager.setStreamVolume(mStreamType, mOldStreamVolume, 0);
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
