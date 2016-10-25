package com.fairphone.checkup.tests.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fairphone.checkup.R;
import com.fairphone.checkup.tests.Test;

/**
 * Created by maarten on 26/09/16.
 */

public class WifiTest extends Test {

    private static final String TAG = WifiTest.class.getSimpleName();

    View mTestView;

    private BroadcastReceiver broadcastReceiver;

    public WifiTest(Context context) {
        super(context);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.wifi_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.wifi_test_description;
    }

    private void replaceView() {
        mTestView = LayoutInflater.from(getContext()).inflate(R.layout.view_wifi_test, null);
        setTestView(mTestView);
    }

    @Override
    protected void runTest() {
        replaceView();
        setupBroadcastReceiver();
    }

    @Override
    protected void onCleanUp() {
        getContext().unregisterReceiver(broadcastReceiver);
        super.onCleanUp();
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

            WifiManager wifiManager = (WifiManager)getContext().getSystemService(Context.WIFI_SERVICE);
            boolean wifiEnabled = wifiManager.isWifiEnabled();
            ((TextView)findViewById(R.id.wifi_enabled_value)).setText(String.valueOf(wifiEnabled));

            if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if(wifiInfo != null) {
                    if (wifiManager.getConfiguredNetworks() != null) {
                        WifiConfiguration activeConfig = null;
                        for (WifiConfiguration conn : wifiManager.getConfiguredNetworks()) {
                            if (conn.status == WifiConfiguration.Status.CURRENT) {
                                activeConfig = conn;
                                break;
                            }
                        }
                        if (activeConfig != null) {
                        }
                    }

                    ((TextView) findViewById(R.id.wifi_ssid_value)).setText(wifiInfo.getSSID());
                    ((TextView) findViewById(R.id.wifi_bssid_value)).setText(wifiInfo.getBSSID());
                    ((TextView) findViewById(R.id.wifi_mac_address_value)).setText(String.valueOf(wifiInfo.getMacAddress()));
                    ((TextView) findViewById(R.id.wifi_linkspeed_value)).setText(wifiInfo.getLinkSpeed() + " " + WifiInfo.LINK_SPEED_UNITS);
                    ((TextView) findViewById(R.id.wifi_strength_value)).setText(wifiInfo.getRssi() + " " + getResources().getString(R.string.wifi_strength_unit));
                    ((TextView) findViewById(R.id.wifi_frequency_value)).setText(wifiInfo.getFrequency() + " " + WifiInfo.FREQUENCY_UNITS);
                    ((TextView) findViewById(R.id.wifi_ipaddress_value)).setText(Formatter.formatIpAddress(wifiInfo.getIpAddress()));

                    DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
                    ((TextView) findViewById(R.id.wifi_netmask_value)).setText(Formatter.formatIpAddress(dhcpInfo.netmask));
                    ((TextView) findViewById(R.id.wifi_gateway_value)).setText(Formatter.formatIpAddress(dhcpInfo.gateway));
                    ((TextView) findViewById(R.id.wifi_dhcp_server_value)).setText(Formatter.formatIpAddress(dhcpInfo.serverAddress));
                    ((TextView) findViewById(R.id.wifi_dns_1_value)).setText(Formatter.formatIpAddress(dhcpInfo.dns1));
                    ((TextView) findViewById(R.id.wifi_dns_2_value)).setText(Formatter.formatIpAddress(dhcpInfo.dns2));
                    ((TextView) findViewById(R.id.wifi_dhcp_lease_value)).setText("" + dhcpInfo.leaseDuration);
                    ((TextView) findViewById(R.id.wifi_hidden_ssid_value)).setText(wifiInfo.getHiddenSSID() ? "true" : "false");
                }
            } else {
                ((TextView)findViewById(R.id.wifi_ssid_value)).setText("Not connected");
                ((TextView)findViewById(R.id.wifi_bssid_value)).setText(getResources().getString(R.string.not_available));
                ((TextView)findViewById(R.id.wifi_mac_address_value)).setText(getResources().getString(R.string.not_available));
                ((TextView)findViewById(R.id.wifi_ipaddress_value)).setText(getResources().getString(R.string.not_available));
                ((TextView)findViewById(R.id.wifi_linkspeed_value)).setText(getResources().getString(R.string.not_available));
                ((TextView)findViewById(R.id.wifi_strength_value)).setText(getResources().getString(R.string.not_available));
                ((TextView)findViewById(R.id.wifi_encryption_value)).setText(getResources().getString(R.string.not_available));
                ((TextView)findViewById(R.id.wifi_frequency_value)).setText(getResources().getString(R.string.not_available));
                ((TextView)findViewById(R.id.wifi_ipaddress_value)).setText(getResources().getString(R.string.not_available));
                ((TextView)findViewById(R.id.wifi_netmask_value)).setText(getResources().getString(R.string.not_available));
                ((TextView)findViewById(R.id.wifi_gateway_value)).setText(getResources().getString(R.string.not_available));
                ((TextView)findViewById(R.id.wifi_dhcp_server_value)).setText(getResources().getString(R.string.not_available));
                ((TextView)findViewById(R.id.wifi_dns_1_value)).setText(getResources().getString(R.string.not_available));
                ((TextView)findViewById(R.id.wifi_dns_2_value)).setText(getResources().getString(R.string.not_available));
                ((TextView)findViewById(R.id.wifi_dhcp_lease_value)).setText(getResources().getString(R.string.not_available));
                ((TextView)findViewById(R.id.wifi_hidden_ssid_value)).setText(getResources().getString(R.string.not_available));
            }
        }
    }

    private String getEncryptionTypeString(int encryptionType) {
        String retval = "";
        switch(encryptionType) {
            case WifiConfiguration.KeyMgmt.WPA_PSK:
                retval = getResources().getString(R.string.WPA_PSK);
                break;
            case WifiConfiguration.KeyMgmt.WPA_EAP:
                retval = getResources().getString(R.string.WPA_EAP);
                break;
            case WifiConfiguration.KeyMgmt.IEEE8021X:
                retval = getResources().getString(R.string.IEEE8021X);
                break;
            case WifiConfiguration.KeyMgmt.NONE:
                retval = getResources().getString(R.string.NONE);
                break;
        }
        return retval;
    }
}
