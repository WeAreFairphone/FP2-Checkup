package com.fairphone.checkup.tests.display;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import com.fairphone.checkup.R;

/**
 * Created by Maarten on 20-10-16.
 */
public class GradientTestView extends View {

//    private GradientDrawable mDrawable;

    public GradientTestView(Context context) {
        super(context);
//        mDrawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR,
//                new int[] {Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED});
    }

    protected void onDraw(Canvas canvas) {
        setWillNotDraw(false);
//        setBackground(mDrawable);
        setBackground(getResources().getDrawable(R.drawable.gradient));
    }
}

