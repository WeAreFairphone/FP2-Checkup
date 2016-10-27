package com.fairphone.checkup;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.fairphone.checkup.modules.Module;
import com.fairphone.checkup.tests.Test;
import com.fairphone.checkup.tests.TestListAdapter;
import com.fairphone.checkup.tests.digitizer.DigitizerTest;
import com.fairphone.checkup.tests.display.DisplayTest;
import com.fairphone.checkup.tests.microphone.PrimaryMicTest;
import com.fairphone.checkup.tests.microphone.SecondaryMicTest;
import com.fairphone.checkup.tests.modem.ModemTest;
import com.fairphone.checkup.tests.speaker.EarSpeakerTest;
import com.fairphone.checkup.tests.speaker.RearSpeakerTest;
import com.fairphone.checkup.tests.vibrator.VibratorTest;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements TestListAdapter.OnClickListener {
    ViewSwitcher mSwitcher;
    ListView mListView;
    List<Test> mTests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createTestList();
        prepareView();
    }

    private void prepareView() {
        setContentView(R.layout.activity_main);
        mSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(new TestListAdapter(this, mTests, this));
    }

    private void createTestList() {
        mTests = new ArrayList<>();
        mTests.add(new DisplayTest(this));
        mTests.add(new PrimaryMicTest(this));
        mTests.add(new SecondaryMicTest(this));
        mTests.add(new ModemTest(this));
        mTests.add(new EarSpeakerTest(this));
        mTests.add(new RearSpeakerTest(this));
        mTests.add(new VibratorTest(this));
    }

    public void onClick(Test test) {
        try {
            mSwitcher.removeViewAt(1);
        } catch (Exception e) {
        }
        mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_right));
        mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_left));
        mSwitcher.addView(test, 1);
        mSwitcher.setDisplayedChild(1);
    }

    @Override
    public void onBackPressed() {
        if (mSwitcher.getDisplayedChild() > 0) {
            mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
            mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
            mSwitcher.setDisplayedChild(0);
            ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }
}
