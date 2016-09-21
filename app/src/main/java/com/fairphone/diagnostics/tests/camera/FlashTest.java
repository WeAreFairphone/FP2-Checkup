package com.fairphone.diagnostics.tests.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;

import java.io.IOException;

/**
 * Created by maarten on 21/09/16.
 */

public class FlashTest extends Test {

    private static final String TAG = FlashTest.class.getSimpleName();

    private Camera mCamera;
    private SurfaceTexture mPreviewTexture;

    public FlashTest(Context context) {
        super(context);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.flash_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.flash_test_description;
    }

    @Override
    protected void onPrepare() {
    }

    @Override
    protected void runTest() {
        mCamera = Camera.open();
        Camera.Parameters p = mCamera.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(p);
        mPreviewTexture = new SurfaceTexture(0);
        try {
            mCamera.setPreviewTexture(mPreviewTexture);
        } catch (IOException ex) {
            Log.d(TAG, "Could not set preview texture");
        }
        mCamera.startPreview();

        askIfSuccess(getResources().getString(R.string.flash_test_finish_question));
    }

    @Override
    protected void onCleanUp() {
        mCamera.release();
        super.onCleanUp();
    }
}
