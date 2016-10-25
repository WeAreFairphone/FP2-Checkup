package com.fairphone.checkup.modules;

import android.content.Context;

import com.fairphone.checkup.R;
import com.fairphone.checkup.tests.Test;
import com.fairphone.checkup.tests.digitizer.DigitizerTest;
import com.fairphone.checkup.tests.display.DisplayTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dirk on 20-10-15.
 */
public class DisplayModule implements Module {
    @Override
    public int getPictureResourceID() {
        return R.drawable.display_module;
    }

    @Override
    public int getDescriptionId() {
        return R.string.display_module_description;
    }

    @Override
    public int getModuleNameID() {
        return R.string.display_module_name;
    }


    public List<Test> getTestList(Context context) {
        List<Test> tests = new ArrayList<>();
        tests.add(new DisplayTest(context));
        tests.add(new DigitizerTest(context));
        return tests;
    }
}
