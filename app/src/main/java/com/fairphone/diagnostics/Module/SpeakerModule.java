package com.fairphone.diagnostics.Module;

import android.content.Context;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;
import com.fairphone.diagnostics.tests.speaker.RearSpeakerTest;
import com.fairphone.diagnostics.tests.usb.UsbPortTest;
import com.fairphone.diagnostics.tests.vibrationmotor.VibrationMotorTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dirk on 20-10-15.
 */
public class SpeakerModule implements Module {
    @Override
    public int getPictureResourceID() {
        return R.drawable.speaker;
    }

    @Override
    public int getDescriptionId() {
        return R.string.speaker_module_description;
    }

    @Override
    public int getModuleNameID() {
        return R.string.speaker_module_name;
    }

    public List<Test> getTestList(Context context) {
        List<Test> tests = new ArrayList<>();
        tests.add(new VibrationMotorTest(context));
        tests.add(new RearSpeakerTest(context));
        tests.add(new UsbPortTest(context));
        return tests;
    }
}
