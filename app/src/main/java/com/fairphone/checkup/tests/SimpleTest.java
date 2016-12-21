package com.fairphone.checkup.tests;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fairphone.checkup.R;

/**
 * A simple {@link Test} that implements an action button and displays instructions.
 * <p>The action button is used to begin (and potentially cancel) the test.</p>
 * <p>If the test is cancellable, the action button will display a cancel label while the test is running.
 * If not cancellable, the action button will remain inactive until completion of the test.</p>
 */
public abstract class SimpleTest extends Test {

    private Button mActionButton;
    private final View.OnClickListener mActionClickLister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mActionButton) {
                if (isCancellable() && isRunning()) {
                    cancelTest();
                } else if (!isRunning()) {
                    beginTest();
                }
            }
        }
    };

    protected ViewGroup mContainer;

    protected SimpleTest(boolean isCancellable) {
        super(isCancellable);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_simple_test, container, false);
        final TextView descriptionView = (TextView) root.findViewById(R.id.test_description);
        final TextView instructionsView = (TextView) root.findViewById(R.id.test_instructions);
        mActionButton = (Button) root.findViewById(R.id.test_action);

        descriptionView.setText(getDetails().getDescription(getActivity()));
        instructionsView.setText(getDetails().getInstructions(getActivity()));
        mActionButton.setOnClickListener(mActionClickLister);

        mContainer = container;

        return root;
    }

    @Override
    protected void onResumeTest(boolean firstResume) {
        super.onResumeTest(firstResume);

        if (firstResume) {
            if (isCancellable()) {
                mActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.accent)));
                mActionButton.setText(R.string.action_cancel_test);
            } else {
                mActionButton.setEnabled(false);
            }
        }
    }

    @Override
    protected void onFinishTest() {
        super.onFinishTest();

        if (isCancellable()) {
            mActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary)));
            mActionButton.setText(R.string.action_begin_test);
        }

        mActionButton.setEnabled(true);
    }

}
