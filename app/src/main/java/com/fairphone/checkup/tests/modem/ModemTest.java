package com.fairphone.checkup.tests.modem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.PhoneStateListener;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fairphone.checkup.R;
import com.fairphone.checkup.tests.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by maarten on 03/10/16.
 */

public class ModemTest extends Test {

    private static final String TAG = ModemTest.class.getSimpleName();

    View mTestView;

    private TelephonyManager mTelephonyManager;
    private List<SubscriptionInfo> mSelectableSubInfos;
    private SubscriptionInfo mSir;

    private PhoneStateListener mPhoneStateListener;
    private BroadcastReceiver broadcastReceiver;

    Class mTelephonyManagerClass;
    Method getNetworkOperatorName;
    Method getNetworkOperatorCode;
    Method getSimOperatorName;
    Method getSimOperatorCode;
    Method getNetworkType;
    Method getImei;

    public ModemTest(Context context) {
        super(context);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.modem_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.modem_test_description;
    }

    private void replaceView() {
        mTestView = LayoutInflater.from(getContext()).inflate(R.layout.view_modem_test, null);
        setTestView(mTestView);
    }

    @Override
    protected void runTest() {
        replaceView();

        mSelectableSubInfos = new ArrayList<SubscriptionInfo>();
        mTelephonyManager = (TelephonyManager)getContext().getSystemService(Context.TELEPHONY_SERVICE);

        SubscriptionManager subscriptionManager = SubscriptionManager.from(getContext());
        mSelectableSubInfos = subscriptionManager.getActiveSubscriptionInfoList();

        setupReflection();

        setupBroadcastReceiver();
    }

    @Override
    protected void onCleanUp() {
        getContext().unregisterReceiver(broadcastReceiver);
        super.onCleanUp();
    }

    private void setupReflection() {
        try {
            mTelephonyManagerClass = Class.forName(mTelephonyManager.getClass().getName());

            getNetworkOperatorName = mTelephonyManagerClass.getDeclaredMethod("getNetworkOperatorName", int.class);
            getNetworkOperatorName.setAccessible(true);

            getNetworkOperatorCode = mTelephonyManagerClass.getDeclaredMethod("getNetworkOperatorForSubscription", int.class);
            getNetworkOperatorCode.setAccessible(true);

            getSimOperatorName = mTelephonyManagerClass.getDeclaredMethod("getSimOperatorNameForSubscription", int.class);
            getSimOperatorName.setAccessible(true);

            getSimOperatorCode = mTelephonyManagerClass.getDeclaredMethod("getSimOperator", int.class);
            getSimOperatorCode.setAccessible(true);

            getNetworkType = mTelephonyManagerClass.getDeclaredMethod("getNetworkType", int.class);
            getNetworkType.setAccessible(true);

            getImei = mTelephonyManagerClass.getDeclaredMethod("getImei", int.class);
            getImei.setAccessible(true);
        } catch (Throwable e) {}
    }

    private void setupBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        broadcastReceiver = new ConnectionChangeReceiver();
        getContext().registerReceiver(broadcastReceiver, filter);
    }

    class ConnectionChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                for (SubscriptionInfo subscriptionInfo : mSelectableSubInfos) {
                    if (subscriptionInfo.getSimSlotIndex() == 0) {
                        ((TextView)findViewById(R.id.modem_network_operator_value)).setText((String)getNetworkOperatorName.invoke(mTelephonyManager,subscriptionInfo.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_network_operator_code_value)).setText((String)getNetworkOperatorCode.invoke(mTelephonyManager,subscriptionInfo.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_sim_operator_value)).setText((String)getSimOperatorName.invoke(mTelephonyManager,subscriptionInfo.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_sim_operator_code_value)).setText((String)getSimOperatorCode.invoke(mTelephonyManager,subscriptionInfo.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_network_type_value)).setText(getNetworkTypeName((int)getNetworkType.invoke(mTelephonyManager,subscriptionInfo.getSubscriptionId())));
                        ((TextView)findViewById(R.id.modem_mnc0_value)).setText("" + subscriptionInfo.getMnc());
                        ((TextView)findViewById(R.id.modem_mcc0_value)).setText("" + subscriptionInfo.getMcc());
                        ((TextView)findViewById(R.id.modem_countrycode_value)).setText(subscriptionInfo.getCountryIso());
                        ((TextView)findViewById(R.id.modem_imei0_value)).setText((String)getImei.invoke(mTelephonyManager,0));
                    }
                    if (subscriptionInfo.getSimSlotIndex() == 1) {
                        ((TextView)findViewById(R.id.modem_network_operator2_value)).setText((String)getNetworkOperatorName.invoke(mTelephonyManager,subscriptionInfo.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_network_operator_code2_value)).setText((String)getNetworkOperatorCode.invoke(mTelephonyManager,subscriptionInfo.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_sim_operator2_value)).setText((String)getSimOperatorName.invoke(mTelephonyManager,subscriptionInfo.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_sim_operator_code2_value)).setText((String)getSimOperatorCode.invoke(mTelephonyManager,subscriptionInfo.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_network_type2_value)).setText(getNetworkTypeName((int)getNetworkType.invoke(mTelephonyManager,subscriptionInfo.getSubscriptionId())));
                        ((TextView)findViewById(R.id.modem_mnc1_value)).setText("" + subscriptionInfo.getMnc());
                        ((TextView)findViewById(R.id.modem_mcc1_value)).setText("" + subscriptionInfo.getMcc());
                        ((TextView)findViewById(R.id.modem_countrycode1_value)).setText(subscriptionInfo.getCountryIso());
                        ((TextView)findViewById(R.id.modem_imei1_value)).setText((String)getImei.invoke(mTelephonyManager,1));
                    }
                }
            } catch (Throwable e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
    }

     private String getNetworkTypeName(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_1xRTT: return "1xRTT";
            case TelephonyManager.NETWORK_TYPE_CDMA: return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EDGE: return "EDGE";
            case TelephonyManager.NETWORK_TYPE_EHRPD: return "eHRPD";
            case TelephonyManager.NETWORK_TYPE_EVDO_0: return "EVDO rev. 0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A: return "EVDO rev. A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B: return "EVDO rev. B";
            case TelephonyManager.NETWORK_TYPE_GPRS: return "GPRS";
            case TelephonyManager.NETWORK_TYPE_HSDPA: return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSPA: return "HSPA";
            case TelephonyManager.NETWORK_TYPE_HSPAP: return "HSPA+";
            case TelephonyManager.NETWORK_TYPE_HSUPA: return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_IDEN: return "iDen";
            case TelephonyManager.NETWORK_TYPE_LTE: return "LTE";
            case TelephonyManager.NETWORK_TYPE_UMTS: return "UMTS";
            case TelephonyManager.NETWORK_TYPE_UNKNOWN: return "Unknown";
        }
        return "";
    }
}
