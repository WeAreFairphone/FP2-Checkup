package com.fairphone.checkup.tests.digitizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ViewFlipper;

import com.fairphone.checkup.R;

import java.util.ArrayList;

/**
 * Created by maarten on 18/10/16.
 */

public class Pattern2View extends DrawView implements OnTouchListener {

    private static boolean endOfLine;
    private static int failMessage;
    private static boolean helpScreen;
    private static boolean inRange;
    private static boolean isEndCorrect;
    public static boolean isPattern1Pass;
    private static boolean isStartCorrect;
    private static boolean isTouch;
    Bitmap bm;
    Path path;
    Paint textPaint;

    public static final int EPDDRAWINGSTATE_DISABLE_DRAWING = 0;
    public static final int GESTURE1 = 1;
    public static final int GESTURE1_GESTURE2 = 3;
    public static final int GESTURE2 = 2;
    public static final int MAX_GESTURE_ID = 4;

    ViewFlipper mViewFlipper;
    DigitizerTest test;

    public Pattern2View(Context context, ViewFlipper viewFlipper, DigitizerTest test) {
        super(context);
        mViewFlipper = viewFlipper;
        this.test = test;
        this.textPaint = new Paint();
        this.path = new Path();
        this.bm = BitmapFactory.decodeResource(getResources(), R.drawable.guide_empty);

        endOfLine = true;
        currentLocation = new Point();
        isTouch = false;
        inRange = true;
        isStartCorrect = false;
        isEndCorrect = false;
        isPattern1Pass = false;
        helpScreen = true;
        failMessage = -1;

        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawFailMessage(canvas);
//        if ((isPattern1Pass && pattern == 1) || ((isPattern2Pass && pattern == 2) || ((isPattern3Pass && pattern == 3) || (isPattern4Pass && pattern == 4)))) {
//            canvas.drawColor(0xff00ff00);
//        }
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawBitmap(bm, 0.0f, 0.0f, paint);
        drawPads(canvas, mTolerance);
        drawPath(canvas);
        drawTouchLine(canvas);
        if (isTouch) {
            drawAxis(canvas);
        }
    }

    protected void drawPath(Canvas canvas) {
        float w = (float) mTolerance;
        Path p = new Path();
        float x1 = screenWidth - w;
        float y2 = screenHeight - w;
        float x3 = w;
        float y3 = screenHeight;
        float x4 = screenWidth;
        float y4 = w;
        p.moveTo(screenWidth, 0.0f);
        p.lineTo(x1, 0.0f);
        p.lineTo(0.0f, y2);
        p.lineTo(0.0f, screenHeight);
        p.lineTo(x3, y3);
        p.lineTo(x4, y4);
        p.lineTo(screenWidth, 0.0f);
        p.close();
        canvas.drawPath(p, mPathPaint);
    }

    private void drawFailMessage(Canvas canvas) {
        Paint paint2 = new Paint();
        paint2.setColor(-1);
        paint2.setTextAlign(Paint.Align.CENTER);
        paint2.setTextSize(50.0f);
        String s = "Error, please retry";
        if (!inRange && isTouch) {
            canvas.drawColor(0xffff0000);
            canvas.drawText("Are you going out of boundary?", 360.0f, 900.0f, paint2);
        }
        if (failMessage == 1) {
            canvas.drawColor(0xffff0000);
            canvas.drawText(s, 360.0f, 840.0f, paint2);
            canvas.drawText("Not start or end on", 360.0f, 900.0f, paint2);
            canvas.drawText("correct green pad?", 360.0f, 960.0f, paint2);
        }
    }

    private void drawPads(Canvas canvas, int tolerance) {
        Paint whiteSquare = new Paint();
        whiteSquare.setColor(getResources().getColor(R.color.theme_primary));
        whiteSquare.setStyle(Paint.Style.FILL);
        whiteSquare.setAlpha(170);

        float lowerBoundX1 = screenWidth - ((float) tolerance);
        float lowerBoundY1 = 0.0f;
        float upperBoundX1 = screenWidth;
        float upperBoundY1 = (float) tolerance;
        float lowerBoundX2 = 0.0f;
        float lowerBoundY2 = screenHeight - ((float) tolerance);
        float upperBoundX2 = (float) tolerance;
        float upperBoundY2 = screenHeight;

        canvas.drawRect(lowerBoundX1, lowerBoundY1, upperBoundX1, upperBoundY1, whiteSquare);
        canvas.drawRect(lowerBoundX2, lowerBoundY2, upperBoundX2, upperBoundY2, whiteSquare);
    }

    protected boolean inStartPad(Point point, int tolerance) {
        return isInPad(screenWidth - ((float) tolerance), 0.0f, screenWidth, (float) tolerance, point);
    }

    protected boolean inEndPad(Point point, int tolerance) {
        return isInPad(0.0f, screenHeight - ((float) tolerance), (float) tolerance, screenHeight, point);
    }

    protected boolean inRange(Point point1, Point point2, int w, boolean multiTouch) {
        float y2;
        float x3;
        float y3;
        float x4;
        float m;
        float c1;
        float c2;

        float x1 = screenWidth - ((float) w);
        y2 = screenHeight - ((float) w);
        x3 = (float) w;
        y3 = screenHeight;
        x4 = screenWidth;
        float y4 = (float) (-w);
        m = (-(y2 - 0.0f)) / (0.0f - x1);
        c1 = 0.0f - (m * x1);
        c2 = y4 - (m * x4);
        return checkRange((float) point1.x, (float) point1.y, m, c1, c2);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        isTouch = true;
        Point point = currentLocation;
        point.x = (int) event.getX();
        point = currentLocation;
        point.y = (int) event.getY();
        int i = currentLocation.x;
        if (helpScreen) {
            helpScreen = false;
            invalidate();
        }
        int action = MotionEventCompat.getActionMasked(event);
        int index = MotionEventCompat.getActionIndex(event);
        int i2;
        switch (action) {
            case EPDDRAWINGSTATE_DISABLE_DRAWING:
                isStartCorrect = false;
                isEndCorrect = false;
                failMessage = -1;
                Point point2 = new Point();
                point2.x = (int) MotionEventCompat.getX(event, index);
                point2.y = (int) MotionEventCompat.getY(event, index);
                mPoints.add(point2);
                invalidate();
                if (endOfLine) {
                    isStartCorrect = inStartPad(point2, mTolerance);
                    break;
                }
                break;
            case GESTURE1:
                Point point5 = new Point();
                point5.x = (int) event.getX();
                point5.y = (int) event.getY();
                isEndCorrect = inEndPad(point5, mTolerance);
                if (isStartCorrect && isEndCorrect && inRange) {
                    isPattern1Pass = true;
                    helpScreen = true;
                    setVisibility(View.GONE);
                    int displayedChild = mViewFlipper.getDisplayedChild();
                    int childCount = mViewFlipper.getChildCount();
                    if (displayedChild == childCount - 1) {
                        test.onTestSuccess();
                    }
                    mViewFlipper.showNext();
                }
                mPoints = new ArrayList();
                mPoints2 = new ArrayList();
                isTouch = false;
                endOfLine = true;
                inRange = true;
                invalidate();
                break;
            case GESTURE2:
                if (event.getPointerCount() == 1) {
                    Point point1 = new Point();
                    point1.x = (int) MotionEventCompat.getX(event, index);
                    point1.y = (int) MotionEventCompat.getY(event, index);
                    if (inRange) {
                        inRange = inRange(point1, point1, mTolerance, false);
                    }
                }
                if (event.getPointerCount() == 2) {
                    Point point22 = new Point();
                    point22.x = (int) MotionEventCompat.getX(event, 0);
                    point22.y = (int) MotionEventCompat.getY(event, 0);
                    Point point3 = new Point();
                    point3.x = (int) MotionEventCompat.getX(event, 1);
                    point3.y = (int) MotionEventCompat.getY(event, 1);
                    if (inRange) {
                        inRange = inRange(point22, point3, mTolerance, false);
                    }
                }
                for (i2 = 0; i2 < event.getPointerCount(); i2++) {
                    Point point4 = new Point();
                    point4.x = (int) MotionEventCompat.getX(event, i2);
                    point4.y = (int) MotionEventCompat.getY(event, i2);
                    if (event.getPointerId(i2) == 0) {
                        this.mPoints.add(point4);
                    } else {
                        this.mPoints2.add(point4);
                    }
                }
                invalidate();
                break;
            case MotionEventCompat.ACTION_POINTER_DOWN:
                i2 = event.getPointerId(getIndex(event));
                Point point7 = new Point();
                point7.x = (int) MotionEventCompat.getX(event, getIndex(event));
                point7.y = (int) MotionEventCompat.getY(event, getIndex(event));
                mPoints2.add(point7);
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                Point point6 = new Point();
                point6.x = (int) event.getX();
                point6.y = (int) event.getY();
                break;
        }
        return true;
    }
}
