package com.fairphone.checkup.tests.microphone;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.View;

import com.fairphone.checkup.R;
import com.fairphone.checkup.tests.Test;

/**
 * Created by maarten on 19/09/16.
 */
public class PrimaryMicTest extends Test {

    private static final String TAG = PrimaryMicTest.class.getSimpleName();

    View mTestView;

    private final int AUDIORECORD_CHANNELS = 16;
    private final int AUDIOTRACK_CHANNELS = 4;
    private final int AUDIO_ENCODING = 2;
    private final int AUDIO_SAMPLE_RATE = 8000;
    private AudioManager audioManager;
    private int initVolume;
    private boolean isLoopback = false;
    private boolean isRecording = false;
    private boolean isTestActive = false;
    private AudioRecord localAudioRecord = null;
    private AudioTrack localAudioTrack = null;

    private Thread recordingThread = null;

    private BroadcastReceiver receiver;

    public PrimaryMicTest(Context context) {
        super(context);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.primary_mic_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.primary_mic_test_description;
    }

    @Override
    protected int getTestInstructionsID() {
        return R.string.primary_mic_test_instructions;
    }

    @Override
    protected void onPrepare() {
        displayInstructions();
    }

    @Override
    protected void runTest() {
        audioManager = ((AudioManager)getContext().getSystemService(Service.AUDIO_SERVICE));
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setParameters("hip_test=primary");
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);

        setupHeadphoneJackMonitor();
        startLoopback();
    }

    @Override
    protected void onCleanUp() {
        stopLoopback();
        super.onCleanUp();
    }

    private void setupHeadphoneJackMonitor() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equals(Intent.ACTION_HEADSET_PLUG)) {
                    int state = intent.getIntExtra("state", -1);
                    switch (state) {
                        case 0:
                            audioManager.setMode(AudioManager.STREAM_VOICE_CALL);
                            break;
                        case 1:
                            audioManager.setMode(AudioManager.MODE_IN_CALL);
                            break;
                    }
                }
            }
        };
        getContext().registerReceiver(receiver, filter);
    }

    private void startLoopback() {
        initVolume = this.audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        recordingThread = new Thread(new Runnable() {
            public void run() {
                //Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                int i = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIORECORD_CHANNELS, AUDIO_ENCODING);
                int j = AudioTrack.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIOTRACK_CHANNELS, AUDIO_ENCODING);
                byte[] arrayOfByte = new byte[i];
                localAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, AUDIO_SAMPLE_RATE, AUDIORECORD_CHANNELS, AUDIO_ENCODING, i);
                localAudioTrack = new AudioTrack(MediaRecorder.AudioSource.REMOTE_SUBMIX, AUDIO_SAMPLE_RATE, AUDIOTRACK_CHANNELS, AUDIO_ENCODING, j, AudioTrack.MODE_STREAM);
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
        audioManager.setParameters("hip_test=none");
        audioManager.setSpeakerphoneOn(false);
        localAudioRecord.stop();
        localAudioRecord.release();
        localAudioTrack.stop();
        localAudioTrack.release();
        try {
            Thread.sleep(100L);
            return;
        } catch (Exception localException) {
            Log.d(TAG, "Sleep exception");
        }
    }
}
