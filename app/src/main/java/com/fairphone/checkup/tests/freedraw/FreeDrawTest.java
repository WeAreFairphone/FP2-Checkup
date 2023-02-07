package com.fairphone.checkup.tests.freedraw;

import android.view.View;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import community.fairphone.checkup.R;
import com.fairphone.checkup.tests.SimpleTest;

public class FreeDrawTest extends SimpleTest {

    public static final SimpleDetails DETAILS = new SimpleTest.SimpleDetails(R.string.freedraw_test_title, R.string.freedraw_test_summary, R.string.freedraw_test_description, R.string.freedraw_test_instructions) {
        @Override
        public Fragment getFragment() {
            return new FreeDrawTest();
        }
    };

    private ViewFlipper mViewFlipper;

    public FreeDrawTest() {
        super(false);
    }

    @Override
    protected SimpleDetails getDetails() {
        return DETAILS;
    }

    private void switchToFullScreen() {
        // TODO create a fullscreen dialog fragment
        mViewFlipper = new ViewFlipper(getActivity());
        ViewFlipper.LayoutParams layoutParams = new ViewFlipper.LayoutParams(
                ViewFlipper.LayoutParams.WRAP_CONTENT, ViewFlipper.LayoutParams.WRAP_CONTENT);
        mViewFlipper.setLayoutParams(layoutParams);

        mContainer.addView(mViewFlipper);

        ((AppCompatActivity) requireActivity()).getSupportActionBar().hide();
        mViewFlipper.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void addDisplayPatterns() {
        mViewFlipper.addView(new FreeDrawView(getActivity()));
    }

    @Override
    protected void onResumeTest(boolean firstResume) {
        super.onResumeTest(firstResume);

        switchToFullScreen();
        addDisplayPatterns();
    }

    @Override
    protected void onPauseTest() {
        super.onPauseTest();

        ((AppCompatActivity) requireActivity()).getSupportActionBar().show();
        mContainer.removeView(mViewFlipper);
    }

}
