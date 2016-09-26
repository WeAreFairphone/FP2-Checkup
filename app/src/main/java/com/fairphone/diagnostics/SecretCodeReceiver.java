package com.fairphone.diagnostics;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * Created by maarten on 26/09/16.
 */

public class SecretCodeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, ModuleChooser.class);
        String secretAction = "android.provider.Telephony.SECRET_CODE";
        String action = intent.getAction();
        String host = intent.getData() != null ? intent.getData().getHost() : null;
        if (secretAction.equals(action) && "0042".equals(host)) {
            packageManager.setComponentEnabledSetting(componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            Intent diagnosticsIntent = new Intent(context, ModuleChooser.class);
            diagnosticsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(diagnosticsIntent);
        } else {
            packageManager.setComponentEnabledSetting(componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
    }
}