package com.fairphone.checkup.tests.lcd;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.fairphone.checkup.R;

/**
 * Created by Maarten on 20-10-16.
 */
public class GradientTestView extends View {

    private Drawable gradient;

    public GradientTestView(Context context) {
        super(context);
        gradient = getResources().getDrawable(R.drawable.gradient, null);
        setBackground(gradient);
    }

    protected void onDraw(Canvas canvas) {
        setWillNotDraw(false);
    }
}

