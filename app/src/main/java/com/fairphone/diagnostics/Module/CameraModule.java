package com.fairphone.diagnostics.Module;

import android.content.Context;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;
import com.fairphone.diagnostics.tests.camera.CameraTest;
import com.fairphone.diagnostics.tests.camera.FlashTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dirk on 20-10-15.
 */
public class CameraModule implements Module {
    @Override
    public int getPictureResourceID() {
        return R.drawable.camera_module_render;
    }

    @Override
    public int getDescriptionId() {
        return R.string.camera_module_description;
    }

    @Override
    public int getModuleNameID() {
        return R.string.camera_module_name;
    }

    @Override
    public List<Test> getTestList(Context context) {
        List<Test> tests = new ArrayList<>();
        tests.add(new CameraTest(context));
        tests.add(new FlashTest(context));
        return tests;
    }

}
