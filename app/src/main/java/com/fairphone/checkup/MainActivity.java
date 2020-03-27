package com.fairphone.checkup;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import com.fairphone.checkup.tests.Test;
import com.fairphone.checkup.tests.TestListAdapter;
import com.fairphone.checkup.tests.freedraw.FreeDrawTest;
import com.fairphone.checkup.tests.lcd.LcdTest;
import com.fairphone.checkup.tests.loopback.PrimaryMicLoopbackTest;
import com.fairphone.checkup.tests.loopback.SecondaryMicLoopbackTest;
import com.fairphone.checkup.tests.modem.ModemTest;
import com.fairphone.checkup.tests.speaker.EarSpeakerTest;
import com.fairphone.checkup.tests.speaker.LoudSpeakerTest;
import com.fairphone.checkup.tests.vibrator.VibratorTest;
import com.fairphone.checkup.tests.wifi.WifiTest;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity implements TestListAdapter.OnClickListener {

    private static final int MISSING_PERMISSIONS_REQUEST_CODE = 0xBEEF;

    private final DialogInterface.OnClickListener mAskPermissionListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            askForPermission();
        }
    };
    private final DialogInterface.OnClickListener mOpenAppSettingsListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            final Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getString(R.string.app_package)));
            MainActivity.this.startActivity(intent);
        }
    };

    ViewSwitcher mSwitcher;
    ListView mListView;
    Test.Details mPendingTestDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prepareView();
    }

    private void prepareView() {
        setContentView(R.layout.activity_main);
        mSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);

        final ArrayList<Test.Details> displayTests = new ArrayList<>();
        displayTests.add(LcdTest.DETAILS);
        displayTests.add(FreeDrawTest.DETAILS);
        displayTests.add(EarSpeakerTest.DETAILS);
        displayTests.add(LoudSpeakerTest.DETAILS);
        displayTests.add(PrimaryMicLoopbackTest.DETAILS);
        displayTests.add(SecondaryMicLoopbackTest.DETAILS);
        displayTests.add(ModemTest.DETAILS);
        displayTests.add(WifiTest.DETAILS);
        displayTests.add(VibratorTest.DETAILS);

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(new TestListAdapter(this, displayTests, this));
    }

    @Override
    public void onClick(Test.Details test) {
        mPendingTestDetails = test;

        if (mPendingTestDetails.getPermission() == null
                || ActivityCompat.checkSelfPermission(this, mPendingTestDetails.getPermission()) == PackageManager.PERMISSION_GRANTED) {
            switchTo(mPendingTestDetails.getFragment());
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, mPendingTestDetails.getPermission())) {
                displayPermissionsRationale(true);
            } else {
                askForPermission();
            }
        }
    }

    private void askForPermission() {
        ActivityCompat.requestPermissions(this, new String[]{mPendingTestDetails.getPermission()}, MISSING_PERMISSIONS_REQUEST_CODE);
    }

    private void switchTo(Fragment fragment) {
        mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
        mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_left));

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.test_container, fragment, getString(R.string.fragment_tag_test))
                .addToBackStack(null)
                .commit();

        mSwitcher.setDisplayedChild(1);
    }

    // TODO nominal behaviour with the Fragments stack?
    @Override
    public void onBackPressed() {
        if (mSwitcher.getDisplayedChild() > 0) {
            mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
            mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
            mSwitcher.setDisplayedChild(0);

            FragmentManager fragmentManager = getSupportFragmentManager();
            getSupportFragmentManager().beginTransaction()
                    .remove(fragmentManager.findFragmentByTag(getString(R.string.fragment_tag_test)))
                    .commit();
        }

        super.onBackPressed();
    }

    /**
     * Callback received when a permission(s) request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == MISSING_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                switchTo(mPendingTestDetails.getFragment());
            } else {
                displayPermissionsRationale(ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]));
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void displayPermissionsRationale(boolean canAsk) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_missing_permission)
                .setNegativeButton(R.string.action_cancel_test, null);

        if (canAsk) {
            builder.setMessage(mPendingTestDetails.getPermissionRationale(this));
            builder.setPositiveButton(R.string.ok, mAskPermissionListener);
        } else {
            builder.setMessage(mPendingTestDetails.getPermissionRationale(this) + " " + getString(R.string.grant_permission_in_settings));
            builder.setPositiveButton(R.string.settings, mOpenAppSettingsListener);
        }

        builder.show();
    }

}
