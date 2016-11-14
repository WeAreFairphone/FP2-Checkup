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

    protected static final int MIC_PRIMARY = 0;
    protected static final int MIC_SECUNDARY = 1;

    private static final int AUDIORECORD_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIOTRACK_CHANNELS = AudioFormat.CHANNEL_OUT_MONO;
    private static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int AUDIO_SAMPLE_RATE = 8000;
    private static final String DEFAULT_AUDIO_PARAMETERS = "hip_test=none";

    private AudioManager mAudioManager;
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
    private final String mAudioParameters;

    /**
     * @param microphone     The microphone to use: {@link #MIC_PRIMARY} or {@link #MIC_SECUNDARY}.
     * @param maxVolumeRatio The ratio to apply to the maximum stream volume.
     */
    protected MicLoopbackTest(int microphone, float maxVolumeRatio) {
        super(true);

        mMaxVolumeRatio = maxVolumeRatio;

        switch (microphone) {
            default:
                Log.e(TAG, "Unknown microphone, falling back to the primary microphone.");
                // fall-through to the next case
            case MIC_PRIMARY:
                mStreamType = AudioManager.STREAM_VOICE_CALL;
                mAudioMode = AudioManager.MODE_IN_COMMUNICATION;
                mAudioParameters = "hip_test=primary";
                break;
            case MIC_SECUNDARY:
                mStreamType = AudioManager.STREAM_MUSIC;
                mAudioMode = AudioManager.MODE_RINGTONE;
                mAudioParameters = "hip_test=secondary";
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
        mAudioManager.setParameters(mAudioParameters);

        startLoopback();
    }

    @Override
    protected void onPauseTest() {
        super.onPauseTest();

        stopLoopback();

        mAudioManager.setParameters(DEFAULT_AUDIO_PARAMETERS);
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
