package com.fairphone.checkup.tests.dualsim;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;

import com.fairphone.checkup.R;
import com.fairphone.checkup.tests.Test;

/**
 * Created by maarten on 4-12-15.
 */
public class DualSimTest extends Test {

    private static final String TAG = DualSimTest.class.getSimpleName();

    View mTestView;

    public DualSimTest(Context context) {
        super(context);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.dualsim_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.dualsim_test_description;
    }

    protected void replaceView() {
        mTestView = LayoutInflater.from(getContext()).inflate(R.layout.view_dualsim_test, null);
        setTestView(mTestView);
    }

    @Override
    protected void runTest() {
        replaceView();
        showSIMinfo();
    }

    private void showSIMinfo() {
        TelephonyManager telephonyManager = (TelephonyManager)getContext().getSystemService(Context.TELEPHONY_SERVICE);
    }

    private String getSimStateString(int simState) {
        String simStateString = getResources().getString(R.string.not_available);
        switch (simState) {
            case TelephonyManager.SIM_STATE_UNKNOWN:
                simStateString = getResources().getString(R.string.sim_state_unkown);
                break;
            case TelephonyManager.SIM_STATE_ABSENT:
                simStateString = getResources().getString(R.string.sim_state_absent);
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                simStateString = getResources().getString(R.string.sim_state_pin_required);
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                simStateString = getResources().getString(R.string.sim_state_puk_required);
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                simStateString = getResources().getString(R.string.sim_state_network_locked);
                break;
            case TelephonyManager.SIM_STATE_READY:
                simStateString = getResources().getString(R.string.sim_state_ready);
                break;
            default:
                break;
        }
        return simStateString;
    }
}