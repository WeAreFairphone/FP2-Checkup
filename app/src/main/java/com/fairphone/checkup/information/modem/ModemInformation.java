package com.fairphone.checkup.information.modem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.fairphone.checkup.information.Information;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ModemInformation extends Information<ModemDetails> {

    private final SubscriptionManager.OnSubscriptionsChangedListener mSubscriptionsChangedListener = new SubscriptionManager.OnSubscriptionsChangedListener() {
        @Override
        public void onSubscriptionsChanged() {
            Log.d(TAG, "Subscriptions changed!");

            refreshDetails(true);
        }
    };
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Connection changed!");

            refreshDetails(true);
        }
    };

    private final TelephonyManager mTelephonyManager;
    private final SubscriptionManager mSubscriptionManager;
    private final IntentFilter mBroadcastReceiverFilter;

    private Class mTelephonyManagerClass;
    private Method getNetworkOperatorName;
    private Method getNetworkOperatorCode;
    private Method getSimOperatorName;
    private Method getSimOperatorCode;
    private Method getNetworkType;
    private Method isNetworkRoaming;
    private Method getImei;

    public ModemInformation(Context context, ChangeListener<ModemDetails> listener) {
        super(context, listener, new ModemDetails(context));

        mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        mSubscriptionManager = SubscriptionManager.from(mContext);

        mBroadcastReceiverFilter = new IntentFilter();
        mBroadcastReceiverFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mBroadcastReceiverFilter.addAction("android.net.wifi.STATE_CHANGE");

        setUpReflection();

        setUpActiveSimCards();
    }

    @Override
    public void setUp() {
        mContext.registerReceiver(broadcastReceiver, mBroadcastReceiverFilter);
        mSubscriptionManager.addOnSubscriptionsChangedListener(mSubscriptionsChangedListener);
    }

    @Override
    public void tearDown() {
        mContext.unregisterReceiver(broadcastReceiver);
        mSubscriptionManager.removeOnSubscriptionsChangedListener(mSubscriptionsChangedListener);
    }

    @Override
    protected void onRefreshDetails() {
        for (int slotIndex = 0; slotIndex < ModemDetails.NB_SIM_SLOTS; slotIndex++) {
            final SimSlotDetails simSlotDetails = mInstanceDetails.getSimSlotDetails(slotIndex);

            // Ignore inactive SIM cards
            if (simSlotDetails == null) {
                continue;
            }

            final SubscriptionInfo subscriptionInfo = mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotIndex);

            if (subscriptionInfo == null) {
                simSlotDetails.setNotConnectedToNetwork();
            } else {
                try {
                    simSlotDetails.setConnectedToNetwork(
                            (String) getNetworkOperatorName.invoke(mTelephonyManager, subscriptionInfo.getSubscriptionId()),
                            (String) getNetworkOperatorCode.invoke(mTelephonyManager, subscriptionInfo.getSubscriptionId()),
                            (int) getNetworkType.invoke(mTelephonyManager, subscriptionInfo.getSubscriptionId()),
                            subscriptionInfo.getCountryIso(),
                            false /*TODO*/,
                            (boolean) isNetworkRoaming.invoke(mTelephonyManager, subscriptionInfo.getSubscriptionId()),
                            subscriptionInfo.getDataRoaming() == SubscriptionManager.DATA_ROAMING_ENABLE
                    );
                } catch (IllegalAccessException | InvocationTargetException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        }
    }

    private void setUpReflection() {
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
        } catch (Throwable e) {
        }
    }

    private void setUpActiveSimCards() {
        for (int slotIndex = 0; slotIndex < ModemDetails.NB_SIM_SLOTS; slotIndex++) {
            final SimSlotDetails simSlotDetails = mInstanceDetails.createSimSlotDetails(slotIndex);
            final SubscriptionInfo subscriptionInfo = mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotIndex);

            try {
                simSlotDetails.setSimDetails(
                        (String) getImei.invoke(mTelephonyManager, subscriptionInfo.getSubscriptionId()),
                        (String) getSimOperatorName.invoke(mTelephonyManager, subscriptionInfo.getSubscriptionId()),
                        (String) getSimOperatorCode.invoke(mTelephonyManager, subscriptionInfo.getSubscriptionId())
                );
            } catch (IllegalAccessException | InvocationTargetException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
    }

}
