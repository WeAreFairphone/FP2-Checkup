package com.fairphone.checkup.information;

import android.content.Context;

/**
 * Abstract class to define a standalone information retriever class.
 */
public abstract class Information<InstanceDetails extends Information.Details> {

    public static class Details {
    }

    public interface ChangeListener<InstanceDetails> {
        void onChange(InstanceDetails details);
    }

    protected final String TAG = getClass().getSimpleName();

    protected final Context mContext;
    protected final ChangeListener<InstanceDetails> mChangeListener;
    protected InstanceDetails mInstanceDetails;

    public Information(Context context, ChangeListener<InstanceDetails> listener, InstanceDetails instanceDetails) {
        mContext = context;
        mChangeListener = listener;
        mInstanceDetails = instanceDetails;
    }

    protected void refreshDetails(boolean notifyChangeListener) {
        onRefreshDetails();

        if (notifyChangeListener) {
            mChangeListener.onChange(mInstanceDetails);
        }
    }

    protected abstract void onRefreshDetails();

    /**
     * Set up any potential listeners and receivers for the underlying information system.
     * <p>Override this method to register and subscribe to events for the underlying information system.</p>
     */
    public abstract void setUp();

    /**
     * Tear down the listeners and receivers for the underlying information system.
     * <p>Override this method to un-register and un-subscribe to events for the underlying information system.</p>
     */
    public abstract void tearDown();

    /**
     * Get the information details.
     * <p>Instead of polling the instance details with this method, rely on the {@link ChangeListener} instance instead to get notified whenever a change happens.</p>
     *
     * @param forceRefresh Whether the instance details should be refreshed right now instead of relying on the listeners and receivers.
     * @return The information details.
     */
    public InstanceDetails getDetails(boolean forceRefresh) {
        if (forceRefresh) {
            refreshDetails(false);
        }

        return mInstanceDetails;
    }

}
