package com.fairphone.checkup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SecretCodeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String secretAction = "android.provider.Telephony.SECRET_CODE";
        String action = intent.getAction();
        String host = intent.getData() != null ? intent.getData().getHost() : null;
        if (secretAction.equals(action) && "3424".equals(host)) {
            Intent diagnosticsIntent = new Intent(context, MainActivity.class);
            diagnosticsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(diagnosticsIntent);
        }
    }
}
