package com.fairphone.checkup.tests;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fairphone.checkup.R;
import com.fairphone.checkup.information.Information;

/**
 * An information test automatically begins when the {@link android.app.Fragment} starts.
 */
public abstract class InformationTest<InstanceInformation extends Information> extends Test {

    protected InstanceInformation mInstanceInformation;

    public InformationTest() {
        super(false);
    }

    @Override
    public void onStart() {
        super.onStart();

        beginTest();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_information_test, container, false);
        final TextView descriptionView = (TextView) root.findViewById(R.id.test_description);

        descriptionView.setText(getDetails().getDescription(getActivity()));

        populateContainer(inflater, container, (ViewGroup) root.findViewById(R.id.test_content_container));

        return root;
    }

    @Override
    protected void onResumeTest(boolean firstResume) {
        super.onResumeTest(firstResume);

        mInstanceInformation.setUp();
    }

    @Override
    protected void onPauseTest() {
        super.onPauseTest();

        mInstanceInformation.tearDown();
    }

    /**
     * @param inflater         The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container        If non-null, this is the parent view that the fragment's UI should be attached to. The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param contentContainer The parent view to populate if the regular information layout is to be used.
     */
    protected abstract void populateContainer(LayoutInflater inflater, ViewGroup container, ViewGroup contentContainer);

}
