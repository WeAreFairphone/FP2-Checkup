package com.fairphone.checkup.tests.digitizer;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.fairphone.checkup.R;
import com.fairphone.checkup.tests.Test;

/**
 * Created by maarten on 27/09/16.
 */

public class DigitizerTest extends Test {

    private static final String TAG = DigitizerTest.class.getSimpleName();

    private ViewFlipper mViewFlipper;
    private RelativeLayout mParentLayout;

    private int parentPaddingLeft;
    private int parentPaddingTop;
    private int parentPaddingRight;
    private int parentPaddingBottom;

    public DigitizerTest(Context context) {
        super(context);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.digitizer_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.digitizer_test_description;
    }

    @Override
    protected void onPrepare() {
        mParentLayout = (RelativeLayout) findViewById(R.id.generic_test_relative_layout);
    }

    @Override
    protected void runTest() {
        switchToFullScreen();
        addDisplayPatterns();
    }

    @Override
    protected void onCleanUp() {
        restoreParentPadding();
        showStartButton();
        mParentLayout.removeView(mViewFlipper);
        super.onCleanUp();
    }

    protected void saveParentPadding() {
        parentPaddingLeft = mParentLayout.getPaddingLeft();
        parentPaddingTop = mParentLayout.getPaddingTop();
        parentPaddingRight = mParentLayout.getPaddingRight();
        parentPaddingBottom = mParentLayout.getPaddingBottom();
    }

    protected void removeParentPaddingSave() {
        saveParentPadding();
        mParentLayout.setPadding(0, 0, 0, 0);
    }

    protected void restoreParentPadding() {
        mParentLayout.setPadding(
                parentPaddingLeft,
                parentPaddingTop,
                parentPaddingRight,
                parentPaddingBottom);
    }

    private void switchToFullScreen() {
        mViewFlipper = new ViewFlipper(getContext());
        ViewFlipper.LayoutParams layoutParams = new ViewFlipper.LayoutParams(
                ViewFlipper.LayoutParams.WRAP_CONTENT, ViewFlipper.LayoutParams.WRAP_CONTENT);
        mViewFlipper.setLayoutParams(layoutParams);
        mParentLayout.addView(mViewFlipper);

        mViewFlipper.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        removeParentPaddingSave();
        hideStartButton();
    }

    private void hideStartButton() {
        findViewById(R.id.test_button).setVisibility(View.INVISIBLE);
    }

    private void showStartButton() {
        findViewById(R.id.test_button).setVisibility(View.VISIBLE);
    }

    private void addDisplayPatterns() {
        mViewFlipper.addView(new Pattern1View(getContext(), mViewFlipper, this));
        mViewFlipper.addView(new Pattern2View(getContext(), mViewFlipper, this));
        mViewFlipper.addView(new Pattern3View(getContext(), mViewFlipper, this));
        mViewFlipper.addView(new Pattern4View(getContext(), mViewFlipper, this));
        mViewFlipper.addView(new FreeDrawView(getContext()));
    }
}
