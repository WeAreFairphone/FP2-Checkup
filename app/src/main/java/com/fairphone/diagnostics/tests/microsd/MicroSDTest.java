package com.fairphone.diagnostics.tests.microsd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

    private static final String TAG = MicroSDTest.class.getSimpleName();

    private static final String MICROSD_PATH = "/storage/sdcard1/";

    View mTestView;
    BroadcastReceiver mReceiver;

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
        mTestView = LayoutInflater.from(getContext()).inflate(R.layout.view_microsd_test, null);
        setTestView(mTestView);
    }

    @Override
    protected void runTest() {
        replaceView();
        setupMicroSdMonitor();
        printData();
    }

    @Override
    protected void onCleanUp() {
        getContext().unregisterReceiver(mReceiver);
        mReceiver = null;
        super.onCleanUp();
    }

    private void setupMicroSdMonitor() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
        filter.addAction("android.intent.action.MEDIA_CHECKING");
        filter.addAction("android.intent.action.MEDIA_EJECT");
        filter.addAction("android.intent.action.MEDIA_MOUNTED");
        filter.addAction("android.intent.action.MEDIA_NOFS");
        filter.addAction("android.intent.action.MEDIA_REMOVED");
        filter.addAction("android.intent.action.MEDIA_SCANNER_FINISHED");
        filter.addAction("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        filter.addAction("android.intent.action.MEDIA_SCANNER_STARTED");
        filter.addAction("android.intent.action.MEDIA_SHARED");
        filter.addAction("android.intent.action.MEDIA_UNMOUNTABLE");
        filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        filter.addDataScheme("file");
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                printData();
            }
        };
        getContext().registerReceiver(mReceiver, filter);
    }

    private void printData() {
        long l = getTotalSize(MICROSD_PATH);
        String str = getResources().getString(R.string.microsd_unknown);
        if (l > 0L) {
            str = getResources().getString(R.string.microsd_mounted);
            ((TextView)findViewById(R.id.microsd_size_value)).setText(Formatter.formatFileSize(getContext(), l));
            long availableSizeInBytes = new StatFs(MICROSD_PATH).getAvailableBytes();
            ((TextView)findViewById(R.id.microsd_available_value)).setText(Formatter.formatFileSize(getContext(), availableSizeInBytes));
        } else {
            ((TextView)findViewById(R.id.microsd_size_value)).setText(getResources().getString(R.string.not_available));
            ((TextView)findViewById(R.id.microsd_available_value)).setText(getResources().getString(R.string.not_available));
        }
        ((TextView)findViewById(R.id.microsd_state_value)).setText(str);
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
