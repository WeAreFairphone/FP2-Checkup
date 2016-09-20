package com.fairphone.diagnostics.tests.microphone;


import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;

/**
 * Created by maarten on 19/09/16.
 */
public class MicrophoneTest extends Test {

    private static final String TAG = MicrophoneTest.class.getSimpleName();

    View mTestView;

    private static String testCaseResult = "Not Tested";
    private static String testCaseStr = "Audio MIC Loopback Test - ";
    private final int AUDIORECORD_CHANNELS = 16;
    private final int AUDIOTRACK_CHANNELS = 4;
    private final int AUDIO_ENCODING = 2;
    private final int AUDIO_SAMPLE_RATE = 8000;
    private AudioManager audioManager;
    private Button button_mic_loopback;
    final Context context = this.getContext();
    private int initVolume;
    private boolean isLoopback = false;
    private boolean isRecording = false;
    private boolean isTestActive = false;
    private AudioRecord localAudioRecord = null;
    private AudioTrack localAudioTrack = null;

    private Thread recordingThread = null;

    public MicrophoneTest(Context context) {
        super(context);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.microphone_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.microphone_test_description;
    }

    @Override
    protected void onPrepare() {
        audioManager = ((AudioManager)getContext().getSystemService(Service.AUDIO_SERVICE));
        audioManager.setMode(AudioManager.MODE_NORMAL);
    }

    @Override
    protected void runTest() {
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
        startLoopback();

        askIfSuccess(getContext().getString(R.string.microphone_test_finish_question));
    }

    @Override
    protected void onCleanUp() {
        stopLoopback();
        super.onCleanUp();
    }

    private void startLoopback() {
        initVolume = this.audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        recordingThread = new Thread(new Runnable() {
            public void run() {
                //Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                int i = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIORECORD_CHANNELS, AUDIO_ENCODING);
                int j = AudioTrack.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIOTRACK_CHANNELS, AUDIO_ENCODING);
                byte[] arrayOfByte = new byte[i];
                localAudioRecord = new AudioRecord(1, AUDIO_SAMPLE_RATE, AUDIORECORD_CHANNELS, AUDIO_ENCODING, i);
                localAudioTrack = new AudioTrack(8, AUDIO_SAMPLE_RATE, AUDIOTRACK_CHANNELS, AUDIO_ENCODING, j, 1);
                localAudioTrack.setPlaybackRate(AUDIO_SAMPLE_RATE);
                localAudioRecord.startRecording();
                isRecording = true;
                isLoopback = true;
                try {
                    localAudioTrack.play();
                    while (isRecording) {
                        localAudioRecord.read(arrayOfByte, 0, i);
                        localAudioTrack.write(arrayOfByte, 0, i);
                    }
                    return;
                } catch (Throwable localThrowable) {
                    localAudioRecord.stop();
                    localAudioRecord.release();
                    localAudioTrack.stop();
                    localAudioTrack.release();
                }
            }
        });
        recordingThread.start();
    }

    private void stopLoopback() {
        if (audioManager == null) {
            return;
        }
        audioManager.setStreamVolume(0, initVolume, 0);
        isRecording = false;
        isLoopback = false;
        audioManager.setSpeakerphoneOn(false);
        localAudioRecord.stop();
        localAudioRecord.release();
        localAudioTrack.stop();
        localAudioTrack.release();
        try {
            Thread.sleep(100L);
            return;
        } catch (Exception localException) {
            Log.d(TAG, "sleep exceptions...");
        }
    }
}