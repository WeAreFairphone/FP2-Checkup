package com.fairphone.checkup.modules;

import android.content.Context;

import com.fairphone.checkup.R;
import com.fairphone.checkup.tests.Test;
import com.fairphone.checkup.tests.accelerometer.AccelerometerTest;
import com.fairphone.checkup.tests.buttons.ButtonsTest;
import com.fairphone.checkup.tests.dualsim.DualSimTest;
import com.fairphone.checkup.tests.gps.GPSTest;
import com.fairphone.checkup.tests.gyroscope.GyroscopeTest;
import com.fairphone.checkup.tests.magnetometer.MagnetometerTest;
import com.fairphone.checkup.tests.microsd.MicroSDTest;
import com.fairphone.checkup.tests.modem.ModemTest;
import com.fairphone.checkup.tests.wifi.WifiTest;

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
        tests.add(new ModemTest(context));
        tests.add(new WifiTest(context));
	    tests.add(new DualSimTest(context));
        tests.add(new MicroSDTest(context));
        tests.add(new AccelerometerTest(context));
        tests.add(new MagnetometerTest(context));
        tests.add(new GyroscopeTest(context));
        tests.add(new GPSTest(context));
        tests.add(new ButtonsTest(context));
        return tests;
    }
}
