package com.fairphone.diagnostics.tests.digitizer;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;

/**
 * Created by maarten on 27/09/16.
 */

public class DigitizerTest extends Test {

    static boolean exitTest;

    private static final String TAG = DigitizerTest.class.getSimpleName();

    private ViewFlipper mViewFlipper;
    private RelativeLayout mParentLayout;
    private ActionBar actionBar;

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
        mParentLayout = (RelativeLayout) findViewById(R.id.genericeTesterRelativeLayout);
    }

    @Override
    protected void runTest() {
        switchToFullScreen();
        addDisplayPatterns();
    }

    @Override
    protected void onCleanUp() {
        restoreParentPadding();
        actionBar.show();
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
        AppCompatActivity activity = (AppCompatActivity) getContext();
        actionBar = activity.getSupportActionBar();
        actionBar.hide();
        hideStartButton();
    }

    private void hideStartButton() {
        findViewById(R.id.startButton).setVisibility(View.INVISIBLE);
    }

    private void showStartButton() {
        findViewById(R.id.startButton).setVisibility(View.VISIBLE);
    }

    private void addDisplayPatterns() {
        mViewFlipper.addView(new Pattern1View(getContext(), mViewFlipper, this));
        mViewFlipper.addView(new Pattern2View(getContext(), mViewFlipper, this));
        mViewFlipper.addView(new Pattern3View(getContext(), mViewFlipper, this));
        mViewFlipper.addView(new Pattern4View(getContext(), mViewFlipper, this));
    }
}
