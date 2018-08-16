package com.fairphone.checkup.information.wifi;

import android.content.Context;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.util.Log;

import community.fairphone.checkup.R;
import com.fairphone.checkup.information.Information;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A plain old Java object holding the Wi-FI network information.
 * <p>At creation, the Wi-Fi network is not connected to a network ({@link #isConnected()} and the network information are not available ({@link R.string#not_available}).</p>
 * <p>The public methods {@link #setDisabled()} and {@link #setEnabled(WifiInfo, LinkProperties)}} should be used to update an instance status.</p>
 */
public class WifiDetails extends Information.Details {

    private static final String TAG = WifiDetails.class.getSimpleName();

    private final Context mContext;
    private final String mDataNotAvailableValue;
    private final String mWifiNetworkInterface;

    private WifiInfo mWifiInfo;
    private LinkProperties mLinkProperties;

    private String mMacAddress;
    private String mIpAddress;
    private String mDnsServers;

    public WifiDetails(Context context) {
        mContext = context;
        mDataNotAvailableValue = mContext.getString(R.string.not_available);
        mWifiNetworkInterface = mContext.getString(R.string.wifi_network_interface);

        setDisabled();
    }

    /**
     * Set the Wi-Fi feature.
     * <p>Use this method when the Wi-Fi is enabled.<br>
     * Some of the network-related fields are set (such as the MAC address) even when there is no active connection.</p>
     */
    public void setEnabled(WifiInfo wifiInfo, LinkProperties linkProperties) {
        mWifiInfo = wifiInfo;
        mLinkProperties = linkProperties;

        retrieveMacAddress();
        retrieveIpAddresses();
    }

    /**
     * Reset the Wi-Fi feature.
     * <p>Use this method when the Wi-Fi is disabled.<br>
     * The network-related and DHCP-related fields are <strong>also</strong> reset by this method.</p>
     */
    public void setDisabled() {
        setEnabled(null, null);
    }

    public boolean isEnabled() {
        return null != mWifiInfo;
    }

    private void retrieveMacAddress() {
        if (mWifiInfo == null || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            /*
             * Either the interface is down or either we are running on Marshmallow and the MAC
             * address is hidden.
             */

            try {
                for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                    if (!mWifiNetworkInterface.equals(networkInterface.getName())) {
                        continue;
                    }

                    final byte[] macAddressBytes = networkInterface.getHardwareAddress();
                    if (macAddressBytes == null) {
                        mMacAddress = mDataNotAvailableValue;
                        break;
                    } else {
                        final StringBuilder builder = new StringBuilder();
                        for (byte b : macAddressBytes) {
                            builder.append(String.format("%02x:", b));
                        }

                        if (builder.length() > 0) {
                            builder.deleteCharAt(builder.length() - 1);
                        }
                        mMacAddress = builder.toString();
                    }
                }
            } catch (SocketException e) {
                Log.e(TAG, e.getLocalizedMessage());
                mMacAddress = mDataNotAvailableValue;
            }
        } else if (mWifiInfo != null) {
            mMacAddress = mWifiInfo.getMacAddress();
        }
    }

    private void retrieveIpAddresses() {
        mIpAddress = mLinkProperties == null ? mDataNotAvailableValue : formatIpAddresses(mLinkProperties.getLinkAddresses());
        mDnsServers = mLinkProperties == null ? mDataNotAvailableValue : formatIpAddresses(mLinkProperties.getDnsServers());
    }

    /**
     * @param addresses The list of objects ({@link InetAddress} or {@link LinkAddress}) to retrieve a human-readable form from.
     * @return A human-readable string of IP addresses (comma-separated).
     */
    private String formatIpAddresses(List<?> addresses) {
        if (addresses == null || addresses.size() == 0) {
            return mDataNotAvailableValue;
        }

        final Iterator<?> iter = addresses.iterator();
        final StringBuilder builder = new StringBuilder();
        Object element;
        while (iter.hasNext()) {
            element = iter.next();

            if (element instanceof InetAddress) {
                builder.append(((InetAddress) element).getHostAddress());
                if (iter.hasNext()) {
                    builder.append(", ");
                }
            } else if (element instanceof LinkAddress) {
                builder.append(element);
                if (iter.hasNext()) {
                    builder.append(", ");
                }
            }
            // We do not handle other types
        }

        return builder.toString();
    }

    public WifiInfo getWifiInfo() {
        return mWifiInfo;
    }

    public LinkProperties getLinkProperties() {
        return mLinkProperties;
    }

    /**
     * Rely on the link properties to determine if there is an active connection.
     *
     * @return Whether there is an active connection to a Wi-Fi network.
     */
    public boolean isConnected() {
        return null != getLinkProperties();
    }

    public String getMacAddress() {
        return mMacAddress;
    }

    public String getIpAddress() {
        return mIpAddress;
    }

    public String getDnsServers() {
        return mDnsServers;
    }

}
