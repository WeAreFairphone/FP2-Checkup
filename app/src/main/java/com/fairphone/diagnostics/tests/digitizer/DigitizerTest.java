package com.fairphone.diagnostics.tests.digitizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;

/**
 * Created by maarten on 27/09/16.
 */

public class DigitizerTest extends Test {

    private static final String TAG = DigitizerTest.class.getSimpleName();

    View mTestView;

    private MyView mView;
    private RelativeLayout mParentLayout;

    private int parentPaddingLeft;
    private int parentPaddingTop;
    private int parentPaddingRight;
    private int parentPaddingBottom;

    private Paint mPaint;
    private MaskFilter mEmboss;
    private MaskFilter  mBlur;

    public DigitizerTest(Context context) {
        super(context);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.digitizer_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.digitizer_test_description;
    }

    protected void replaceView() {
        mTestView = LayoutInflater.from(getContext()).inflate(R.layout.view_digitizer_test, null);
        setTestView(mTestView);
    }

    @Override
    protected void onPrepare() {
        mParentLayout = (RelativeLayout) findViewById(R.id.genericeTesterRelativeLayout);
    }

    @Override
    protected void runTest() {
        switchToFullScreen();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFF00A5DF);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

        mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 },
                0.4f, 6, 3.5f);
        mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
    }

    @Override
    protected void onCleanUp() {
        restoreParentPadding();
        showStartButton();
        mParentLayout.removeView(mView);
        super.onCleanUp();
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
        mView = new MyView(getContext());
        ViewFlipper.LayoutParams layoutParams = new ViewFlipper.LayoutParams(
                ViewFlipper.LayoutParams.WRAP_CONTENT, ViewFlipper.LayoutParams.WRAP_CONTENT);
        mView.setLayoutParams(layoutParams);
        mParentLayout.addView(mView);

        mView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        removeParentPaddingSave();
        hideStartButton();
    }

    private void hideStartButton() {
        findViewById(R.id.startButton).setVisibility(View.INVISIBLE);
    }

    private void showStartButton() {
        findViewById(R.id.startButton).setVisibility(View.VISIBLE);
    }

    public class MyView extends View {
        private static final float MINP = 0.25f;
        private static final float MAXP = 0.75f;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;

        public MyView(Context c) {
            super(c);
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(0xFFFFFFFF);
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;
        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }
}
