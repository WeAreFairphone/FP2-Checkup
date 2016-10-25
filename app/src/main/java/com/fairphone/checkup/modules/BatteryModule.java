package com.fairphone.checkup.modules;

import android.content.Context;

import com.fairphone.checkup.R;
import com.fairphone.checkup.tests.Test;
import com.fairphone.checkup.tests.battery.BatteryTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maarten on 21/09/16.
 */
public class BatteryModule implements Module {
    @Override
    public int getPictureResourceID() {
        return R.drawable.battery_module_render;
    }

    @Override
    public int getModuleNameID() {
        return R.string.battery_module_name;
    }

    @Override
    public int getDescriptionId() {
        return R.string.battery_module_description;
    }

    public List<Test> getTestList(Context context) {
        List<Test> tests = new ArrayList<>();
        tests.add(new BatteryTest(context));
        return tests;
    }
}