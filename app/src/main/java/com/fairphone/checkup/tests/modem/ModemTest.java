package com.fairphone.checkup.tests.modem;

import android.Manifest;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import community.fairphone.checkup.R;
import com.fairphone.checkup.information.Information;
import com.fairphone.checkup.information.modem.ModemDetails;
import com.fairphone.checkup.information.modem.ModemInformation;
import com.fairphone.checkup.information.modem.SimSlotDetails;
import com.fairphone.checkup.tests.InformationTest;
import com.fairphone.checkup.tests.Test;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;

public class ModemTest extends InformationTest<ModemInformation> {

    public static final Details DETAILS = new Test.Details(R.string.modem_test_title, R.string.modem_test_summary, R.string.modem_test_description, R.string.modem_test_permissions_rationale, Manifest.permission.READ_PHONE_STATE) {
        @Override
        public Fragment getFragment() {
            return new ModemTest();
        }
    };

    private List<ViewGroup> mTestSimViews;

    public ModemTest() {
        super();
        mTestSimViews = new ArrayList<>(ModemDetails.NB_SIM_SLOTS);
    }

    @Override
    protected Details getDetails() {
        return DETAILS;
    }

    @Override
    protected void onCreateTest() {
        super.onCreateTest();

        mInstanceInformation = new ModemInformation(getActivity(), new Information.ChangeListener<ModemDetails>() {
            @Override
            public void onChange(ModemDetails details) {
                refreshView(details);
            }
        });
    }

    @Override
    protected void populateContainer(LayoutInflater inflater, ViewGroup container, ViewGroup contentContainer) {
        mTestSimViews.clear();

        final ViewGroup modemView = (ViewGroup) inflater.inflate(R.layout.view_modem_test, contentContainer, false);

        mTestSimViews.add((ViewGroup) inflater.inflate(R.layout.view_modem_test_sim, modemView, false));
        mTestSimViews.add((ViewGroup) inflater.inflate(R.layout.view_modem_test_sim, modemView, false));

        ViewGroup testSimView;
        SimSlotDetails simSlotDetails;

        for (int slotIndex = 0; slotIndex < ModemDetails.NB_SIM_SLOTS; slotIndex++) {
            testSimView = mTestSimViews.get(slotIndex);
            simSlotDetails = mInstanceInformation.getDetails(false).getSimSlotDetails(slotIndex);

            ((TextView) testSimView.findViewById(R.id.modem_imei_value)).setText(simSlotDetails.getImei());
        }

        modemView.addView(mTestSimViews.get(0));
        modemView.addView(mTestSimViews.get(1));
        contentContainer.addView(modemView);
    }

    private void refreshView(ModemDetails modemDetails) {
        ViewGroup testSimView;
        SimSlotDetails simSlotDetails;
        String simTitle;

        for (int slotIndex = 0; slotIndex < 2; slotIndex++) {
            simSlotDetails = modemDetails.getSimSlotDetails(slotIndex);
            testSimView = mTestSimViews.get(slotIndex);

            switch (simSlotDetails.getSimState()) {
                case TelephonyManager.SIM_STATE_ABSENT:
                    simTitle = String.format(getString(R.string.modem_sim_absent_title), slotIndex + 1);
                    break;
                case TelephonyManager.SIM_STATE_READY:
                    if (simSlotDetails.isSimConnectedToNetwork()) {
                        simTitle = String.format(getString(R.string.modem_sim_ready_title), slotIndex + 1);
                    } else {
                        simTitle = String.format(getString(R.string.modem_sim_ready_not_connected_title), slotIndex + 1);
                    }
                    break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                    simTitle = String.format(getString(R.string.modem_sim_pin_required_title), slotIndex + 1);
                    break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                    simTitle = String.format(getString(R.string.modem_sim_puk_required_title), slotIndex + 1);
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                    simTitle = String.format(getString(R.string.modem_sim_network_locked_title), slotIndex + 1);
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                default:
                    simTitle = String.format(getString(R.string.modem_sim_unknown_state_title), slotIndex + 1);
            }
            ((TextView) testSimView.findViewById(R.id.sim_title)).setText(simTitle);

            if (!simSlotDetails.isSimPresent()) {
                // Hide the SIM details
                testSimView.findViewById(R.id.modem_sim_details).setVisibility(View.GONE);
            } else {
                // Show the SIM details
                testSimView.findViewById(R.id.modem_sim_details).setVisibility(View.VISIBLE);

                ((TextView) testSimView.findViewById(R.id.modem_sim_operator_value)).setText(simSlotDetails.getSimOperatorName());
                ((TextView) testSimView.findViewById(R.id.modem_sim_operator_code_value)).setText(simSlotDetails.getSimOperatorCode());
                ((TextView) testSimView.findViewById(R.id.modem_sim_mcc_value)).setText(simSlotDetails.getSimOperatorMCC());
                ((TextView) testSimView.findViewById(R.id.modem_sim_mnc_value)).setText(simSlotDetails.getSimOperatorMNC());

                if (!simSlotDetails.isSimConnectedToNetwork()) {
                    // Hide the connectivity details
                    testSimView.findViewById(R.id.modem_sim_connectivity_details).setVisibility(View.GONE);
                } else {
                    // Show both the SIM details and the connectivity details
                    testSimView.findViewById(R.id.modem_sim_details).setVisibility(View.VISIBLE);
                    testSimView.findViewById(R.id.modem_sim_connectivity_details).setVisibility(View.VISIBLE);

                    ((TextView) testSimView.findViewById(R.id.modem_network_operator_value)).setText(simSlotDetails.getNetworkOperatorName());
                    ((TextView) testSimView.findViewById(R.id.modem_network_operator_code_value)).setText(simSlotDetails.getNetworkOperatorCode());
                    ((TextView) testSimView.findViewById(R.id.modem_network_mcc_value)).setText(simSlotDetails.getNetworkOperatorMCC());
                    ((TextView) testSimView.findViewById(R.id.modem_network_mnc_value)).setText(simSlotDetails.getNetworkOperatorMNC());
                    ((TextView) testSimView.findViewById(R.id.modem_network_type_value)).setText(simSlotDetails.getNetworkTypeName());
                    ((TextView) testSimView.findViewById(R.id.modem_country_code_value)).setText(simSlotDetails.getNetworkCountryCode());
                    ((TextView) testSimView.findViewById(R.id.modem_data_connected_value)).setText(simSlotDetails.isDataConnectedOnNetwork() ? getString(R.string.yes) : getString(R.string.no));
                    ((TextView) testSimView.findViewById(R.id.modem_roaming_value)).setText(simSlotDetails.isRoamingOnNetwork() ? getString(R.string.yes) : getString(R.string.no));
                    ((TextView) testSimView.findViewById(R.id.modem_data_roaming_value)).setText(simSlotDetails.isDataRoamingOnNetwork() ? getString(R.string.yes) : getString(R.string.no));
                }
            }
        }
    }

}
