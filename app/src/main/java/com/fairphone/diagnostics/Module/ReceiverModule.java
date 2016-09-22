package com.fairphone.diagnostics.Module;

import android.content.Context;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;
import com.fairphone.diagnostics.tests.ambientlight.AmbientLightTest;
import com.fairphone.diagnostics.tests.camera.FrontCameraTest;
import com.fairphone.diagnostics.tests.headphonejack.HeadphoneJackTest;
import com.fairphone.diagnostics.tests.led.LEDTest;
import com.fairphone.diagnostics.tests.proximity.ProximityTest;
import com.fairphone.diagnostics.tests.speaker.EarSpeakerTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dirk on 20-10-15.
 */
public class ReceiverModule implements Module {
    @Override
    public int getPictureResourceID() {
        return R.drawable.top_module_render;
    }

    @Override
    public int getDescriptionId() {
        return R.string.receiver_module_description;
    }

    @Override
    public int getModuleNameID() {
        return R.string.receiver_module_name;
    }


    public List<Test> getTestList(Context context) {
        List<Test> tests = new ArrayList<>();
        tests.add(new HeadphoneJackTest(context));
        tests.add(new LEDTest(context));
        tests.add(new AmbientLightTest(context));
        tests.add(new ProximityTest(context));
        tests.add(new EarSpeakerTest(context));
        tests.add(new FrontCameraTest(context));
        return tests;
    }
}
