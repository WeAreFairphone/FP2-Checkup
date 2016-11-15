package com.fairphone.checkup.tests;

/**
 * An information test automatically begins when the {@link android.app.Fragment} starts.
 */
public abstract class InformationTest extends NewTest {

    public InformationTest() {
        super(false);
    }

    @Override
    public void onStart() {
        super.onStart();

        beginTest();
    }

}
