package com.fairphone.checkup.tests.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fairphone.checkup.R;
import com.fairphone.checkup.tests.Test;

/**
 * Created by maarten on 22/09/16.
 */

public class BatteryTest extends Test {

    private static final String TAG = BatteryTest.class.getSimpleName();

    View mTestView;
    BroadcastReceiver batteryStatusReceiver;

    public BatteryTest(Context context) {
        super(context);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.battery_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.battery_test_description;
    }

    protected void replaceView() {
        mTestView = LayoutInflater.from(getContext()).inflate(R.layout.view_battery_test, null);
        setTestView(mTestView);
    }

    @Override
    protected void runTest() {
        replaceView();
        setupBroadcastReceiver();
    }

    @Override
    protected void onCleanUp() {
        getContext().unregisterReceiver(batteryStatusReceiver);
        batteryStatusReceiver = null;
        super.onCleanUp();
    }

    private void setupBroadcastReceiver() {
        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction(Intent.ACTION_POWER_CONNECTED);
        ifilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        ifilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        batteryStatusReceiver = new BatteryReceiver();
        getContext().registerReceiver(batteryStatusReceiver, ifilter);
    }

    class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            switch(status) {
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    ((TextView)findViewById(R.id.battery_status_value)).setText(getResources().getString(R.string.battery_status_unknown));
                    break;
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    ((TextView)findViewById(R.id.battery_status_value)).setText(getResources().getString(R.string.battery_status_charging));
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    ((TextView)findViewById(R.id.battery_status_value)).setText(getResources().getString(R.string.battery_status_discharging));
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    ((TextView)findViewById(R.id.battery_status_value)).setText(getResources().getString(R.string.battery_status_not_charging));
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    ((TextView)findViewById(R.id.battery_status_value)).setText(getResources().getString(R.string.battery_status_full));
                    break;
                default:
                    ((TextView)findViewById(R.id.battery_status_value)).setText(getResources().getString(R.string.not_available));
                    break;
            }

            int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            switch(plugged) {
                case BatteryManager.BATTERY_PLUGGED_AC:
                    ((TextView)findViewById(R.id.battery_plugged_value)).setText(getResources().getString(R.string.battery_plugged_ac));
                    break;
                case BatteryManager.BATTERY_PLUGGED_USB:
                    ((TextView)findViewById(R.id.battery_plugged_value)).setText(getResources().getString(R.string.battery_plugged_usb));
                    break;
                case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                    ((TextView)findViewById(R.id.battery_plugged_value)).setText(getResources().getString(R.string.battery_plugged_wireless));
                    break;
                default:
                    ((TextView)findViewById(R.id.battery_plugged_value)).setText("No");
                    break;
            }

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

            ((TextView)findViewById(R.id.battery_level_value)).setText(level + getResources().getString(R.string.battery_level_unit));

            ((TextView)findViewById(R.id.battery_voltage_value)).setText(intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) / 1000.0F + " " + getResources().getString(R.string.battery_voltage_unit));
            ((TextView)findViewById(R.id.battery_temperature_value)).setText(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10.0F + " " + getResources().getString(R.string.battery_temperature_unit));

            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            switch (health) {
                case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                    ((TextView)findViewById(R.id.battery_health_value)).setText(getResources().getString(R.string.battery_health_unkown));
                    break;
                case BatteryManager.BATTERY_HEALTH_GOOD:
                    ((TextView)findViewById(R.id.battery_health_value)).setText(getResources().getString(R.string.battery_health_good));
                    break;
                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    ((TextView)findViewById(R.id.battery_health_value)).setText(getResources().getString(R.string.battery_health_overheat));
                    break;
                case BatteryManager.BATTERY_HEALTH_DEAD:
                    ((TextView)findViewById(R.id.battery_health_value)).setText(getResources().getString(R.string.battery_health_dead));
                    break;
                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    ((TextView)findViewById(R.id.battery_health_value)).setText(getResources().getString(R.string.battery_health_over_voltage));
                    break;
                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    ((TextView)findViewById(R.id.battery_health_value)).setText(getResources().getString(R.string.battery_health_unspecified_failure));
                    break;
                case BatteryManager.BATTERY_HEALTH_COLD:
                    ((TextView)findViewById(R.id.battery_health_value)).setText(getResources().getString(R.string.battery_health_cold));
                    break;
                default:
                    ((TextView)findViewById(R.id.battery_health_value)).setText(getResources().getString(R.string.not_available));
            }
        }
    }
}
