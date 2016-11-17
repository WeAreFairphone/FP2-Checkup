package com.fairphone.checkup.tests.wifi;

import android.app.Fragment;
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
import com.fairphone.checkup.tests.NewTest;

import java.util.Locale;

public class WifiTest extends InformationTest<WifiInformation> {

    public static final NewTest.Details DETAILS = new NewTest.Details(R.string.wifi_test_title, R.string.wifi_test_summary, R.string.wifi_test_description) {
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

                ((TextView) mTestView.findViewById(R.id.wifi_ssid_value)).setText(wifiDetails.getSsid());
                ((TextView) mTestView.findViewById(R.id.wifi_bssid_value)).setText(wifiDetails.getBssid());
                ((TextView) mTestView.findViewById(R.id.wifi_link_speed_value)).setText(String.format(Locale.ENGLISH, "%s %s", wifiDetails.getLinkSpeed(), wifiDetails.getLinkSpeedUnit()));
                ((TextView) mTestView.findViewById(R.id.wifi_strength_value)).setText(String.format(Locale.ENGLISH, "%s %s (%d%%)", wifiDetails.getSignalStrength(), wifiDetails.getSignalStrengthUnit(), wifiDetails.getSignalLevel()));
                ((TextView) mTestView.findViewById(R.id.wifi_frequency_value)).setText(String.format(Locale.ENGLISH, "%s %s", wifiDetails.getFrequency(), wifiDetails.getFrequencyUnit()));
                ((TextView) mTestView.findViewById(R.id.wifi_ip_address_value)).setText(wifiDetails.getIpAddress());

                ((TextView) mTestView.findViewById(R.id.wifi_netmask_value)).setText(wifiDetails.getNetmask());
                ((TextView) mTestView.findViewById(R.id.wifi_gateway_address_value)).setText(wifiDetails.getGatewayAddress());
                ((TextView) mTestView.findViewById(R.id.wifi_dhcp_server_address_value)).setText(wifiDetails.getServerAddress());
                ((TextView) mTestView.findViewById(R.id.wifi_dns_1_value)).setText(wifiDetails.getDns1Address());
                ((TextView) mTestView.findViewById(R.id.wifi_dns_2_value)).setText(wifiDetails.getDns2Address());
                ((TextView) mTestView.findViewById(R.id.wifi_dhcp_lease_value)).setText(String.format(Locale.ENGLISH, "%d s", wifiDetails.getLeaseDuration()));
                ((TextView) mTestView.findViewById(R.id.wifi_hidden_ssid_value)).setText(wifiDetails.isSsidHidden() ? getString(R.string.yes) : getString(R.string.no));
            }
        }
    }

}
