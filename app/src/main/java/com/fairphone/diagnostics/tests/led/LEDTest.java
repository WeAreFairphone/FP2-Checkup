package com.fairphone.diagnostics.tests.led;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;

/**
 * Created by maarten on 20/09/16.
 */

public class LEDTest extends Test {

    private static final String TAG = LEDTest.class.getSimpleName();

    private NotificationManager notificationManager;
    private Notification notification;
    private boolean loop = true;
    private int msInterval = 500;

    public LEDTest(Context context) {
        super(context);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.led_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.led_test_description;
    }

    @Override
    protected void onPrepare() {
        notificationManager = (NotificationManager)getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notification = new Notification();
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.flags |= 0x10000000;
    }

    @Override
    protected void runTest() {

        new ThreeColorBlink().execute();

        askIfSuccess(getResources().getString(R.string.led_test_finish_question));
    }

    @Override
    protected void onCleanUp() {
        loop = false;
        notificationManager.cancel(0);
        super.onCleanUp();
    }

    private class ThreeColorBlink extends AsyncTask<Void, Void, Void> {
        private int counter = 0;

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                synchronized (this) {
                    notification.ledARGB = 0xFFFF0000;
                    if (LEDTest.this.loop) {
                        while(loop) {
                            notificationManager.notify(0, notification);
                            this.wait(msInterval);
                            notificationManager.cancel(0);
                            if (counter++ % 3 == 0) {
                                notification.ledARGB = 0xFF00FF00; // green
                            } else if (counter++ % 2 == 0) {
                                notification.ledARGB = 0xFF0000FF; // blue
                            } else {
                                notification.ledARGB = 0xFFFF0000; // red
                            }
                        }
                    }
                }
            }
            catch (InterruptedException e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            notificationManager.cancel(0);
            counter = 0;
        }
    }
}
