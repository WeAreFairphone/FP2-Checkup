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
import android.view.ViewGroup;
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

    private List<ViewGroup> mTestSimViews;

    private TelephonyManager mTelephonyManager;
    private SubscriptionManager mSubscriptionManager;

    private SubscriptionManager.OnSubscriptionsChangedListener mSubscriptionsChangedListener;
    private BroadcastReceiver broadcastReceiver;

    Class mTelephonyManagerClass;
    Method getNetworkOperatorName;
    Method getNetworkOperatorCode;
    Method getSimOperatorName;
    Method getSimOperatorCode;
    Method getNetworkType;
    Method isNetworkRoaming;
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
        if (mTestSimViews == null) {
            mTestSimViews = new ArrayList<>(2);
        }
        mTestSimViews.clear();

        mTestSimViews.add((ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.view_modem_test_sim, null));
        mTestSimViews.add((ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.view_modem_test_sim, null));

        final ViewGroup testView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.view_modem_test, null);
        final ViewGroup testViewContainer = (ViewGroup) testView.findViewById(R.id.content_layout);
        testViewContainer.removeAllViews();
        testViewContainer.addView(mTestSimViews.get(0));
        testViewContainer.addView(mTestSimViews.get(1));
        setTestView(testView);
    }

    @Override
    protected void runTest() {
    }

    @Override
    protected void onPrepare() {
        hideActionButton();
        replaceView();

        mTelephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        mSubscriptionManager = SubscriptionManager.from(getContext());

        setupReflection();

        // ACTUAL TEST //

        for (int slotIndex = 0; slotIndex < 2; slotIndex++) {
            final ViewGroup testSimView = mTestSimViews.get(slotIndex);

            try {
                ((TextView) testSimView.findViewById(R.id.modem_imei_value)).setText((String) getImei.invoke(mTelephonyManager, slotIndex));
            } catch (Throwable e) {
                Log.e(TAG, String.format("Could not retrieve SIM slot #%d IMEI", slotIndex), e);
            }
        }

        setupBroadcastReceiver();

        // END OF ACTUAL TEST //
    }

    @Override
    protected void onCleanUp() {
        getContext().unregisterReceiver(broadcastReceiver);
        mSubscriptionManager.removeOnSubscriptionsChangedListener(mSubscriptionsChangedListener);

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

            isNetworkRoaming = mTelephonyManagerClass.getDeclaredMethod("isNetworkRoaming", int.class);
            isNetworkRoaming.setAccessible(true);

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

        mSubscriptionsChangedListener = new SubscriptionsChangedListener();
        mSubscriptionManager.addOnSubscriptionsChangedListener(mSubscriptionsChangedListener);
    }

    private void readSimDetails() {
        ViewGroup testSimView;

        for (int slotIndex = 0; slotIndex < 2; slotIndex++) {
            final SubscriptionInfo subscriptionInfo = mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotIndex);

            testSimView = mTestSimViews.get(slotIndex);

            if (subscriptionInfo == null) {
                ((TextView) testSimView.findViewById(R.id.sim_title)).setText(String.format(getContext().getString(R.string.modem_unavailable_sim_title), slotIndex + 1));

                // Hide the connectivity details
                testSimView.findViewById(R.id.modem_sim_connectivity_details).setVisibility(GONE);
            } else {
                ((TextView) testSimView.findViewById(R.id.sim_title)).setText(String.format(getContext().getString(R.string.modem_sim_title), slotIndex + 1));

                testSimView.findViewById(R.id.modem_sim_connectivity_details).setVisibility(VISIBLE);

                try {
                    String simOperatorCode = (String) getSimOperatorCode.invoke(mTelephonyManager, subscriptionInfo.getSubscriptionId());
                    String networkOperatorCode = (String) getNetworkOperatorCode.invoke(mTelephonyManager, subscriptionInfo.getSubscriptionId());

                    ((TextView) testSimView.findViewById(R.id.modem_sim_operator_value)).setText((String) getSimOperatorName.invoke(mTelephonyManager, subscriptionInfo.getSubscriptionId()));
                    ((TextView) testSimView.findViewById(R.id.modem_sim_operator_code_value)).setText(simOperatorCode);
                    ((TextView) testSimView.findViewById(R.id.modem_sim_mcc_value)).setText(simOperatorCode.substring(0, 3));
                    ((TextView) testSimView.findViewById(R.id.modem_sim_mnc_value)).setText(simOperatorCode.substring(3));
                    ((TextView) testSimView.findViewById(R.id.modem_network_operator_value)).setText((String) getNetworkOperatorName.invoke(mTelephonyManager, subscriptionInfo.getSubscriptionId()));
                    ((TextView) testSimView.findViewById(R.id.modem_network_operator_code_value)).setText(networkOperatorCode);
                    ((TextView) testSimView.findViewById(R.id.modem_network_mcc_value)).setText(networkOperatorCode.substring(0, 3));
                    ((TextView) testSimView.findViewById(R.id.modem_network_mnc_value)).setText(networkOperatorCode.substring(3));
                    ((TextView) testSimView.findViewById(R.id.modem_network_type_value)).setText(getNetworkTypeName((int) getNetworkType.invoke(mTelephonyManager, subscriptionInfo.getSubscriptionId())));
                    ((TextView) testSimView.findViewById(R.id.modem_country_code_value)).setText(subscriptionInfo.getCountryIso());
                    ((TextView) testSimView.findViewById(R.id.modem_roaming_value)).setText((boolean) isNetworkRoaming.invoke(mTelephonyManager, subscriptionInfo.getSubscriptionId()) ? getContext().getString(R.string.yes) : getContext().getString(R.string.no));
                    ((TextView) testSimView.findViewById(R.id.modem_data_roaming_value)).setText(subscriptionInfo.getDataRoaming() == SubscriptionManager.DATA_ROAMING_ENABLE ? getContext().getString(R.string.yes) : getContext().getString(R.string.no));
                } catch (Throwable e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        }
    }

    class ConnectionChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Connection changed!");

            readSimDetails();
        }
    }

    class SubscriptionsChangedListener extends SubscriptionManager.OnSubscriptionsChangedListener {
        @Override
        public void onSubscriptionsChanged() {
            Log.d(TAG, "Subscriptions changed!");

            readSimDetails();
        }
    }

     private static String getNetworkTypeName(int networkType) {
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
            case TelephonyManager.NETWORK_TYPE_UNKNOWN: default: return "Unknown";
        }
    }
}
