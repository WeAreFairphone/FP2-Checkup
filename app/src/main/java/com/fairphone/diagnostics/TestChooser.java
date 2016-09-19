package com.fairphone.diagnostics;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.fairphone.diagnostics.Module.Module;
import com.fairphone.diagnostics.tests.Test;
import com.fairphone.diagnostics.tests.TestListAdapter;
import com.fairphone.diagnostics.tests.ambientlight.AmbientLightTest;
import com.fairphone.diagnostics.tests.camera.CameraTest;
import com.fairphone.diagnostics.tests.camera.FrontCameraTest;
import com.fairphone.diagnostics.tests.display.DisplayTest;
import com.fairphone.diagnostics.tests.proximity.ProximityTest;
import com.fairphone.diagnostics.tests.vibrationmotor.VibrationMotorTest;

import java.util.ArrayList;
import java.util.List;

public class TestChooser extends Activity implements TestListAdapter.OnClickListener {
    ViewSwitcher mSwitcher;
    ListView mListView;
    List<Test> mTests;
    Module mModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModule = getModule();
        createTestList();
        prepareView();
    }

    private Module getModule() {
        return (Module) getIntent().getSerializableExtra("Module");
    }

    private void prepareView() {
        setContentView(R.layout.activity_test_chooser);
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
        tests.add(new VibrationMotorTest(this));
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
