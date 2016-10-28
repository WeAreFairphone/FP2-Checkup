package com.fairphone.checkup.tests.freedraw;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.fairphone.checkup.R;
import com.fairphone.checkup.tests.Test;

public class FreeDrawTest extends Test {

    private static final String TAG = FreeDrawTest.class.getSimpleName();

    private ViewFlipper mViewFlipper;
    private RelativeLayout mParentLayout;

    private int parentPaddingLeft;
    private int parentPaddingTop;
    private int parentPaddingRight;
    private int parentPaddingBottom;

    public FreeDrawTest(Context context) {
        super(context);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.freedraw_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.freedraw_test_description;
    }

    @Override
    protected int getTestInstructionsID() {
        return R.string.freedraw_test_instructions;
    }

    @Override
    protected void onPrepare() {
        displayInstructions();
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
        mViewFlipper.addView(new FreeDrawView(getContext()));
    }
}
