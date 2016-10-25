package com.fairphone.checkup.modules;

import android.content.Context;

import com.fairphone.checkup.R;
import com.fairphone.checkup.tests.Test;
import com.fairphone.checkup.tests.microphone.PrimaryMicTest;
import com.fairphone.checkup.tests.speaker.RearSpeakerTest;
import com.fairphone.checkup.tests.usb.UsbPortTest;
import com.fairphone.checkup.tests.vibrator.VibratorTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dirk on 20-10-15.
 */
public class SpeakerModule implements Module {
    @Override
    public int getPictureResourceID() {
        return R.drawable.bottom_module_render;
    }

    @Override
    public int getDescriptionId() {
        return R.string.bottom_module_description;
    }

    @Override
    public int getModuleNameID() {
        return R.string.bottom_module_name;
    }

    public List<Test> getTestList(Context context) {
        List<Test> tests = new ArrayList<>();
        tests.add(new VibratorTest(context));
        tests.add(new RearSpeakerTest(context));
        tests.add(new PrimaryMicTest(context));
        tests.add(new UsbPortTest(context));
        return tests;
    }
}
