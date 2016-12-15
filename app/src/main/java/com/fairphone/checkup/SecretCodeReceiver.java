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
            Intent activityIntent = new Intent(context, MainActivity.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(activityIntent);
        }
    }
}
