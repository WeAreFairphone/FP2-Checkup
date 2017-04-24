package com.fairphone.checkup.tests.wifi;

import android.app.Fragment;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fairphone.checkup.R;
import com.fairphone.checkup.information.Information;
import com.fairphone.checkup.information.wifi.WifiDetails;
import com.fairphone.checkup.information.wifi.WifiInformation;
import com.fairphone.checkup.tests.InformationTest;
import com.fairphone.checkup.tests.Test;

import java.util.Locale;

public class WifiTest extends InformationTest<WifiInformation> {

    public static final Test.Details DETAILS = new Test.Details(R.string.wifi_test_title, R.string.wifi_test_summary, R.string.wifi_test_description) {
        @Override
        public Fragment getFragment() {
            return new WifiTest();
        }
    };

    View mTestView;

    public WifiTest() {
        super();
    }

    @Override
    protected Details getDetails() {
        return DETAILS;
    }

    @Override
    protected void onCreateTest() {
        super.onCreateTest();

        mInstanceInformation = new WifiInformation(getActivity(), new Information.ChangeListener<WifiDetails>() {
            @Override
            public void onChange(WifiDetails details) {
                refreshView(details);
            }
        });
    }

    @Override
    protected void populateContainer(LayoutInflater inflater, ViewGroup container, ViewGroup contentContainer) {
        mTestView = inflater.inflate(R.layout.view_wifi_test, contentContainer, false);

        contentContainer.addView(mTestView);
    }

    private void refreshView(WifiDetails wifiDetails) {
        if (!wifiDetails.isEnabled()) {
            ((TextView) mTestView.findViewById(R.id.wifi_title)).setText(getString(R.string.wifi_disabled_title));

            // Hide the Wi-Fi details
            mTestView.findViewById(R.id.wifi_details).setVisibility(View.GONE);
        } else {
            // Show the Wi-Fi details
            mTestView.findViewById(R.id.wifi_details).setVisibility(View.VISIBLE);

            ((TextView) mTestView.findViewById(R.id.wifi_mac_address_value)).setText(String.valueOf(wifiDetails.getMacAddress()));

            if (!wifiDetails.isConnected()) {
                ((TextView) mTestView.findViewById(R.id.wifi_title)).setText(getString(R.string.wifi_not_connected_title));

                // Hide the connectivity details
                mTestView.findViewById(R.id.wifi_connectivity_details).setVisibility(View.GONE);
            } else {
                ((TextView) mTestView.findViewById(R.id.wifi_title)).setText(getString(R.string.wifi_connected_title));

                // Show the connectivity details
                mTestView.findViewById(R.id.wifi_connectivity_details).setVisibility(View.VISIBLE);

                ((TextView) mTestView.findViewById(R.id.wifi_ssid_value)).setText(wifiDetails.getWifiInfo().getHiddenSSID() ? getString(R.string.wifi_ssid_is_hidden) : wifiDetails.getWifiInfo().getSSID());
                ((TextView) mTestView.findViewById(R.id.wifi_bssid_value)).setText(wifiDetails.getWifiInfo().getBSSID());
                ((TextView) mTestView.findViewById(R.id.wifi_link_speed_value)).setText(String.format(Locale.ENGLISH, "%s %s", wifiDetails.getWifiInfo().getLinkSpeed(), WifiInfo.LINK_SPEED_UNITS));
                ((TextView) mTestView.findViewById(R.id.wifi_strength_value)).setText(String.format(Locale.ENGLISH, "%s dbM (%d%%)", wifiDetails.getWifiInfo().getRssi(), WifiManager.calculateSignalLevel(wifiDetails.getWifiInfo().getRssi(), 100)));
                ((TextView) mTestView.findViewById(R.id.wifi_frequency_value)).setText(String.format(Locale.ENGLISH, "%s %s", wifiDetails.getWifiInfo().getFrequency(), WifiInfo.FREQUENCY_UNITS));
                ((TextView) mTestView.findViewById(R.id.wifi_ip_address_value)).setText(wifiDetails.getIpAddress());
                ((TextView) mTestView.findViewById(R.id.wifi_dns_servers_value)).setText(wifiDetails.getDnsServers());
            }
        }
    }

}
