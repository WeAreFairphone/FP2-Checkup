package com.fairphone.checkup.tests.lcd;


import android.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ViewFlipper;

import com.fairphone.checkup.R;
import com.fairphone.checkup.tests.Test;
import com.fairphone.checkup.tests.SimpleTest;


public class LcdTest extends SimpleTest {

    public static final Details DETAILS = new Test.Details(R.string.lcd_test_title, R.string.lcd_test_summary, R.string.lcd_test_description, R.string.lcd_test_instructions) {
        @Override
        public Fragment getFragment() {
            return new LcdTest();
        }
    };

    private ViewFlipper mViewFlipper;

    private int parentPaddingLeft;
    private int parentPaddingTop;
    private int parentPaddingRight;
    private int parentPaddingBottom;

    public LcdTest() {
        super(false);
    }

    @Override
    protected Details getDetails() {
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
        mViewFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ViewFlipper viewFlipper = (ViewFlipper) v;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int displayedChild = viewFlipper.getDisplayedChild();
                    int childCount = viewFlipper.getChildCount();
                    if (displayedChild == childCount - 1) {
                        finishTest(true);
                    }
                    viewFlipper.showNext();
                }
                return true;
            }
        });

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
        mViewFlipper.addView(new FullColorTestView(getActivity(), 0xffffffff));
        mViewFlipper.addView(new FullColorTestView(getActivity(), 0xff000000));
        mViewFlipper.addView(new FullColorTestView(getActivity(), 0xffff0000));
        mViewFlipper.addView(new FullColorTestView(getActivity(), 0xff00ff00));
        mViewFlipper.addView(new FullColorTestView(getActivity(), 0xff0000ff));
        mViewFlipper.addView(new GradientTestView(getActivity()));
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
