package com.fairphone.checkup.information.modem;

import android.content.Context;

import com.fairphone.checkup.information.Information;

public class ModemDetails extends Information.Details {

    public final static int NB_SIM_SLOTS = 2;

    private final Context mContext;
    private final SimSlotDetails[] mSimSlotDetails;

    public ModemDetails(Context context) {
        mContext = context;
        mSimSlotDetails = new SimSlotDetails[NB_SIM_SLOTS];
    }

    public SimSlotDetails createSimSlotDetails(int slotIndex) {
        mSimSlotDetails[slotIndex] = new SimSlotDetails(mContext);

        return mSimSlotDetails[slotIndex];
    }

    /**
     * @param slotIndex The SIM slot index to look up.
     * @return The SIM slot details or <code>null</code> if no SIM enabled in this slot.
     */
    public SimSlotDetails getSimSlotDetails(int slotIndex) {
        return mSimSlotDetails[slotIndex];
    }

}
