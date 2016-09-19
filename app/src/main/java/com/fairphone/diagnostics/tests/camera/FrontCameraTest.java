package com.fairphone.diagnostics.tests.camera;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

import com.fairphone.diagnostics.R;

/**
 * Created by dirk on 20-10-15.
 */
public class FrontCameraTest extends CameraTest {

    public FrontCameraTest(Context context) {
        super(context, 1);
        View view = findViewById(R.id.surfaceView);

    }

    @Override
    protected void replaceView() {
        super.replaceView();
        View view = findViewById(R.id.surfaceView);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = (int) (250 * Resources.getSystem().getDisplayMetrics().density);
        view.setLayoutParams(params);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.front_camera_test_title;
    }
}
