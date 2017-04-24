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

    /**
     * Low volume ratio to apply to the maximum stream volume when ducking playback.
     */
    private static final float LOW_VOLUME_RATIO = 0.2f;

    private final AudioManager.OnAudioFocusChangeListener mAFChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    onCancelTest();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    mLocalStreamVolume = mAudioManager.getStreamVolume(mStreamType);
                    onPauseTest();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    mLocalStreamVolume = mAudioManager.getStreamVolume(mStreamType);
                    mAudioManager.setStreamVolume(mStreamType, mLowVolume, 0);
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    mAudioManager.setStreamVolume(mStreamType, mLocalStreamVolume, 0);
                    if (isPaused()) {
                        onResume();
                    }
                    break;
            }
        }
    };

    protected AudioManager mAudioManager;
    protected MediaPlayer mMediaPlayer;

    private int mAudioSessionId;
    private int mOldStreamVolume;
    private int mLocalStreamVolume;
    private int mLowVolume;

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
        mLowVolume = Math.max(0, Math.round(maxStreamVolume * LOW_VOLUME_RATIO));
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
        getActivity().setVolumeControlStream(mStreamType);

        if (mAudioManager.requestAudioFocus(mAFChangeListener, mStreamType, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // Oops, there is somebody playing something else but they do not want to share

            // TODO warn the user that they are going to hear what is already playing
        } else {
            mMediaPlayer.start();
        }
    }

    @Override
    protected void onPauseTest() {
        super.onPauseTest();

        if (mMediaPlayer == null) {
            Log.e(TAG, "Missing MediaPlayer instance");
            return;
        }

        mMediaPlayer.pause();

        mAudioManager.abandonAudioFocus(mAFChangeListener);
        getActivity().setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
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
