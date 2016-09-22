package com.fairphone.diagnostics.Module;

import android.content.Context;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;
import com.fairphone.diagnostics.tests.dualsim.DualSimTest;
import com.fairphone.diagnostics.tests.microsd.MicroSDTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dirk on 20-10-15.
 */
public class TransceiverModule implements Module {
    @Override
    public int getPictureResourceID() {
        return R.drawable.core_module_render;
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
	    tests.add(new DualSimTest(context));
        tests.add(new MicroSDTest(context));
        return tests;
    }
}
