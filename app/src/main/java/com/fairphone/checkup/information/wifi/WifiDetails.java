package com.fairphone.checkup.information.wifi;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.fairphone.checkup.R;
import com.fairphone.checkup.information.Information;

public class WifiDetails extends Information.Details {

    private static final String NO_BSSID = "00:00:00:00:00:00";
    private static final String SIGNAL_STRENGTH_UNIT = "dBm";

    private final Context mContext;
    private final String mDataNotAvailableValue;

    private WifiInfo mWifiInfo;
    private DhcpInfo mDhcpInfo;

    public WifiDetails(Context context) {
        mContext = context;
        mDataNotAvailableValue = mContext.getString(R.string.not_available);

        setDisabled();
    }

    /**
     * Set the Wi-Fi feature.
     * <p>Use this method when the Wi-Fi is enabled.<br>
     * Some of the network-related fields are set (such as the MAC address) even when there is no active connection.</p>
     */
    public void setEnabled(WifiInfo wifiInfo, DhcpInfo dhcpInfo) {
        mWifiInfo = wifiInfo;
        mDhcpInfo = dhcpInfo;
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

    /**
     * Rely on the BSSID to determine if there is an active connection.
     * <p>We do not want to rely on the SSID as it can be hidden or empty and the decodeing layer
     * does not yield a consistent result.</p>
     *
     * @return Whether there is an active connection to a Wi-Fi network.
     */
    public boolean isConnected() {
        return null != mWifiInfo && null != mWifiInfo.getBSSID() && !NO_BSSID.equals(mWifiInfo.getBSSID());
    }

    public String getSsid() {
        return (null == mWifiInfo) ? mDataNotAvailableValue : mWifiInfo.getSSID();
    }

    /**
     * @return Whether the connected SSID is hidden or <code>false</code> if not connected.
     */
    public boolean isSsidHidden() {
        return null != mWifiInfo && mWifiInfo.getHiddenSSID();
    }

    public String getBssid() {
        return (null == mWifiInfo) ? mDataNotAvailableValue : mWifiInfo.getBSSID();
    }

    public String getMacAddress() {
        return (null == mWifiInfo) ? mDataNotAvailableValue : mWifiInfo.getMacAddress();
    }

    /**
     * @return The WiFi RSSI or <code>0</code> if not connected.
     * @see #getSignalStrengthUnit()
     */
    public int getSignalStrength() {
        return (null == mWifiInfo) ? 0 : mWifiInfo.getRssi();
    }

    public String getSignalStrengthUnit() {
        return SIGNAL_STRENGTH_UNIT;
    }

    /**
     * @return The signal level from <code>0</code> (bad connection or not connected) to <code>100</code>.
     * @see WifiManager#calculateSignalLevel(int, int)
     */
    public int getSignalLevel() {
        return (null == mWifiInfo) ? 0 : WifiManager.calculateSignalLevel(mWifiInfo.getRssi(), 100);
    }

    /**
     * @return The link speed or <code>0</code> if not connected.
     * @see #getLinkSpeedUnit()
     */
    public int getLinkSpeed() {
        return (null == mWifiInfo) ? 0 : mWifiInfo.getLinkSpeed();
    }

    public String getLinkSpeedUnit() {
        return WifiInfo.LINK_SPEED_UNITS;
    }

    /**
     * @return The frequency or <code>0</code> if not connected.
     * @see #getFrequencyUnit()
     */
    public int getFrequency() {
        return (null == mWifiInfo) ? 0 : mWifiInfo.getFrequency();
    }

    public String getFrequencyUnit() {
        return WifiInfo.FREQUENCY_UNITS;
    }

    public String getIpAddress() {
        return (null == mDhcpInfo) ? mDataNotAvailableValue : Formatter.formatIpAddress(mDhcpInfo.ipAddress);
    }

    public String getGatewayAddress() {
        return (null == mDhcpInfo) ? mDataNotAvailableValue : Formatter.formatIpAddress(mDhcpInfo.gateway);
    }

    public String getNetmask() {
        return (null == mDhcpInfo) ? mDataNotAvailableValue : Formatter.formatIpAddress(mDhcpInfo.netmask);
    }

    public String getDns1Address() {
        return (null == mDhcpInfo) ? mDataNotAvailableValue : Formatter.formatIpAddress(mDhcpInfo.dns1);
    }

    public String getDns2Address() {
        return (null == mDhcpInfo) ? mDataNotAvailableValue : Formatter.formatIpAddress(mDhcpInfo.dns2);
    }

    public String getServerAddress() {
        return (null == mDhcpInfo) ? mDataNotAvailableValue : Formatter.formatIpAddress(mDhcpInfo.serverAddress);
    }

    /**
     * @return The DHCP lease duration in seconds or <code>0</code> if not connected.
     */
    public int getLeaseDuration() {
        return (null == mDhcpInfo) ? 0 : mDhcpInfo.leaseDuration;
    }

}
