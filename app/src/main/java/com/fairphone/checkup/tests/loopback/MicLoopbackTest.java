package com.fairphone.checkup.tests.loopback;


import android.app.Service;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

import com.fairphone.checkup.tests.SimpleTest;

public abstract class MicLoopbackTest extends SimpleTest {

    public static class AudioParameters {
        public final String mDefaultMic;
        public final String mPrimaryMicOnly;
        public final String mSecondaryMicOnly;

        public AudioParameters(String defaultMic, String primaryMicOnly, String secondaryMicOnly) {
            mDefaultMic = defaultMic;
            mPrimaryMicOnly = primaryMicOnly;
            mSecondaryMicOnly = secondaryMicOnly;
        }
    }

    protected static final int MIC_PRIMARY = 0;
    protected static final int MIC_SECONDARY = 1;

    private static final int AUDIORECORD_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIOTRACK_CHANNELS = AudioFormat.CHANNEL_OUT_MONO;
    private static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int AUDIO_SAMPLE_RATE = 8000;
    private static final AudioParameters AUDIO_PARAMETERS_L;
    private static final AudioParameters AUDIO_PARAMETERS_M;

    static {
        AUDIO_PARAMETERS_L = new AudioParameters("hip_test=none", "hip_test=primary", "hip_test=secondary");
        AUDIO_PARAMETERS_M = new AudioParameters("fp_test=", "fp_test=primary_mic", "fp_test=secondary_mic");
    }

    private AudioManager mAudioManager;
    private AudioParameters mAudioParameters;
    private Handler mHandler;

    private Thread mRecordingThread;
    private AudioRecord mLocalAudioRecord;
    private AudioTrack mLocalAudioTrack;

    private boolean mIsLoopingBack = false;
    private boolean mOldIsMicrophoneMute;
    private int mOldStreamVolume;
    private int mLocalStreamVolume;

    private final float mMaxVolumeRatio;
    private final int mStreamType;
    private final int mAudioMode;
    private final String mCurrentMicOnly;

    /**
     * @param microphone     The microphone to use: {@link #MIC_PRIMARY} or {@link #MIC_SECONDARY}.
     * @param maxVolumeRatio The ratio to apply to the maximum stream volume.
     */
    protected MicLoopbackTest(int microphone, float maxVolumeRatio) {
        super(true);

        switch (android.os.Build.VERSION.SDK_INT) {
            case android.os.Build.VERSION_CODES.M:
                mAudioParameters = AUDIO_PARAMETERS_M;
                break;
            default:
                mAudioParameters = AUDIO_PARAMETERS_L;
                break;
        }

        mMaxVolumeRatio = maxVolumeRatio;

        switch (microphone) {
            default:
                Log.e(TAG, "Unknown microphone, falling back to the primary microphone.");
                // fall-through to the next case
            case MIC_PRIMARY:
                mStreamType = AudioManager.STREAM_VOICE_CALL;
                mAudioMode = AudioManager.MODE_IN_COMMUNICATION;
                mCurrentMicOnly = mAudioParameters.mPrimaryMicOnly;
                break;
            case MIC_SECONDARY:
                mStreamType = AudioManager.STREAM_MUSIC;
                mAudioMode = AudioManager.MODE_RINGTONE;
                mCurrentMicOnly = mAudioParameters.mSecondaryMicOnly;
                break;
        }
    }

    @Override
    protected void onCreateTest() {
        super.onCreateTest();

        mAudioManager = (AudioManager) getActivity().getSystemService(Service.AUDIO_SERVICE);
        mHandler = new Handler();

        final int maxStreamVolume = mAudioManager.getStreamMaxVolume(mStreamType);
        mLocalStreamVolume = Math.min(Math.round(maxStreamVolume * mMaxVolumeRatio), maxStreamVolume);
    }

    @Override
    protected void onResumeTest(boolean firstResume) {
        super.onResumeTest(firstResume);

        mOldStreamVolume = mAudioManager.getStreamVolume(mStreamType);
        mOldIsMicrophoneMute = mAudioManager.isMicrophoneMute();

        mAudioManager.setMode(mAudioMode);
        mAudioManager.setMicrophoneMute(false);
        mAudioManager.setStreamVolume(mStreamType, mLocalStreamVolume, 0);
        mAudioManager.setStreamSolo(mStreamType, true);
        mAudioManager.setParameters(mCurrentMicOnly);

        startLoopback();
    }

    @Override
    protected void onPauseTest() {
        super.onPauseTest();

        stopLoopback();

        mAudioManager.setParameters(mAudioParameters.mDefaultMic);
        mAudioManager.setStreamSolo(mStreamType, false);
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, mOldStreamVolume, 0);
        mAudioManager.setMicrophoneMute(mOldIsMicrophoneMute);
        mAudioManager.setMode(AudioManager.MODE_NORMAL);
    }

    @Override
    protected void onFinishTest() {
        super.onFinishTest();

        mAudioManager = null;
    }

    private void startLoopback() {
        mRecordingThread = new Thread(new Runnable() {
            public void run() {
                //Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                int i = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIORECORD_CHANNELS, AUDIO_ENCODING);
                int j = AudioTrack.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIOTRACK_CHANNELS, AUDIO_ENCODING);
                byte[] arrayOfByte = new byte[i];

                mLocalAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, AUDIO_SAMPLE_RATE, AUDIORECORD_CHANNELS, AUDIO_ENCODING, i);
                mLocalAudioTrack = new AudioTrack(mStreamType, AUDIO_SAMPLE_RATE, AUDIOTRACK_CHANNELS, AUDIO_ENCODING, j, AudioTrack.MODE_STREAM);

                mLocalAudioTrack.setPlaybackRate(AUDIO_SAMPLE_RATE);
                mLocalAudioRecord.startRecording();

                mIsLoopingBack = true;

                try {
                    mLocalAudioTrack.play();

                    while (mIsLoopingBack) {
                        mLocalAudioRecord.read(arrayOfByte, 0, i);
                        mLocalAudioTrack.write(arrayOfByte, 0, i);
                    }

                    // avoid jumping to the finally statement
                    return;
                } catch (Throwable localThrowable) {
                    Log.e(TAG, "Error while looping back", localThrowable);
                } finally {
                    // TODO graceful failure
                    // Final handling after an exception occurred: cancel the test
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            cancelTest();
                        }
                    });
                }
            }
        });
        mRecordingThread.start();
    }

    private void stopLoopback() {
        mIsLoopingBack = false;

        // Wait some time for the loopback thread to terminate
        try {
            Thread.sleep(100L);
            mRecordingThread.join();
        } catch (InterruptedException e) {
            Log.d(TAG, "Sleep/join exception", e);
        }

        if (mLocalAudioRecord != null) {
            mLocalAudioRecord.stop();
            mLocalAudioRecord.release();
            mLocalAudioRecord = null;
        }

        if (mLocalAudioTrack != null) {
            mLocalAudioTrack.stop();
            mLocalAudioTrack.release();
            mLocalAudioTrack = null;
        }
    }
}
