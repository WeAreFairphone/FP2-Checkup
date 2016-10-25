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

public class Pattern4View extends DrawView implements OnTouchListener {

    private static boolean endOfLine;
    private static int failMessage;
    private static boolean helpScreen;
    private static boolean inRange;
    private static boolean isStartCorrect;
    private static boolean isStartCorrect2;
    private static boolean isEndCorrect;
    private static boolean isEndCorrect2;
    public static boolean isPattern3Pass;
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

    public Pattern4View(Context context, ViewFlipper viewFlipper, DigitizerTest test) {
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
        isStartCorrect2 = false;
        isEndCorrect = false;
        isEndCorrect2 = false;
        isPattern3Pass = false;
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
        drawPath(canvas);
        drawPads(canvas, mTolerance);
        drawTouchLine(canvas);
        if (isTouch) {
            drawAxis(canvas);
        }
    }

    protected void drawPath(Canvas canvas) {
        float w = (float) mTolerance;
        Path p = new Path();
        Path p2 = new Path();
        float y2 = (screenHeight / 3.0f) - w;
        float y1 = y2;
        float y4 = (screenHeight / 3.0f) + w;
        float y3 = y4;
        float y6 = ((2.0f * screenHeight) / 3.0f) - w;
        float y5 = y6;
        float y8 = ((2.0f * screenHeight) / 3.0f) + w;
        float y7 = y8;
        float x5 = 0.0f;
        float x4 = 0.0f;
        float x1 = 0.0f;
        float x7 = screenWidth;
        float x6 = x7;
        float x3 = x7;
        float x2 = x7;
        p.moveTo(x1, y1);
        p.lineTo(x2, y2);
        p.lineTo(x3, y3);
        p.lineTo(x4, y4);
        p.lineTo(x1, y1);
        p.close();
        p2.moveTo(x5, y5);
        p2.lineTo(x6, y6);
        p2.lineTo(x7, y7);
        p2.lineTo(0.0f, y8);
        p2.lineTo(x5, y5);
        p2.close();
        canvas.drawPath(p, mPathPaint);
        canvas.drawPath(p2, mPathPaint);
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

    private void drawPads(Canvas canvas, int tolerance2) {
        Paint whiteSquare = new Paint();
        whiteSquare.setColor(getResources().getColor(R.color.colorPrimary));
        whiteSquare.setStyle(Paint.Style.FILL);
        whiteSquare.setAlpha(170);

        float lowerBoundY3 = (screenHeight / 3.0f) - ((float) tolerance2);
        float lowerBoundY1 = lowerBoundY3;
        float lowerBoundY4 = ((2.0f * screenHeight) / 3.0f) - ((float) tolerance2);
        float lowerBoundY2 = lowerBoundY4;
        float upperBoundY3 = (screenHeight / 3.0f) + ((float) tolerance2);
        float upperBoundY1 = upperBoundY3;
        float upperBoundY4 = ((2.0f * screenHeight) / 3.0f) + ((float) tolerance2);
        float upperBoundY2 = upperBoundY4;
        float lowerBoundX2 = 0.0f;
        float lowerBoundX1 = 0.0f;
        float lowerBoundX4 = screenWidth - ((float) ((tolerance2 * 3) / 2));
        float lowerBoundX3 = lowerBoundX4;
        float upperBoundX2 = (float) ((tolerance2 * 3) / 2);
        float upperBoundX1 = upperBoundX2;
        float upperBoundX4 = screenWidth;
        float upperBoundX3 = upperBoundX4;

        canvas.drawRect(lowerBoundX1, lowerBoundY1, upperBoundX1, upperBoundY1, whiteSquare);
        canvas.drawRect(lowerBoundX2, lowerBoundY2, upperBoundX2, upperBoundY2, whiteSquare);
        canvas.drawRect(lowerBoundX3, lowerBoundY3, upperBoundX3, upperBoundY3, whiteSquare);
        canvas.drawRect(lowerBoundX4, lowerBoundY4, upperBoundX4, upperBoundY4, whiteSquare);
    }

    protected boolean inStartPad(Point point, int tolerance) {
        return isInPad(0.0f, (screenHeight / 3.0f) - ((float) tolerance), (float) ((tolerance * 3) / 2), (screenHeight / 3.0f) + ((float) tolerance), point) || isInPad(0.0f, ((2.0f * screenHeight) / 3.0f) - ((float) tolerance), (float) ((tolerance * 3) / 2), ((2.0f * screenHeight) / 3.0f) + ((float) tolerance), point);
    }

    protected boolean inEndPad(Point point, int tolerance) {
        return isInPad(screenWidth - ((float) ((tolerance * 3) / 2)), (screenHeight / 3.0f) - ((float) tolerance), screenWidth, (screenHeight / 3.0f) + ((float) tolerance), point) || isInPad(screenWidth - ((float) ((tolerance * 3) / 2)), ((2.0f * screenHeight) / 3.0f) - ((float) tolerance), screenWidth, ((2.0f * screenHeight) / 3.0f) + ((float) tolerance), point);
    }

    protected boolean inRange(Point point1, Point point2, int w, boolean multiTouch) {
        float lowerBoundY1 = (screenHeight / 3.0f) - ((float) w);
        float upperBoundY1 = (screenHeight / 3.0f) + ((float) w);
        float lowerBoundY2 = ((2.0f * screenHeight) / 3.0f) - ((float) w);
        float upperBoundY2 = ((2.0f * screenHeight) / 3.0f) + ((float) w);
        return ((lowerBoundY1 <= ((float) point1.y) && ((float) point1.y) < upperBoundY1) || (lowerBoundY2 <= ((float) point1.y) && ((float) point1.y) < upperBoundY2)) && ((lowerBoundY1 <= ((float) point2.y) && ((float) point2.y) < upperBoundY1) || (lowerBoundY2 <= ((float) point2.y) && ((float) point2.y) < upperBoundY2));
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
                isStartCorrect2 = false;
                isEndCorrect2 = false;
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
                if (isStartCorrect && isEndCorrect && inRange && isStartCorrect2 && isEndCorrect2) {
                    isPattern3Pass = true;
                    helpScreen = true;

                    int displayedChild = mViewFlipper.getDisplayedChild();
                    int childCount = mViewFlipper.getChildCount();
                    if (displayedChild == childCount - 1) {
                        test.onTestSuccess();
                    }
                    mViewFlipper.showNext();
                }
                if (!(isStartCorrect && isEndCorrect && isStartCorrect2 && isEndCorrect2)) {
                    failMessage = 1;
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
                        mPoints.add(point4);
                    } else {
                        mPoints2.add(point4);
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
                isStartCorrect2 = inStartPad(point7, mTolerance);
                if (isStartCorrect2) {
                    invalidate();
                    break;
                }
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                Point point6 = new Point();
                point6.x = (int) event.getX();
                point6.y = (int) event.getY();
                isEndCorrect2 = inEndPad(point6, mTolerance);
                if (isEndCorrect2) {
                    break;
                }
                break;
        }
        return true;
    }
}
