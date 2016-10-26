package com.fairphone.checkup.tests.digitizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;

import com.fairphone.checkup.R;

import java.util.ArrayList;

/**
 * Created by maarten on 19/10/16.
 */

public abstract class DrawView extends View {

    protected static Point currentLocation;
    ArrayList<Point> mPoints;
    ArrayList<Point> mPoints2;
    protected Paint mPathPaint;
    private Paint mTouchPaint;
    private Paint mAxisPaint;
    public static float screenHeight;
    public static float screenWidth;

    int mTolerance;

    public DrawView(Context context) {
        super(context);
        mPoints = new ArrayList<>();
        mPoints2 = new ArrayList<>();
        mPathPaint = createPathPaint();
        mTouchPaint = createPaint(getResources().getColor(R.color.theme_primary), 2.0f);
        mAxisPaint = createPaint(getResources().getColor(R.color.theme_accent), 2.0f);
        mTolerance = 100;
    }

    protected boolean checkRange(float x, float y, float m, float c1, float c2) {
        float lowerBoundX = ((-y) - c1) / m;
        float upperBoundX = ((-y) - c2) / m;
        return !(lowerBoundX > x || x >= upperBoundX);
    }

    protected int getIndex(MotionEvent event) {
        return (event.getAction() & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
    }

    protected Paint createPaint(int color, float width) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStrokeWidth(width);
        paint.setStrokeCap(Paint.Cap.ROUND);
        return paint;
    }

    protected Paint createPathPaint() {
        Paint pathPaint = new Paint();
        pathPaint.setColor(0xff888888);
        pathPaint.setStyle(Paint.Style.FILL);
        pathPaint.setAlpha(127);
        return pathPaint;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldwidth, int oldheight) {
        screenWidth = (float) width;
        screenHeight = (float) height;
        super.onSizeChanged(width, height, oldwidth, oldheight);
    }

    protected abstract void drawPath(Canvas canvas);

    protected boolean drawTouchLine(Canvas canvas) {
        Path path = new Path();
        Path path2 = new Path();
        boolean first = true;
        boolean first2 = true;
        for (Point point : mPoints) {
            if (first) {
                first = false;
                path.moveTo((float) point.x, (float) point.y);
            } else {
                path.lineTo((float) point.x, (float) point.y);
            }
        }
        canvas.drawPath(path, mTouchPaint);
        for (Point point2 : mPoints2) {
            if (first2) {
                first2 = false;
                path2.moveTo((float) point2.x, (float) point2.y);
            } else {
                path2.lineTo((float) point2.x, (float) point2.y);
            }
        }
        canvas.drawPath(path2, mTouchPaint);
        return true;
    }

    protected void drawAxis(Canvas canvas) {
        Canvas canvas2 = canvas;
        canvas2.drawLine((float) currentLocation.x, (float) 0, (float) currentLocation.x, (float) ((int) screenHeight), this.mAxisPaint);
        int stopX2 = (int) screenWidth;
        canvas2 = canvas;
        canvas2.drawLine((float) 0, (float) currentLocation.y, (float) stopX2, (float) currentLocation.y, this.mAxisPaint);
    }

    protected abstract boolean inStartPad(Point point, int tolerance);

    protected abstract boolean inEndPad(Point point, int tolerance);

    protected abstract boolean inRange(Point point1, Point point2, int w, boolean multiTouch);

    protected boolean isInPad(float lowerBoundX, float lowerBoundY, float upperBoundX, float upperBoundY, Point point) {
        return lowerBoundX <= ((float) point.x) && ((float) point.x) < upperBoundX && lowerBoundY <= ((float) point.y) && ((float) point.y) < upperBoundY;
    }
}

