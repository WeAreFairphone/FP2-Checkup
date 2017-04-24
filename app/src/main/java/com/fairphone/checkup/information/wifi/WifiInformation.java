package com.fairphone.checkup.information.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.fairphone.checkup.information.Information;

public class WifiInformation extends Information<WifiDetails> {

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Connection changed!");

            refreshDetails(true);
        }
    };

    private final ConnectivityManager mConnectivityManager;
    private final WifiManager mWifiManager;
    private final IntentFilter mBroadcastReceiverFilter;

    public WifiInformation(Context context, ChangeListener<WifiDetails> listener) {
        super(context, listener, new WifiDetails(context));

        mConnectivityManager = (ConnectivityManager) mContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mBroadcastReceiverFilter = new IntentFilter();
        mBroadcastReceiverFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mBroadcastReceiverFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
    }

    @Override
    public void setUp() {
        mContext.registerReceiver(broadcastReceiver, mBroadcastReceiverFilter);
    }

    @Override
    public void tearDown() {
        mContext.unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onRefreshDetails() {
        if (!mWifiManager.isWifiEnabled()) {
            mInstanceDetails.setDisabled();
        } else {
            LinkProperties linkProperties = null;

            // Initialise the link properties status
            for (Network network : mConnectivityManager.getAllNetworks()) {
                if (ConnectivityManager.TYPE_WIFI == mConnectivityManager.getNetworkInfo(network).getType()) {
                    linkProperties = mConnectivityManager.getLinkProperties(network);
                    // We support only one active Wi-Fi network
                    break;
                }
            }

            mInstanceDetails.setEnabled(mWifiManager.getConnectionInfo(), linkProperties);
        }
    }

}