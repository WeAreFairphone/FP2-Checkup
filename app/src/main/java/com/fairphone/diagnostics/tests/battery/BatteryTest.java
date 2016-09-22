package com.fairphone.diagnostics.tests.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;

/**
 * Created by maarten on 22/09/16.
 */

public class BatteryTest extends Test {

    private static final String TAG = BatteryTest.class.getSimpleName();

    private TextView batteryHealth;
    private TextView batteryLevel;
    private TextView batteryStatus;
    private TextView batteryTempature;
    private TextView batteryVoltage;
    //final Context context = this;
    //private RelativeLayout layout;
    //private Class tcNext = CameraKeyTest.class;
    //private int testMode;

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
        View mTestView = LayoutInflater.from(getContext()).inflate(R.layout.view_battery_test, null);
        setTestView(mTestView);
    }


    @Override
    protected void onPrepare() {

    }

    @Override
    protected void runTest() {
        replaceView();

        batteryLevel = (TextView)findViewById(R.id.tvBatteryLevel);
        batteryStatus = ((TextView)findViewById(R.id.tvBatteryStatus));
        batteryVoltage = ((TextView)findViewById(R.id.tvBatteryVolt));
        batteryTempature = ((TextView)findViewById(R.id.tvBatteryTemperature));
        batteryHealth = ((TextView)findViewById(R.id.tvBatteryHealth));

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        getContext().registerReceiver(batteryStatusReceiver, intentFilter);

        askIfSuccess(getResources().getString(R.string.battery_test_finish_question));
    }

    private BroadcastReceiver batteryStatusReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                batteryLevel.setText("Level: " + String.valueOf(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)) + "%");
                batteryVoltage.setText("Voltage: " + String.valueOf(intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) / 1000.0F) + "V");
                batteryTempature.setText("Temperature: " + String.valueOf(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10.0F) + " °C");

                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 1);
                int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 1);

                String statusResult = "Unknown";
                if(status == BatteryManager.BATTERY_STATUS_CHARGING) {
                    statusResult = "Charging";
                }
                if (status == BatteryManager.BATTERY_STATUS_DISCHARGING || status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
                    statusResult = "Not Charging";
                }
                if ((status == BatteryManager.BATTERY_STATUS_FULL) || (batteryLevel == 100)) {
                    statusResult = "Phone detected charger but unable to proceed with charging. Reason: Battery full charged";
                }
                batteryStatus.setText("Status: " + statusResult);


                int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 1);

                String result;
                switch (health) {
                    case BatteryManager.BATTERY_HEALTH_GOOD:
                        result = "Good";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                        result = "Over Heat";
                        break;
                    case BatteryManager.BATTERY_HEALTH_DEAD:
                        result = "Dead";
                        break;
                    case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                        result = "Over Voltage";
                        break;
                    case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                        result = "Unspecified Failure";
                        break;
                    default:
                        result = "Unknown";
                }
                batteryHealth.setText("Health: " + result);
            }
        }
    };
}
