package com.fairphone.checkup.tests.lcd;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

/**
 * Created by dirk on 13-10-15.
 */
public class FullColorTestView extends View {

    private int mColor;

    public FullColorTestView(Context context, int color) {
        super(context);
        mColor = color;
        setBackgroundColor(mColor);
    }

    protected void onDraw(Canvas canvas) {
        setWillNotDraw(false);
    }

}

