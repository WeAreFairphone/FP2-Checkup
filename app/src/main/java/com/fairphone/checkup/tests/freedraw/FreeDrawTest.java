package com.fairphone.checkup.tests.freedraw;

import android.app.Fragment;
import android.view.View;
import android.widget.ViewFlipper;

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

    private int parentPaddingLeft;
    private int parentPaddingTop;
    private int parentPaddingRight;
    private int parentPaddingBottom;

    public FreeDrawTest() {
        super(false);
    }

    @Override
    protected SimpleDetails getDetails() {
        return DETAILS;
    }

    protected void saveParentPadding() {
        parentPaddingLeft = mContainer.getPaddingLeft();
        parentPaddingTop = mContainer.getPaddingTop();
        parentPaddingRight = mContainer.getPaddingRight();
        parentPaddingBottom = mContainer.getPaddingBottom();
    }

    protected void removeParentPaddingSave() {
        saveParentPadding();
        mContainer.setPadding(0, 0, 0, 0);
    }

    protected void restoreParentPadding() {
        mContainer.setPadding(
                parentPaddingLeft,
                parentPaddingTop,
                parentPaddingRight,
                parentPaddingBottom);
    }

    private void switchToFullScreen() {
        // TODO create a fullscreen dialog fragment
        mViewFlipper = new ViewFlipper(getActivity());
        ViewFlipper.LayoutParams layoutParams = new ViewFlipper.LayoutParams(
                ViewFlipper.LayoutParams.WRAP_CONTENT, ViewFlipper.LayoutParams.WRAP_CONTENT);
        mViewFlipper.setLayoutParams(layoutParams);

        mContainer.addView(mViewFlipper);

        mViewFlipper.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        removeParentPaddingSave();
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

        restoreParentPadding();
        mContainer.removeView(mViewFlipper);
    }

}
