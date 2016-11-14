package com.fairphone.checkup.tests.modem;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fairphone.checkup.R;
import com.fairphone.checkup.information.Information;
import com.fairphone.checkup.information.modem.ModemDetails;
import com.fairphone.checkup.information.modem.ModemInformation;
import com.fairphone.checkup.information.modem.SimSlotDetails;
import com.fairphone.checkup.tests.NewTest;

import java.util.ArrayList;
import java.util.List;

public class ModemTest extends NewTest {

    public static final Details DETAILS = new NewTest.Details(R.string.modem_test_title, R.string.modem_test_summary, R.string.modem_test_description) {
        @Override
        public Fragment getFragment() {
            return new ModemTest();
        }
    };

    private ModemInformation mModemInformation;

    private ViewGroup mContainer;
    private List<ViewGroup> mTestSimViews;

    public ModemTest() {
        super(false);
        mTestSimViews = new ArrayList<>(ModemDetails.NB_SIM_SLOTS);
    }

    @Override
    protected Details getDetails() {
        return DETAILS;
    }

    @Override
    public void onStart() {
        super.onStart();

        beginTest();
    }

    @Override
    protected void onCreateTest() {
        super.onCreateTest();

        mModemInformation = new ModemInformation(getActivity(), new Information.ChangeListener<ModemDetails>() {
            @Override
            public void onChange(ModemDetails details) {
                refreshView(details);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTestSimViews.clear();

        final ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_modem_test, container, false);
        final TextView descriptionView = (TextView) root.findViewById(R.id.test_description);
        final ViewGroup contentContainer = (ViewGroup) root.findViewById(R.id.content_layout);

        mTestSimViews.add((ViewGroup) inflater.inflate(R.layout.view_modem_test_sim, contentContainer, false));
        mTestSimViews.add((ViewGroup) inflater.inflate(R.layout.view_modem_test_sim, contentContainer, false));

        ViewGroup testSimView;
        SimSlotDetails simSlotDetails;

        for (int slotIndex = 0; slotIndex < ModemDetails.NB_SIM_SLOTS; slotIndex++) {
            testSimView = mTestSimViews.get(slotIndex);
            simSlotDetails = mModemInformation.getDetails(false).getSimSlotDetails(slotIndex);

            try {
                ((TextView) testSimView.findViewById(R.id.modem_imei_value)).setText(simSlotDetails.getImei());

                ((TextView) testSimView.findViewById(R.id.modem_sim_operator_value)).setText(simSlotDetails.getSimOperatorName());
                ((TextView) testSimView.findViewById(R.id.modem_sim_operator_code_value)).setText(simSlotDetails.getSimOperatorCode());
                ((TextView) testSimView.findViewById(R.id.modem_sim_mcc_value)).setText(simSlotDetails.getSimOperatorMCC());
                ((TextView) testSimView.findViewById(R.id.modem_sim_mnc_value)).setText(simSlotDetails.getSimOperatorMNC());
            } catch (Throwable e) {
                Log.e(TAG, String.format("Could not retrieve SIM slot #%d IMEI", slotIndex), e);
            }
        }

        descriptionView.setText(getDetails().getDescription(getActivity()));
        contentContainer.removeAllViews();
        contentContainer.addView(mTestSimViews.get(0));
        contentContainer.addView(mTestSimViews.get(1));

        mContainer = container;

        return root;
    }

    @Override
    protected void onResumeTest(boolean firstResume) {
        super.onResumeTest(firstResume);

        mModemInformation.setUp();
    }

    @Override
    protected void onPauseTest() {
        super.onPauseTest();

        mModemInformation.tearDown();
    }

    private void refreshView(ModemDetails modemDetails) {
        ViewGroup testSimView;
        SimSlotDetails simSlotDetails;

        for (int slotIndex = 0; slotIndex < 2; slotIndex++) {
            simSlotDetails = modemDetails.getSimSlotDetails(slotIndex);
            testSimView = mTestSimViews.get(slotIndex);

            if (simSlotDetails == null) {
                ((TextView) testSimView.findViewById(R.id.sim_title)).setText(String.format(getString(R.string.modem_unavailable_sim_title), slotIndex + 1));

                // Hide the connectivity details
                testSimView.findViewById(R.id.modem_sim_connectivity_details).setVisibility(View.GONE);
            } else {
                ((TextView) testSimView.findViewById(R.id.sim_title)).setText(String.format(getString(R.string.modem_sim_title), slotIndex + 1));

                // Show the connectivity details
                testSimView.findViewById(R.id.modem_sim_connectivity_details).setVisibility(View.VISIBLE);

                ((TextView) testSimView.findViewById(R.id.modem_network_operator_value)).setText(simSlotDetails.getNetworkOperatorName());
                ((TextView) testSimView.findViewById(R.id.modem_network_operator_code_value)).setText(simSlotDetails.getNetworkOperatorCode());
                ((TextView) testSimView.findViewById(R.id.modem_network_mcc_value)).setText(simSlotDetails.getNetworkOperatorMCC());
                ((TextView) testSimView.findViewById(R.id.modem_network_mnc_value)).setText(simSlotDetails.getNetworkOperatorMNC());
                ((TextView) testSimView.findViewById(R.id.modem_network_type_value)).setText(simSlotDetails.getNetworkTypeName());
                ((TextView) testSimView.findViewById(R.id.modem_country_code_value)).setText(simSlotDetails.getNetworkCountryCode());
                ((TextView) testSimView.findViewById(R.id.modem_roaming_value)).setText(simSlotDetails.isRoamingOnNetwork() ? getString(R.string.yes) : getString(R.string.no));
                ((TextView) testSimView.findViewById(R.id.modem_data_roaming_value)).setText(simSlotDetails.isDataRoamingOnNetwork() ? getString(R.string.yes) : getString(R.string.no));
            }
        }
    }

}
