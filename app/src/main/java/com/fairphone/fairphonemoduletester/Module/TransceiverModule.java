package com.fairphone.fairphonemoduletester.Module;

import android.content.Context;

import com.fairphone.fairphonemoduletester.R;
import com.fairphone.fairphonemoduletester.tests.Test;
import com.fairphone.fairphonemoduletester.tests.ambientlight.AmbientLightTest;
import com.fairphone.fairphonemoduletester.tests.camera.CameraTest;
import com.fairphone.fairphonemoduletester.tests.camera.FrontCameraTest;
import com.fairphone.fairphonemoduletester.tests.display.DisplayTest;
import com.fairphone.fairphonemoduletester.tests.proximity.ProximityTest;
import com.fairphone.fairphonemoduletester.tests.vibrator.VibratorTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dirk on 20-10-15.
 */
public class TransceiverModule implements Module {
    @Override
    public int getPictureResourceID() {
        return R.drawable.transceiver;
    }

    @Override
    public int getDescriptionId() {
        return R.string.transceiver_module_description;
    }

    @Override
    public int getModuleNameID() {
        return R.string.transceiver_module_name;
    }

    @Override
    public List<Test> getTestList(Context context) {
        ArrayList<Test> tests = new ArrayList<>();
        tests.add(new DisplayTest(context));
        tests.add(new ProximityTest(context));
        tests.add(new AmbientLightTest(context));
        tests.add(new VibratorTest(context));
        tests.add(new CameraTest(context));
        tests.add(new FrontCameraTest(context));
        return tests;
    }
}
