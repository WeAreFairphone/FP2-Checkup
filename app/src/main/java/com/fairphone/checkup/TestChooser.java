package com.fairphone.checkup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.fairphone.checkup.modules.Module;
import com.fairphone.checkup.tests.Test;
import com.fairphone.checkup.tests.TestListAdapter;
import com.fairphone.checkup.tests.ambientlight.AmbientLightTest;
import com.fairphone.checkup.tests.camera.CameraTest;
import com.fairphone.checkup.tests.camera.FrontCameraTest;
import com.fairphone.checkup.tests.display.DisplayTest;
import com.fairphone.checkup.tests.proximity.ProximityTest;
import com.fairphone.checkup.tests.vibrator.VibratorTest;

import java.util.ArrayList;
import java.util.List;

public class TestChooser extends AppCompatActivity implements TestListAdapter.OnClickListener {
    ViewSwitcher mSwitcher;
    ListView mListView;
    List<Test> mTests;
    Module mModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_chooser);

        // toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mModule = getModule();
        createTestList();
        prepareView();
    }

    private Module getModule() {
        return (Module) getIntent().getSerializableExtra("Module");
    }

    private void prepareView() {
        mSwitcher = (ViewSwitcher) findViewById(R.id.testChooserViewSwitcher);
        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(new TestListAdapter(this, mTests, this));
        if (mModule != null) {
            ((ImageView) findViewById(R.id.imageView)).setImageResource(mModule.getPictureResourceID());
            ((TextView) findViewById(R.id.modulename)).setText(getString(mModule.getModuleNameID()));
        }
    }

    private void createTestList() {
        if (mModule == null) {
            createDemoTestList();
        } else {
            mTests = mModule.getTestList(this);
        }
    }

    private void createDemoTestList() {
        ArrayList<Test> tests = new ArrayList<>();
        tests.add(new DisplayTest(this));
        tests.add(new ProximityTest(this));
        tests.add(new AmbientLightTest(this));
        tests.add(new VibratorTest(this));
        tests.add(new CameraTest(this));
        tests.add(new FrontCameraTest(this));
        mTests = tests;
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
