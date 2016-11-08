package com.fairphone.checkup.tests;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.io.Serializable;

/**
 * A standalone (abstract) test element that lives as a fragment.
 * <p>The default behaviour for the test strings (title, summary, and description) is to retrieve them from the resource file as-is.
 * To format them in a different way, do override:
 * <ul>
 * <li>{@link Details#getTitle(Context)} that returns the title string</li>
 * <li>{@link Details#getSummary(Context)} that returns the summary string</li>
 * <li>{@link Details#getDescription(Context)} that returns the description string</li>
 * </ul>
 * </p>
 * <p><strong>The call to the parent method </strong>(<code>super.onBeginTest()</code> for instance)<strong> must be included in each overridden method.</strong></p>
 * <p>The {@link #beginTest()}, {@link #cancelTest()}, and {@link #finishTest(boolean)} methods should <strong>not</strong> be overridden.
 * They are the public interface to a test with the status methods ({@link #isFresh()}, {@link #isRunning()}, {@link #isCancelled()}, {@link #isCompleted()}, {@link #hasPassed()}, {@link #hasFailed()}).</p
 * <p>A test inner lifecycle follows the {@link Fragment}'s with some dedicated test callbacks:
 * <ul>
 * <li>{@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} when the test user interface is displayed</li>
 * <li>{@link #onBeginTest()} when the test is begun</li>
 * <li>{@link #onResumeTest()} when the test is resumed (at creation or when the {@link Fragment} is resumed)</li>
 * <li>{@link #onPauseTest()} when the test is paused (at destruction or when the {@link Fragment} is paused)</li>
 * <li>{@link #onCancelTest()} if and when the test is cancelled</li>
 * <li>{@link #onFinishTest()} when the test is finished: cancelled, passed, or failed</li>
 * </ul>
 * The test will be cancelled through {@link #cancelTest()} if the test is stopped ({@link #onStop()}) while still running.
 * </p>
 * <p>To define a test details (title, summary, description, and instructions), a concrete test should instantiate a {@link Details} sub-class.</p>
 * TODO properly define the state machine (w.r.t Fragment)
 */
public abstract class NewTest extends Fragment {

    private static final String TAG = NewTest.class.getSimpleName();

    public static abstract class Details implements Serializable {

        protected final int mTitleId;
        protected final int mSummaryId;
        protected final int mDescriptionId;
        protected final int mInstructionsId;

        /**
         * @param titleId        The title string resource id.
         * @param summaryId      The summary string resource id.
         * @param descriptionId  The description string resource id.
         * @param instructionsId The instructions string resource id, or -1 if none.
         */
        protected Details(int titleId, int summaryId, int descriptionId, int instructionsId) {
            this.mTitleId = titleId;
            this.mSummaryId = summaryId;
            this.mDescriptionId = descriptionId;
            this.mInstructionsId = instructionsId;
        }

        /**
         * Call to {@link Details(int, int, int, int)} with -1 as the instructions string resource id.
         *
         * @param titleId       The title string resource id.
         * @param summaryId     The summary string resource id.
         * @param descriptionId The description string resource id.
         */
        protected Details(int titleId, int summaryId, int descriptionId) {
            this(titleId, summaryId, descriptionId, -1);
        }

        /**
         * The title uniquely identifies a test.
         * <p>It can be displayed a list item title or as the app title itself.</p>
         * <p>Override this method to format the title string.</p>
         *
         * @return The title string.
         */
        public String getTitle(Context context) {
            return context.getString(mTitleId);
        }

        /**
         * The summary is a one line description of a test, useful to describe an test in a list.
         * <p>the full description is the {@link #getDescription(Context)}.</p>
         * <p>Override this method to format the summary string.</p>
         *
         * @return The summary string.
         * @see #getDescription(Context)
         */
        public String getSummary(Context context) {
            return context.getString(mSummaryId);
        }


        /**
         * The description is a multiline description of a test.
         * <p>It is *not* the instructions of the test but explains what the test is about in a more thorough way than the test summary.</p>
         * <p>Override this method to format the description string.</p>
         *
         * @return The description string.
         * @see #getSummary(Context)
         */
        public String getDescription(Context context) {
            return context.getString(mDescriptionId);
        }

        public boolean hasInstructions() {
            return mInstructionsId != -1;
        }

        /**
         * The instructions are a multiline description of what the user has to do.
         * <p>It is *not* the general description of the test but explains what is going to happen and what the user needs to do.</p>
         * <p>Override this method to format the instructions string.</p>
         *
         * @return The instructions string or <code>null</code> if none.
         */
        public String getInstructions(Context context) {
            if (hasInstructions()) {
                return context.getString(mInstructionsId);
            } else {
                return null;
            }
        }

        public abstract Fragment getFragment();
    }

    private static final int STATUS_FRESH = 0;
    private static final int STATUS_RUNNING = 1;
    private static final int STATUS_CANCELLED = 2;
    private static final int STATUS_PASSED = 3;
    private static final int STATUS_FAILED = 4;
    private static final int STATUS_PAUSED = 5;

    private int mStatus;
    private boolean mIsCancellable;

    protected NewTest(boolean isCancellable) {
        super();

        mStatus = STATUS_FRESH;
        mIsCancellable = isCancellable;
    }

    protected abstract Details getDetails();

    /**
     * @return Whether this test can be cancelled.
     */
    public boolean isCancellable() {
        return mIsCancellable;
    }

    /**
     * Method to actually begin the test.
     * <p>Set the internal {@link #mStatus}, call {@link #onBeginTest()}, and then {@link #onResumeTest()} if the test was not already running.</p>
     */
    public void beginTest() {
        if (isRunning()) {
            return;
        }

        mStatus = STATUS_RUNNING;
        onBeginTest();
        onResumeTest();
    }

    /**
     * Callback at the beginning of the test.
     * <p>Override this method to set up and run the test.</p>
     */
    protected void onBeginTest() {
        Log.d(TAG, "onBeginTest()");
    }

    /**
     * Method to actually resume the test.
     * <p>Set the internal {@link #mStatus} and then call {@link #onResumeTest()} if the test was not already running.</p>
     */
    public void resumeTest() {
        if (isRunning()) {
            return;
        }

        mStatus = STATUS_RUNNING;
        onResumeTest();
    }

    /**
     * Callback when the test is resumed.
     * <p>Override this method to run the test at creation or after being paused.</p>
     */
    protected void onResumeTest() {
        Log.d(TAG, "onResumeTest()");
    }

    /**
     * Method to actually pause the test.
     * <p>Set the internal {@link #mStatus} and then call {@link #onPauseTest()} if the test was not already paused.</p>
     */
    public void pauseTest() {
        if (isPaused()) {
            return;
        }

        mStatus = STATUS_PAUSED;
        onPauseTest();
    }

    /**
     * Callback when the test is paused.
     * <p>Override this method to stop the test at destruction or just temporarily.</p>
     */
    protected void onPauseTest() {
        Log.d(TAG, "onPauseTest()");
    }

    /**
     * Method to actually cancel the test.
     * <p>Set the internal {@link #mStatus}, call {@link #onPauseTest()}, then {@link #onCancelTest()}, and then {@link #onFinishTest()} if the test was not already cancelled.</p>
     */
    public void cancelTest() {
        if (isCancelled()) {
            return;
        }

        mStatus = STATUS_CANCELLED;
        if (isRunning()) {
            onPauseTest();
        }
        onCancelTest();
        onFinishTest();
    }

    /**
     * Callback at the cancellation of the test.
     * <p>Override this method to cancel the test.</p>
     */
    protected void onCancelTest() {
        Log.d(TAG, "onCancelTest()");
    }

    /**
     * Method to actually finish a test.
     * <p>Set the internal {@link #mStatus}, call {@link #onPauseTest()}, and then {@link #onFinishTest()} if the test was not already finished (e.g. passed or failed) or cancelled.</p>
     *
     * @param passed Whether the test passed or not.
     */
    protected void finishTest(boolean passed) {
        if (isCompleted() || isCancelled()) {
            return;
        }

        mStatus = passed ? STATUS_PASSED : STATUS_FAILED;
        onPauseTest();
        onFinishTest();
    }

    /**
     * Callback at the end of the test.
     * <p>Override this method to tear down the test.</p>
     */
    protected void onFinishTest() {
        Log.d(TAG, "onFinishTest()");

    }

    @Override
    public void onResume() {
        super.onResume();

        if (isPaused()) {
            resumeTest();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (isRunning()) {
            pauseTest();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (isRunning() || isPaused()) {
            cancelTest();
        }
    }

    /**
     * @return Whether this test is still fresh (has not even begun to run).
     */
    public boolean isFresh() {
        return mStatus == STATUS_FRESH;
    }

    /**
     * @return Whether this test is still running.
     */
    public boolean isRunning() {
        return mStatus == STATUS_RUNNING;
    }

    /**
     * @return Whether this test is paused.
     */
    public boolean isPaused() {
        return mStatus == STATUS_PAUSED;
    }

    /**
     * @return Whether this test has completely run and has failed or passed.
     * @see #hasPassed()
     * @see #hasFailed()
     * @see #isCancelled()
     */
    public boolean isCompleted() {
        return hasPassed() || hasFailed();
    }

    /**
     * @return Whether this test has completely run and passed.
     */
    public boolean hasPassed() {
        return mStatus == STATUS_PASSED;
    }

    /**
     * @return Whether this test has completely run and failed.
     */
    public boolean hasFailed() {
        return mStatus == STATUS_FAILED;
    }

    /**
     * @return Whether this test was begun and cancelled.
     */
    public boolean isCancelled() {
        return mStatus == STATUS_CANCELLED;
    }

}
