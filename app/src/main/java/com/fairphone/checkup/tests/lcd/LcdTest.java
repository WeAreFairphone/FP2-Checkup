package com.fairphone.checkup.tests.lcd;


import android.view.MotionEvent;
import android.view.View;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import community.fairphone.checkup.R;
import com.fairphone.checkup.tests.SimpleTest;


public class LcdTest extends SimpleTest {

    public static final SimpleDetails DETAILS = new SimpleTest.SimpleDetails(R.string.lcd_test_title, R.string.lcd_test_summary, R.string.lcd_test_description, R.string.lcd_test_instructions) {
        @Override
        public Fragment getFragment() {
            return new LcdTest();
        }
    };

    private ViewFlipper mViewFlipper;

    public LcdTest() {
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

        ((AppCompatActivity) requireActivity()).getSupportActionBar().hide();
        mViewFlipper.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
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

        ((AppCompatActivity) requireActivity()).getSupportActionBar().show();
        mContainer.removeView(mViewFlipper);
    }
}
