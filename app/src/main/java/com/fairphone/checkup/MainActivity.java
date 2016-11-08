package com.fairphone.checkup;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import com.fairphone.checkup.tests.NewTest;
import com.fairphone.checkup.tests.TestListAdapter;
import com.fairphone.checkup.tests.lcd.LcdTest;

import java.util.ArrayList;

public class MainActivity extends Activity implements TestListAdapter.OnClickListener {
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

        final ArrayList<NewTest.Details> displayTests = new ArrayList<>();
        displayTests.add(LcdTest.DETAILS);

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(new TestListAdapter(this, displayTests, this));
    }

    public void onClick(NewTest.Details test) {
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
