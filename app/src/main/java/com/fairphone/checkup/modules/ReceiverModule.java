package com.fairphone.checkup.modules;

import android.content.Context;

import com.fairphone.checkup.R;
import com.fairphone.checkup.tests.Test;
import com.fairphone.checkup.tests.ambientlight.AmbientLightTest;
import com.fairphone.checkup.tests.camera.FrontCameraTest;
import com.fairphone.checkup.tests.headphonejack.HeadphoneJackTest;
import com.fairphone.checkup.tests.led.LEDTest;
import com.fairphone.checkup.tests.microphone.SecondaryMicTest;
import com.fairphone.checkup.tests.proximity.ProximityTest;
import com.fairphone.checkup.tests.speaker.EarSpeakerTest;

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
        return R.string.top_module_description;
    }

    @Override
    public int getModuleNameID() {
        return R.string.top_module_name;
    }

    public List<Test> getTestList(Context context) {
        List<Test> tests = new ArrayList<>();
        tests.add(new HeadphoneJackTest(context));
        tests.add(new LEDTest(context));
        tests.add(new AmbientLightTest(context));
        tests.add(new ProximityTest(context));
        tests.add(new EarSpeakerTest(context));
        tests.add(new SecondaryMicTest(context));
        tests.add(new FrontCameraTest(context));
        return tests;
    }
}
