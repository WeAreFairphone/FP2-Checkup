package com.fairphone.checkup.tests.modem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fairphone.checkup.R;
import com.fairphone.checkup.tests.Test;

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

        for (int i = 0; i < mTelephonyManager.getSimCount(); i++) {
            final SubscriptionInfo sir = findRecordBySlotId(getContext(), i);
            if (sir != null) {
                mSelectableSubInfos.add(sir);
            }
        }
        mSir = mSelectableSubInfos.size() > 0 ? mSelectableSubInfos.get(0) : null;

        updatePhoneInfos();

        setupBroadcastReceiver();
    }

    @Override
    protected void onCleanUp() {
        getContext().unregisterReceiver(broadcastReceiver);
        super.onCleanUp();
    }

    private void updatePhoneInfos() {
        if (mSir != null) {
            mPhoneStateListener = new PhoneStateListener(mSir.getSubscriptionId()) {
                @Override
                public void onDataConnectionStateChanged(int state) {
                    //updateDataState();
                    //updateNetworkType();
                }

                @Override
                public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                    //updateSignalStrength(signalStrength);
                }

                @Override
                public void onServiceStateChanged(ServiceState serviceState) {
                    //updateServiceState(serviceState);
                }
            };
        }
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
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

            String defaultImei = mTelephonyManager.getImei();
            String imei0 = mTelephonyManager.getImei(0);
            String imei1 = mTelephonyManager.getImei(1);

            SubscriptionManager mSubscriptionManager = SubscriptionManager.from(getContext());
            List<SubscriptionInfo> mSubInfoList = mSubscriptionManager.getActiveSubscriptionInfoList();

            if(mSubInfoList != null) {
                // there seems to be at least one card present, let's find out

                if(mSubInfoList.size() == 1)  {                                                         // there's one card present
                    SubscriptionInfo subscriptionInfo = mSubInfoList.get(0);

                    if(subscriptionInfo.getSimSlotIndex() == 0) {                                       // the card is in the first slot
                        //sim1Presence.setText(R.string.dualsim_card_present_yes);
                        ((TextView)findViewById(R.id.modem_network_operator_value)).setText(mTelephonyManager.getNetworkOperatorName(subscriptionInfo.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_network_operator_code_value)).setText(mTelephonyManager.getNetworkOperatorForSubscription(subscriptionInfo.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_sim_operator_value)).setText(mTelephonyManager.getSimOperatorNameForSubscription(subscriptionInfo.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_sim_operator_code_value)).setText(mTelephonyManager.getSimOperator(subscriptionInfo.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_network_type_value)).setText(getNetworkTypeName(mTelephonyManager.getNetworkType(subscriptionInfo.getSubscriptionId())));
                        ((TextView)findViewById(R.id.modem_countrycode_value)).setText(subscriptionInfo.getCountryIso());
                        //sim2Presence.setText(R.string.dualsim_card_present_no);
                    } else if(subscriptionInfo.getSimSlotIndex() == 1) {                                // the card is in the second slot
                        //sim1Presence.setText(R.string.dualsim_card_present_no);
                        //sim2Presence.setText(R.string.dualsim_card_present_yes);
                        ((TextView)findViewById(R.id.modem_network_operator2_value)).setText(mTelephonyManager.getNetworkOperatorName(subscriptionInfo.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_network_operator_code2_value)).setText(mTelephonyManager.getNetworkOperatorForSubscription(subscriptionInfo.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_sim_operator2_value)).setText(mTelephonyManager.getSimOperatorNameForSubscription(subscriptionInfo.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_sim_operator_code2_value)).setText(mTelephonyManager.getSimOperator(subscriptionInfo.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_network_type2_value)).setText(getNetworkTypeName(mTelephonyManager.getNetworkType(subscriptionInfo.getSubscriptionId())));
                        ((TextView)findViewById(R.id.modem_countrycode1_value)).setText(subscriptionInfo.getCountryIso());
                    }
                }

                if(mSubInfoList.size() == 2)  {                                                         // there's two cards present

                    SubscriptionInfo subscriptionInfo1 = mSubInfoList.get(0);

                    if(subscriptionInfo1.getSimSlotIndex() == 0) {                                       // the card is in the first slot
                        //sim1Presence.setText(R.string.dualsim_card_present_yes);
                        ((TextView)findViewById(R.id.modem_network_operator_value)).setText(mTelephonyManager.getNetworkOperatorName(subscriptionInfo1.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_network_operator_code_value)).setText(mTelephonyManager.getNetworkOperatorForSubscription(subscriptionInfo1.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_sim_operator_value)).setText(mTelephonyManager.getSimOperatorNameForSubscription(subscriptionInfo1.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_sim_operator_code_value)).setText(mTelephonyManager.getSimOperator(subscriptionInfo1.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_network_type_value)).setText(getNetworkTypeName(mTelephonyManager.getNetworkType(subscriptionInfo1.getSubscriptionId())));
                        ((TextView)findViewById(R.id.modem_countrycode_value)).setText(subscriptionInfo1.getCountryIso());
                    } else if(subscriptionInfo1.getSimSlotIndex() == 1) {                                // the card is in the second slot
                        //sim2Presence.setText(R.string.dualsim_card_present_yes);
                        ((TextView)findViewById(R.id.modem_network_operator2_value)).setText(mTelephonyManager.getNetworkOperatorName(subscriptionInfo1.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_network_operator_code2_value)).setText(mTelephonyManager.getNetworkOperatorForSubscription(subscriptionInfo1.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_sim_operator2_value)).setText(mTelephonyManager.getSimOperatorNameForSubscription(subscriptionInfo1.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_sim_operator_code2_value)).setText(mTelephonyManager.getSimOperator(subscriptionInfo1.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_network_type2_value)).setText(getNetworkTypeName(mTelephonyManager.getNetworkType(subscriptionInfo1.getSubscriptionId())));
                        ((TextView)findViewById(R.id.modem_countrycode1_value)).setText(subscriptionInfo1.getCountryIso());
                    }

                    SubscriptionInfo subscriptionInfo2 = mSubInfoList.get(1);

                    if(subscriptionInfo2.getSimSlotIndex() == 0) {                                       // the card is in the first slot
                        //sim1Presence.setText(R.string.dualsim_card_present_yes);
                        ((TextView)findViewById(R.id.modem_network_operator_value)).setText(mTelephonyManager.getNetworkOperatorName(subscriptionInfo2.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_network_operator_code_value)).setText(mTelephonyManager.getNetworkOperatorForSubscription(subscriptionInfo2.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_sim_operator_value)).setText(mTelephonyManager.getSimOperatorNameForSubscription(subscriptionInfo2.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_sim_operator_code_value)).setText(mTelephonyManager.getSimOperator(subscriptionInfo2.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_network_type_value)).setText(getNetworkTypeName(mTelephonyManager.getNetworkType(subscriptionInfo2.getSubscriptionId())));
                        ((TextView)findViewById(R.id.modem_countrycode_value)).setText(subscriptionInfo2.getCountryIso());
                    } else if(subscriptionInfo2.getSimSlotIndex() == 1) {                                // the card is in the second slot
                        //sim2Presence.setText(R.string.dualsim_card_present_yes);
                        ((TextView)findViewById(R.id.modem_network_operator2_value)).setText(mTelephonyManager.getNetworkOperatorName(subscriptionInfo2.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_network_operator_code2_value)).setText(mTelephonyManager.getNetworkOperatorForSubscription(subscriptionInfo2.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_sim_operator2_value)).setText(mTelephonyManager.getSimOperatorNameForSubscription(subscriptionInfo2.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_sim_operator_code2_value)).setText(mTelephonyManager.getSimOperator(subscriptionInfo2.getSubscriptionId()));
                        ((TextView)findViewById(R.id.modem_network_type2_value)).setText(getNetworkTypeName(mTelephonyManager.getNetworkType(subscriptionInfo2.getSubscriptionId())));
                        ((TextView)findViewById(R.id.modem_countrycode1_value)).setText(subscriptionInfo2.getCountryIso());
                    }
                }
            }
            ((TextView)findViewById(R.id.modem_data_activity_value)).setText(""+mTelephonyManager.getDataActivity());
            ((TextView)findViewById(R.id.modem_imei0_value)).setText(imei0);

            ((TextView)findViewById(R.id.modem_data_activity2_value)).setText(""+mTelephonyManager.getDataActivity());
            ((TextView)findViewById(R.id.modem_imei1_value)).setText(imei1);

            if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {

            } else {
            }
        }
    }

    public static SubscriptionInfo findRecordBySlotId(Context context, final int slotId) {
        final List<SubscriptionInfo> subInfoList =
                SubscriptionManager.from(context).getActiveSubscriptionInfoList();
        if (subInfoList != null) {
            final int subInfoLength = subInfoList.size();

            for (int i = 0; i < subInfoLength; ++i) {
                final SubscriptionInfo sir = subInfoList.get(i);
                if (sir.getSimSlotIndex() == slotId) {
                    //Right now we take the first subscription on a SIM.
                    return sir;
                }
            }
        }

        return null;
    }

    private String getDataStateString(int dataState) {
        switch(dataState) {
            case -1:
                return "Unknown";
            case 0:
                return "Disconnected";
            case 1:
                return "Connecting";
            case 2:
                return "Connected";
            case 3:
                return "Suspended";
        }
        return "";
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
