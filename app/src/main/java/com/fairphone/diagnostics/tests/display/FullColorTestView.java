package com.fairphone.diagnostics.tests.display;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.View;

/**
 * Created by dirk on 13-10-15.
 */
public class FullColorTestView extends View {
    private ShapeDrawable mDrawable;
    private int mColor;

    public FullColorTestView(Context context, int color) {
        super(context);
        mColor = color;
    }

    protected void onDraw(Canvas canvas) {
        setWillNotDraw(false);
        mDrawable = new ShapeDrawable(new RectShape());
        mDrawable.getPaint().setColor(mColor);
        mDrawable.setBounds(0, 0, 1080, 1920);
        mDrawable.draw(canvas);
    }

}

