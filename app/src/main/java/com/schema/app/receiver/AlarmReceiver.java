package com.schema.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.schema.app.ui.activity.AlarmActivity;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int eventId = intent.getIntExtra("event_id", -1);
        if (eventId == -1) {
            return;
        }

        Intent activityIntent = new Intent(context, AlarmActivity.class);
        if (intent.getExtras() != null) {
            activityIntent.putExtras(intent.getExtras());
        }
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        
        context.startActivity(activityIntent);
    }
}
