package com.fairphone.fairphonemoduletester.Module;

import android.content.Context;

import com.fairphone.fairphonemoduletester.R;
import com.fairphone.fairphonemoduletester.tests.Test;
import com.fairphone.fairphonemoduletester.tests.camera.CameraTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dirk on 20-10-15.
 */
public class CameraModule implements Module {
    @Override
    public int getPictureResourceID() {
        return R.drawable.camera;
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
        return tests;
    }

}
