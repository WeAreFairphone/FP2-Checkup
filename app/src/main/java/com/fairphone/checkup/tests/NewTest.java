package com.fairphone.checkup.tests;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.Locale;

/**
 * A standalone (abstract) test element that lives as a fragment.
 * <p>A test inner lifecycle follows the {@link Fragment}'s life with some dedicated callbacks:
 * <ul>
 * <li>{@link #onCreateTest()} called from {@link Fragment#onCreate(Bundle)}, to do the initial test creation</li>
 * <li>{@link Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)} when the test user interface is created</li>
 * <li>{@link #onResumeTest(boolean)} when the test is resumed (at creation or when the {@link Fragment} itself is resumed and the test was paused)</li>
 * <li>{@link #onPauseTest()} when the test is paused (at destruction if the test was running or when the {@link Fragment} itself is paused)</li>
 * <li>{@link #onCancelTest()} if and when the test is cancelled</li>
 * <li>{@link #onFinishTest()} when the test is finished (cancelled, passed, or failed): when the {@link Fragment} is being stopped</li>
 * </ul>
 * The test will be cancelled through {@link #cancelTest()} if the {@link Fragment} is stopped ({@link Fragment#onStop()}).
 * </p>
 * <p><strong>The call to the parent method </strong>(<code>super.onBeginTest()</code> for instance)<strong> must be included in each overridden method.</strong></p>
 * <p>The {@link #beginTest()}, {@link #cancelTest()}, and {@link #finishTest(boolean)} methods should <strong>not</strong> be overridden.
 * They are the public interface to a test with the status methods ({@link #isFresh()}, {@link #isRunning()}, {@link #isCancelled()}, {@link #isCompleted()}, {@link #hasPassed()}, {@link #hasFailed()}).</p>
 * <p>To define a test details (title, summary, description, and instructions), a concrete test should instantiate a {@link Details} sub-class.<br>
 * The default behaviour is to retrieve the strings as-is from the resource files.
 * To format them in a different way, do override:
 * <ul>
 * <li>{@link Details#getTitle(Context)} that returns the title string</li>
 * <li>{@link Details#getSummary(Context)} that returns the summary string</li>
 * <li>{@link Details#getDescription(Context)} that returns the description string</li>
 * </ul>
 * </p>
 * TODO properly document the state machine (w.r.t Fragment)
 */
public abstract class NewTest extends Fragment {

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

    protected final String TAG = getClass().getSimpleName();

    protected NewTest(boolean isCancellable) {
        super();

        mStatus = STATUS_FRESH;
        mIsCancellable = isCancellable;
    }

    protected abstract Details getDetails();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createTest();
    }

    @Override
    public void onResume() {
        super.onResume();

        resumeTest();
    }

    @Override
    public void onPause() {
        super.onPause();

        pauseTest();
    }

    @Override
    public void onStop() {
        super.onStop();

        cancelTest();
    }

    /**
     * @return Whether this test can be cancelled.
     */
    public boolean isCancellable() {
        return mIsCancellable;
    }

    /**
     * Method to actually create the test.
     */
    public void createTest() {
        if (!isFresh()) {
            return;
        }

        Log.d(TAG, "createTest()");

        onCreateTest();
    }

    /**
     * Callback at the creation of the test.
     * <p>Override this method to set up the test.</p>
     */
    protected void onCreateTest() {
        Log.d(TAG, "onCreateTest()");
    }

    /**
     * Method to actually begin the test.
     */
    public void beginTest() {
        if (isRunning()) {
            return;
        }

        // If the test is cancelled, #onFinishTest() has been called so we need to call #onCreateTest() again
        final boolean needsCreation = isCancelled();

        Log.d(TAG, "beginTest()");

        mStatus = STATUS_RUNNING;
        if (needsCreation) {
            onCreateTest();
        }
        onResumeTest(true);
    }

    /**
     * Method to actually resume the test.
     */
    public void resumeTest() {
        if (!isPaused()) {
            return;
        }

        Log.d(TAG, "resumeTest()");

        mStatus = STATUS_RUNNING;
        onResumeTest(false);
    }

    /**
     * Callback when the test is resumed.
     * <p>Override this method to run the test at creation or after being paused.</p>
     *
     * @param firstResume Whether this is the first call initiated by {@link #beginTest()}.
     */
    protected void onResumeTest(boolean firstResume) {
        Log.d(TAG, String.format(Locale.ENGLISH, "onResumeTest(%s)", firstResume ? "true" : "false"));
    }

    /**
     * Method to actually pause the test.
     */
    public void pauseTest() {
        if (!isRunning()) {
            return;
        }

        Log.d(TAG, "pauseTest()");

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
     */
    public void cancelTest() {
        if (isCancelled()) {
            return;
        }

        Log.d(TAG, "cancelTest()");

        if (isRunning()) {
            onPauseTest();
        }

        mStatus = STATUS_CANCELLED;
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
     *
     * @param passed Whether the test passed or not.
     */
    protected void finishTest(boolean passed) {
        if (isCompleted() || isCancelled()) {
            return;
        }

        Log.d(TAG, "finishTest()");

        if (isRunning()) {
            onPauseTest();
        }

        mStatus = passed ? STATUS_PASSED : STATUS_FAILED;
        onFinishTest();
    }

    /**
     * Callback at the end of the test.
     * <p>Override this method to tear down the test.</p>
     */
    protected void onFinishTest() {
        Log.d(TAG, "onFinishTest()");
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
