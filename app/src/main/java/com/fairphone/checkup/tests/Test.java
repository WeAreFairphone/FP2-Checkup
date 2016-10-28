package com.fairphone.checkup.tests;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fairphone.checkup.R;

public abstract class Test extends RelativeLayout {

    private View mOldView;
    private Button mButton;

    private boolean hasRun;
    private boolean passed;
    private boolean isRunning;

    public boolean hasRun() {
        return hasRun;
    }

    public boolean didPass() {
        return passed;
    }

    public Test(Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_tester, this);
        TextView title = (TextView) findViewById(R.id.test_title);
        title.setText(getTestTitle());

        TextView description = (TextView) findViewById(R.id.test_description_text);
        description.setText(getTestDescription());
        mButton = (Button) findViewById(R.id.test_button);
        configureStartButton();
        configureBackButton();
        onPrepare();
    }

    private void configureStartButton() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTest();
            }
        });
        mButton.setText("Start Test");
    }

    private void configureCancleButton() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTestFailure();
            }
        });
        mButton.setText("Cancel Test");
    }

    private void configureBackButton() {
        OnKeyListener onKeyListener = new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK && isRunning) {
                    onTestFailure();
                    return true;
                }
                return false;
            }
        };
        setFocusableInTouchMode(true);
        requestFocus();
        setOnKeyListener(onKeyListener);
    }

    protected void askIfSuccess(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(message);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onTestSuccess();
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onTestFailure();
            }
        });
        alert.setCancelable(false);
        alert.show();
    }

    public void onTestSuccess() {
        cleanUp();
        //passed = true;
        //((TextView) findViewById(R.id.testResult)).setText(R.string.test_passed);
        //((TextView) findViewById(R.id.testResult)).setTextColor(0xff00aa00);
    }

    protected void onTestFailure() {
        cleanUp();
        //passed = false;
        //((TextView) findViewById(R.id.testResult)).setText(R.string.test_failed);
        //((TextView) findViewById(R.id.testResult)).setTextColor(0xffaa0000);
    }

    protected void setTestView(View view) {
        FrameLayout layout = (FrameLayout) findViewById(R.id.test_description_layout);
        View oldView = layout.getChildAt(0);
        if (mOldView == null) {
            mOldView = oldView;
        }
        layout.removeView(oldView);
        layout.addView(view);
    }

    protected void cleanUp() {
        isRunning = false;
        hasRun = true;
        if (mOldView != null) {
            setTestView(mOldView);
        }
        configureStartButton();
        onCleanUp();
    }

    private void startTest() {
        mButton.setText("Start Test");
        configureCancleButton();
        isRunning = true;
        runTest();
    }

    protected void onCleanUp() {
    }

    ;

    protected void onPrepare() {
    }

    ;

    /**
     * Implement by actual tests.
     *
     * @return indicates if the run was successful
     */
    protected abstract void runTest();

    /**
     * @return the title of the test.
     */
    protected String getTestTitle() {
        return getResources().getString(getTestTitleID());
    }

    protected int getTestTitleID() {
        return R.string.genericTestTitle;
    }

    /**
     * Returns the TestDescription.
     *
     * @return the description of the test.
     */
    protected String getTestDescription() {
        return getTestDescription(getContext());
    }

    protected String getTestDescription(Context context) {
        return getResources().getString(getTestDescriptionID());
    }

    protected int getTestDescriptionID() {
        return R.string.genericTestDescription;
    }

    ;

}
