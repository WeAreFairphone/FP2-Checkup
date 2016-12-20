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

    private Method getImei;
    private Method getSimState;
    private Method getSimOperatorName;
    private Method getSimOperatorCode;
    private Method getNetworkOperatorName;
    private Method getNetworkOperatorCode;
    private Method getNetworkType;
    private Method isNetworkRoaming;

    public ModemInformation(Context context, ChangeListener<ModemDetails> listener) {
        super(context, listener, new ModemDetails(context));

        mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        mSubscriptionManager = SubscriptionManager.from(mContext);

        mBroadcastReceiverFilter = new IntentFilter();
        mBroadcastReceiverFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mBroadcastReceiverFilter.addAction("android.net.wifi.STATE_CHANGE");

        setUpReflection();

        setUpSimSlots();
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
            final SubscriptionInfo subscriptionInfo = mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(slotIndex);

            if (subscriptionInfo == null) {
                // Do we need to update the current details?
                if (simSlotDetails.isSimPresent()) {
                    simSlotDetails.setSimNotPresent();
                }
            } else {
                try {
                    int simState = (Integer) getSimState.invoke(mTelephonyManager, slotIndex);

                    simSlotDetails.setSimPresent(
                            simState,
                            (String) getSimOperatorName.invoke(mTelephonyManager, subscriptionInfo.getSubscriptionId()),
                            (String) getSimOperatorCode.invoke(mTelephonyManager, subscriptionInfo.getSubscriptionId())
                    );

                    if (TelephonyManager.SIM_STATE_READY == simState) {
                        simSlotDetails.setSimConnectedToNetwork(
                                simState,
                                (String) getNetworkOperatorName.invoke(mTelephonyManager, subscriptionInfo.getSubscriptionId()),
                                (String) getNetworkOperatorCode.invoke(mTelephonyManager, subscriptionInfo.getSubscriptionId()),
                                (int) getNetworkType.invoke(mTelephonyManager, subscriptionInfo.getSubscriptionId()),
                                subscriptionInfo.getCountryIso(),
                                false /*TODO*/,
                                (boolean) isNetworkRoaming.invoke(mTelephonyManager, subscriptionInfo.getSubscriptionId()),
                                subscriptionInfo.getDataRoaming() == SubscriptionManager.DATA_ROAMING_ENABLE
                        );
                    } else {
                        simSlotDetails.setSimNotConnectedToNetwork(simState);
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        }
    }

    private void setUpReflection() {
        try {
            final Class telephonyManagerClass = Class.forName(mTelephonyManager.getClass().getName());

            getImei = telephonyManagerClass.getDeclaredMethod("getImei", int.class);
            getImei.setAccessible(true);

            getSimState = telephonyManagerClass.getDeclaredMethod("getSimState", int.class);
            getSimState.setAccessible(true);

            getSimOperatorName = telephonyManagerClass.getDeclaredMethod("getSimOperatorNameForSubscription", int.class);
            getSimOperatorName.setAccessible(true);

            getSimOperatorCode = telephonyManagerClass.getDeclaredMethod("getSimOperator", int.class);
            getSimOperatorCode.setAccessible(true);

            getNetworkOperatorName = telephonyManagerClass.getDeclaredMethod("getNetworkOperatorName", int.class);
            getNetworkOperatorName.setAccessible(true);

            getNetworkOperatorCode = telephonyManagerClass.getDeclaredMethod("getNetworkOperatorForSubscription", int.class);
            getNetworkOperatorCode.setAccessible(true);

            getNetworkType = telephonyManagerClass.getDeclaredMethod("getNetworkType", int.class);
            getNetworkType.setAccessible(true);

            isNetworkRoaming = telephonyManagerClass.getDeclaredMethod("isNetworkRoaming", int.class);
            isNetworkRoaming.setAccessible(true);
        } catch (Throwable e) {
            Log.e(TAG, "Could not retrieve a declared method through reflection", e);
        }
    }

    private void setUpSimSlots() {
        for (int slotIndex = 0; slotIndex < ModemDetails.NB_SIM_SLOTS; slotIndex++) {
            final SimSlotDetails simSlotDetails = mInstanceDetails.getSimSlotDetails(slotIndex);

            try {
                simSlotDetails.setImei((String) getImei.invoke(mTelephonyManager, slotIndex));
            } catch (IllegalAccessException | InvocationTargetException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
    }

}
