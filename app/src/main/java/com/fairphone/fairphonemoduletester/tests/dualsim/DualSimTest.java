package com.fairphone.fairphonemoduletester.tests.dualsim;

import android.content.Context;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fairphone.fairphonemoduletester.R;
import com.fairphone.fairphonemoduletester.tests.Test;

import java.util.List;

/**
 * Created by maarten on 4-12-15.
 */
public class DualSimTest extends Test {

    private static final String TAG = "DualSimTest";
    private View mTestView;

    private SubscriptionManager mSubscriptionManager;

    private List<SubscriptionInfo> mSubInfoList;

    public DualSimTest(Context context) {
        super(context);
    }

    protected void replaceView() {
        mTestView = LayoutInflater.from(getContext()).inflate(R.layout.view_dualsim_test, null);
        setTestView(mTestView);
    }

    @Override
    protected void runTest() {
        replaceView();

        mSubscriptionManager = SubscriptionManager.from(getContext());
        mSubInfoList = mSubscriptionManager.getActiveSubscriptionInfoList();

        if(mSubInfoList != null) {
            // there seems to be at least one card present, let's find out

            TextView sim1Presence = (TextView)findViewById(R.id.sim1Presence);
            TextView sim2Presence = (TextView)findViewById(R.id.sim2Presence);

            TextView sim1PhoneNumber = (TextView)findViewById(R.id.sim1PhoneNumber);
            TextView sim2PhoneNumber = (TextView)findViewById(R.id.sim2PhoneNumber);

            TextView sim1Carrier = (TextView)findViewById(R.id.sim1Carrier);
            TextView sim2Carrier = (TextView)findViewById(R.id.sim2Carrier);

            if(mSubInfoList.size() == 1)  {                                                         // there's one card present
                SubscriptionInfo subscriptionInfo = mSubInfoList.get(0);

                if(subscriptionInfo.getSimSlotIndex() == 0) {                                       // the card is in the first slot
                    sim1Presence.setText("Yes");
                    sim1Carrier.setText(subscriptionInfo.getCarrierName());
                    sim1PhoneNumber.setText(subscriptionInfo.getNumber());
                } else if(subscriptionInfo.getSimSlotIndex() == 1) {                                // the card is in the second slot
                    sim2Presence.setText("Yes");
                    sim2Carrier.setText(subscriptionInfo.getCarrierName());
                    sim2PhoneNumber.setText(subscriptionInfo.getNumber());
                }
            }

            if(mSubInfoList.size() == 2)  {                                                         // there's two cards present

                SubscriptionInfo subscriptionInfo1 = mSubInfoList.get(0);

                if(subscriptionInfo1.getSimSlotIndex() == 0) {                                       // the card is in the first slot
                    sim1Presence.setText("Yes");
                    sim1Carrier.setText(subscriptionInfo1.getCarrierName());
                    sim1PhoneNumber.setText(subscriptionInfo1.getNumber());
                } else if(subscriptionInfo1.getSimSlotIndex() == 1) {                                // the card is in the second slot
                    sim2Presence.setText("Yes");
                    sim2Carrier.setText(subscriptionInfo1.getCarrierName());
                    sim2PhoneNumber.setText(subscriptionInfo1.getNumber());
                }

                SubscriptionInfo subscriptionInfo2 = mSubInfoList.get(1);

                if(subscriptionInfo2.getSimSlotIndex() == 0) {                                       // the card is in the first slot
                    sim1Presence.setText("Yes");
                    sim1Carrier.setText(subscriptionInfo2.getCarrierName());
                    sim1PhoneNumber.setText(subscriptionInfo2.getNumber());
                } else if(subscriptionInfo2.getSimSlotIndex() == 1) {                                // the card is in the second slot
                    sim2Presence.setText("Yes");
                    sim2Carrier.setText(subscriptionInfo2.getCarrierName());
                    sim2PhoneNumber.setText(subscriptionInfo2.getNumber());
                }
            }
        }
    }

    @Override
    protected int getTestTitleID() {
        return R.string.dualsim_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.dualsim_test_description;
    }
}

