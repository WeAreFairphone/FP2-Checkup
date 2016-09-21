package com.fairphone.diagnostics.Module;

import android.content.Context;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;

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
        return tests;
    }
}