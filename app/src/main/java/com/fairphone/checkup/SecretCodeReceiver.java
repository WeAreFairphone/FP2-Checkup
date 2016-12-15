package com.fairphone.checkup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SecretCodeReceiver extends BroadcastReceiver {
    private static final String SECRET_ACTION = "android.provider.Telephony.SECRET_CODE";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String host = intent.getData() != null ? intent.getData().getHost() : null;

        if (SECRET_ACTION.equals(action) && context.getString(R.string.secret_code_main_activity).equals(host)) {
            Intent activityIntent = new Intent(context, MainActivity.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(activityIntent);
        }
    }
}
