package com.fairphone.checkup.information.modem;


import android.content.Context;
import android.telephony.TelephonyManager;

import com.fairphone.checkup.R;

/**
 * A plain old Java object holding a SIM slot information.
 * <p>At creation, the SIM slot is not connected to a network ({@link #isSimConnectedToNetwork()} and the SIM slot information are not available ({@link R.string#not_available}).</p>
 * <p>The public methods {@link #setImei(String)}, {@link #setSimNotPresent()}, {@link #setSimPresent(int, String, String)}, {@link #setSimNotConnectedToNetwork(int)}, and {@link #setSimConnectedToNetwork(int, String, String, String, String, boolean, boolean, boolean)} should be used to update an instance status.</p>
 */
public class SimSlotDetails {

    private final Context mContext;
    private final String mDataNotAvailableValue;

    private String mImei;
    private int mSimState;
    private String mSimOperatorName;
    private String mSimOperatorCode;
    private String mNetworkOperatorName;
    private String mNetworkOperatorCode;
    private String mNetworkTypeName;
    private String mNetworkCountryCode;
    private boolean mIsDataConnectedOnNetwork;
    private boolean mIsRoamingOnNetwork;
    private boolean mIsDataRoamingOnNetwork;

    public SimSlotDetails(Context context) {
        mContext = context;
        mDataNotAvailableValue = mContext.getString(R.string.not_available);

        setImei(mDataNotAvailableValue);
        setSimNotPresent();
    }

    public void setImei(String imei) {
        mImei = imei;
    }

    /**
     * Set the SIM-related fields.
     * <p>Use this method when a SIM is present (or inserted).<br>
     * The network-related fields are <strong>not</strong> reset by this method.</p>
     *
     * @param simState        The SIM state from {@link TelephonyManager} (see the #SIM_STATE_* constants).
     * @param simOperatorName The SIM operator name (as displayed to the end-user).
     * @param simOperatorCode The SIM operator code (MCC+MNC).
     */
    public void setSimPresent(int simState, String simOperatorName, String simOperatorCode) {
        mSimState = simState;
        mSimOperatorName = simOperatorName;
        mSimOperatorCode = simOperatorCode;
    }

    /**
     * Reset the SIM-related fields.
     * <p>Use this method when a SIM is absent (or removed).<br>
     * The network-related fields are <strong>also</strong> reset by this method.</p>
     *
     * @see #setSimNotConnectedToNetwork(int)
     */
    public void setSimNotPresent() {
        mSimOperatorName = mDataNotAvailableValue;
        mSimOperatorCode = mDataNotAvailableValue;

        setSimNotConnectedToNetwork(TelephonyManager.SIM_STATE_ABSENT);
    }

    /**
     * Set the network-related fields.
     * <p>Use this method when a SIM is not connected to a network anymore.<br>
     * The SIM operator name and code are <strong>not</strong> reset by this method.</p>
     */
    public void setSimConnectedToNetwork(int simState, String networkOperatorName, String networkOperatorCode, String networkTypeName, String networkCountryCode, boolean isDataConnectedOnNetwork, boolean isRoamingOnNetwork, boolean isDataRoamingOnNetwork) {
        mSimState = simState;
        mNetworkOperatorName = networkOperatorName;
        mNetworkOperatorCode = networkOperatorCode;
        mNetworkTypeName = networkTypeName;
        mNetworkCountryCode = networkCountryCode;
        mIsDataConnectedOnNetwork = isDataConnectedOnNetwork;
        mIsRoamingOnNetwork = isRoamingOnNetwork;
        mIsDataRoamingOnNetwork = isDataRoamingOnNetwork;
    }

    /**
     * Reset the network-related fields.
     * <p>use this method when a SIM is not connected to a network anymore.<br>
     * The SIM operator name and code are <strong>not</strong> reset by this method.</p>
     */
    public void setSimNotConnectedToNetwork(int simState) {
        mSimState = simState;
        mNetworkOperatorName = mDataNotAvailableValue;
        mNetworkOperatorCode = mDataNotAvailableValue;
        mNetworkTypeName = mDataNotAvailableValue;
        mNetworkCountryCode = mDataNotAvailableValue;
        mIsDataConnectedOnNetwork = false;
        mIsRoamingOnNetwork = false;
        mIsDataRoamingOnNetwork = false;
    }

    public String getImei() {
        return mImei;
    }

    public boolean isSimPresent() {
        return TelephonyManager.SIM_STATE_ABSENT != mSimState;
    }

    public int getSimState() {
        return mSimState;
    }

    public String getSimOperatorName() {
        return mSimOperatorName;
    }

    public String getSimOperatorCode() {
        return mSimOperatorCode;
    }

    public String getSimOperatorMCC() {
        if (mDataNotAvailableValue.equals(mSimOperatorCode)) {
            return mDataNotAvailableValue;
        } else {
            return mSimOperatorCode.substring(0, 3);
        }
    }

    public String getSimOperatorMNC() {
        if (mDataNotAvailableValue.equals(mSimOperatorCode)) {
            return mDataNotAvailableValue;
        } else {
            return mSimOperatorCode.substring(3);
        }
    }

    public boolean isSimConnectedToNetwork() {
        return TelephonyManager.SIM_STATE_READY == mSimState;
    }

    public String getNetworkOperatorName() {
        return mNetworkOperatorName;
    }

    public String getNetworkOperatorCode() {
        return mNetworkOperatorCode;
    }

    public String getNetworkTypeName() {
        return mNetworkTypeName;
    }

    public String getNetworkCountryCode() {
        return mNetworkCountryCode;
    }

    public String getNetworkOperatorMCC() {
        if (mDataNotAvailableValue.equals(mNetworkOperatorCode)) {
            return mDataNotAvailableValue;
        } else {
            return mNetworkOperatorCode.substring(0, 3);
        }
    }

    public String getNetworkOperatorMNC() {
        if (mDataNotAvailableValue.equals(mNetworkOperatorCode)) {
            return mDataNotAvailableValue;
        } else {
            return mNetworkOperatorCode.substring(3);
        }
    }

    public boolean isDataConnectedOnNetwork() {
        return mIsDataConnectedOnNetwork;
    }

    public boolean isRoamingOnNetwork() {
        return mIsRoamingOnNetwork;
    }

    public boolean isDataRoamingOnNetwork() {
        return mIsDataRoamingOnNetwork;
    }

}
