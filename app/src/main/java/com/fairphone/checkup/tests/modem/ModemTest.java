package com.fairphone.checkup.tests.modem;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fairphone.checkup.R;
import com.fairphone.checkup.tests.NewTest;
import com.fairphone.checkup.tests.Test;
import com.fairphone.checkup.tests.microphone.PrimaryMicTest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ModemTest extends NewTest {

    private static final String TAG = ModemTest.class.getSimpleName();

    public static final Details DETAILS = new NewTest.Details(R.string.modem_test_title, R.string.modem_test_summary, R.string.modem_test_description) {
        @Override
        public Fragment getFragment() {
            return new ModemTest();
        }
    };

    protected ViewGroup mContainer;
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

    public ModemTest() {
        super(false);

        mTestSimViews = new ArrayList<>(2);
    }

    @Override
    protected Details getDetails() {
        return DETAILS;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTestSimViews.clear();

        final ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_modem_test, container, false);
        final TextView descriptionView = (TextView) root.findViewById(R.id.test_description);
        final ViewGroup contentContainer = (ViewGroup) root.findViewById(R.id.content_layout);

        mTestSimViews.add((ViewGroup) inflater.inflate(R.layout.view_modem_test_sim, contentContainer, false));
        mTestSimViews.add((ViewGroup) inflater.inflate(R.layout.view_modem_test_sim, contentContainer, false));

        descriptionView.setText(getDetails().getDescription(getActivity()));
        contentContainer.removeAllViews();
        contentContainer.addView(mTestSimViews.get(0));
        contentContainer.addView(mTestSimViews.get(1));

        mContainer = container;

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        beginTest();
    }

    @Override
    protected void onCreateTest() {
        super.onCreateTest();

        mTelephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        mSubscriptionManager = SubscriptionManager.from(getActivity());

        setupReflection();

        for (int slotIndex = 0; slotIndex < 2; slotIndex++) {
            final ViewGroup testSimView = mTestSimViews.get(slotIndex);

            try {
                ((TextView) testSimView.findViewById(R.id.modem_imei_value)).setText((String) getImei.invoke(mTelephonyManager, slotIndex));
            } catch (Throwable e) {
                Log.e(TAG, String.format("Could not retrieve SIM slot #%d IMEI", slotIndex), e);
            }
        }
    }

    @Override
    protected void onResumeTest(boolean firstResume) {
        super.onResumeTest(firstResume);

        setupBroadcastReceiver();
    }

    @Override
    protected void onPauseTest() {
        super.onPauseTest();

        if (broadcastReceiver != null) {
            getActivity().unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
        }
//        if (mSubscriptionsChangedListener != null) {
//            mSubscriptionManager.removeOnSubscriptionsChangedListener(mSubscriptionsChangedListener);
//            mSubscriptionsChangedListener = null;
//        }
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
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        filter.addAction("android.net.wifi.STATE_CHANGE");
//        broadcastReceiver = new ConnectionChangeReceiver();
//        getActivity().registerReceiver(broadcastReceiver, filter);

        mSubscriptionsChangedListener = new SubscriptionsChangedListener();
        mSubscriptionManager.addOnSubscriptionsChangedListener(mSubscriptionsChangedListener);
    }

    private void readSimDetails() {
        ViewGroup testSimView;

        for (int slotIndex = 0; slotIndex < 2; slotIndex++) {
            final SubscriptionInfo subscriptionInfo = mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotIndex);

            testSimView = mTestSimViews.get(slotIndex);

            if (subscriptionInfo == null) {
                ((TextView) testSimView.findViewById(R.id.sim_title)).setText(String.format(getString(R.string.modem_unavailable_sim_title), slotIndex + 1));

                // Hide the connectivity details
                testSimView.findViewById(R.id.modem_sim_connectivity_details).setVisibility(View.GONE);
            } else {
                ((TextView) testSimView.findViewById(R.id.sim_title)).setText(String.format(getString(R.string.modem_sim_title), slotIndex + 1));

                testSimView.findViewById(R.id.modem_sim_connectivity_details).setVisibility(View.VISIBLE);

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
                    ((TextView) testSimView.findViewById(R.id.modem_roaming_value)).setText((boolean) isNetworkRoaming.invoke(mTelephonyManager, subscriptionInfo.getSubscriptionId()) ? getString(R.string.yes) : getString(R.string.no));
                    ((TextView) testSimView.findViewById(R.id.modem_data_roaming_value)).setText(subscriptionInfo.getDataRoaming() == SubscriptionManager.DATA_ROAMING_ENABLE ? getString(R.string.yes) : getString(R.string.no));
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
