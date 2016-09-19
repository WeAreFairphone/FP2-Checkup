package com.fairphone.diagnostics.tests.camera;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;

import java.io.IOException;

/**
 * Created by dirk on 19-10-15.
 */

public class CameraTest extends Test implements SurfaceHolder.Callback {

    private static final String TAG = CameraTest.class.getSimpleName();

    View mTestView;
    Camera mCamera;
    SurfaceView mSurfaceView;
    Handler mHandler;
    int mCamNo;

    public CameraTest(Context context) {
        super(context);
        mHandler = new Handler();
    }

    public CameraTest(Context context, int camNo) {
        super(context);
        mHandler = new Handler();
        this.mCamNo = camNo;
    }

    protected void replaceView() {
        mTestView = LayoutInflater.from(getContext()).inflate(R.layout.view_camera_test, null);
        setTestView(mTestView);
    }

    @Override
    protected void runTest() {
        replaceView();
        mCamera = Camera.open(mCamNo);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        final SurfaceHolder holder = mSurfaceView.getHolder();
        holder.addCallback(this);
        holder.setType(LAYER_TYPE_HARDWARE);
        Runnable run = new Runnable() {
            @Override
            public void run() {
                Camera.Parameters localParameters = mCamera.getParameters();
                localParameters.setFlashMode("torch");
                localParameters.setAntibanding("auto");
                localParameters.setFocusMode("continuous-picture");
                mCamera.setDisplayOrientation(90);
                mCamera.setParameters(localParameters);
                try {

                    mCamera.setPreviewDisplay(holder);
                    mCamera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                    onTestFailure();
                    return;
                }
            }
        };
        mHandler.postDelayed(run, 10);
    }


    @Override
    protected void onCleanUp() {
        mCamera.release();
        super.onCleanUp();
    }

    @Override
    protected int getTestTitleID() {
        return R.string.camera_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.camera_test_description;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}


