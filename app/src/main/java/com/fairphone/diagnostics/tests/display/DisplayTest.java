package com.fairphone.diagnostics.tests.display;


import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;


public class DisplayTest extends Test {

    private static final String TAG = DisplayTest.class.getSimpleName();

    private ViewFlipper mViewFlipper;
    private RelativeLayout mParentLayout;
    private ActionBar actionBar;

    private int parentPaddingLeft;
    private int parentPaddingTop;
    private int parentPaddingRight;
    private int parentPaddingBottom;

    public DisplayTest(Context context) {
        super(context);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.lcd_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.lcd_test_description;
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
        mViewFlipper.addView(new FullColorTestView(getContext(), 0xffff0000));
        mViewFlipper.addView(new FullColorTestView(getContext(), 0xff00ff00));
        mViewFlipper.addView(new FullColorTestView(getContext(), 0xff0000ff));
        mViewFlipper.addView(new FullColorTestView(getContext(), 0xffffffff));
        mViewFlipper.addView(new FullColorTestView(getContext(), 0xff000000));
        mViewFlipper.addView(new GradientTestView(getContext()));
    }

    private void setTouchHandler() {
        mViewFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewFlipper viewFlipper = (ViewFlipper) v;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int displayedChild = viewFlipper.getDisplayedChild();
                    int childCount = viewFlipper.getChildCount();
                    if (displayedChild == childCount - 1) {
                        onTestSuccess();
                    }
                    viewFlipper.showNext();
                }
                return true;
            }
        });
    }

    @Override
    protected void runTest() {
        switchToFullScreen();
        setTouchHandler();
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

    @Override
    protected void onPrepare() {
        mParentLayout = (RelativeLayout) findViewById(R.id.genericeTesterRelativeLayout);
    }
}
