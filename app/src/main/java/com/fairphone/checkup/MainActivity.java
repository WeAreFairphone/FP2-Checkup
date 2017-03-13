package com.fairphone.checkup;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity implements TestListAdapter.OnClickListener {

    ViewSwitcher mSwitcher;
    ListView mListView;

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

    public void onClick(Test.Details test) {
        mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
        mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_left));

        final FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.test_container, test.getFragment(), getString(R.string.fragment_tag_test))
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

            final FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .remove(fragmentManager.findFragmentByTag(getString(R.string.fragment_tag_test)))
                    .commit();
        }

        super.onBackPressed();
    }

}
