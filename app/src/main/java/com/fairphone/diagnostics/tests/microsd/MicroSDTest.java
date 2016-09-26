package com.fairphone.diagnostics.tests.microsd;

import android.content.Context;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fairphone.diagnostics.R;
import com.fairphone.diagnostics.tests.Test;

/**
 * Created by maarten on 21/09/16.
 */

public class MicroSDTest extends Test {

    private static final String PATH = "/storage/sdcard1/";

    public MicroSDTest(Context context) {
        super(context);
    }

    @Override
    protected int getTestTitleID() {
        return R.string.microsd_test_title;
    }

    @Override
    protected int getTestDescriptionID() {
        return R.string.microsd_test_description;
    }

    protected void replaceView() {
        View mTestView = LayoutInflater.from(getContext()).inflate(R.layout.view_microsd_test, null);
        setTestView(mTestView);
    }

    @Override
    protected void onPrepare() {

    }

    @Override
    protected void runTest() {
        replaceView();

        printData();

        //askIfSuccess(getResources().getString(R.string.microsd_test_finish_question));
    }

    private void printData() {
        long l = getTotalSize(PATH);
        String str = "Not mounted";
        if (l > 0L) {
            str = "Mounted";
            ((TextView)findViewById(R.id.microsd_size)).setText("Total size: " + Formatter.formatFileSize(getContext(), l));
            long availableSizeInBytes=new StatFs(PATH).getAvailableBytes();
            ((TextView)findViewById(R.id.microsd_available)).setText("Available space: " + Formatter.formatFileSize(getContext(), availableSizeInBytes));
        }
        ((TextView)findViewById(R.id.microsd_state)).setText("Status: " + str);
    }

    public static long getTotalSize(String paramString) {
        try {
            StatFs statFs = new StatFs(paramString);
            long blockSize = statFs.getBlockSizeLong();
            long blockCount = statFs.getBlockCountLong();
            return blockSize * blockCount;
        } catch (Exception ex) {}
        return 0L;
    }
}
