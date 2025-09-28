package com.schema.app.ui.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.schema.app.data.model.Event;
import com.schema.app.receiver.AlarmReceiver;
import com.schema.app.receiver.PreAlarmReceiver;
import com.schema.app.ui.activity.SettingsActivity;

public class AlarmScheduler {

    private static final String TAG = "AlarmScheduler";
    private static final int PRE_ALARM_REQUEST_CODE_OFFSET = 100000;

    public static void scheduleAlarm(Context context, Event event) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long mainAlarmTime = event.getStartPreparationTimeMillis();

        // 1. 메인 알람 설정 (PrepareActivity 실행)
        Intent mainIntent = new Intent(context, AlarmReceiver.class);
        mainIntent.putExtra("event_id", event.getId());

        PendingIntent mainPendingIntent = PendingIntent.getBroadcast(
                context,
                event.getId(),
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 2. SharedPreferences에서 사용자 설정 미리 알림 시간 가져오기 (기본값 5분)
        SharedPreferences prefs = context.getSharedPreferences(SettingsActivity.PREFS_NAME, Context.MODE_PRIVATE);
        int preAlarmMinutes = prefs.getInt(SettingsActivity.KEY_PRE_ALARM_TIME, 5);
        long preAlarmTime = mainAlarmTime - (preAlarmMinutes * 60000L);

        Intent preAlarmIntent = new Intent(context, PreAlarmReceiver.class);
        preAlarmIntent.putExtra("event_id", event.getId());
        preAlarmIntent.putExtra("event_title", event.getTitle());

        PendingIntent preAlarmPendingIntent = PendingIntent.getBroadcast(
                context,
                event.getId() + PRE_ALARM_REQUEST_CODE_OFFSET, // 메인 알람과 충돌하지 않도록 다른 리퀘스트 코드 사용
                preAlarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 알람 설정
        if (alarmManager != null) {
            try {
                // 메인 알람
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mainAlarmTime, mainPendingIntent);
                Log.d(TAG, "Main alarm scheduled for event: " + event.getTitle() + " at " + mainAlarmTime);

                // 미리 알람 (현재 시간보다 미래일 경우에만 설정)
                if (preAlarmTime > System.currentTimeMillis()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, preAlarmTime, preAlarmPendingIntent);
                    Log.d(TAG, "Pre-alarm scheduled for event: " + event.getTitle() + " at " + preAlarmTime + " (" + preAlarmMinutes + " min before)");
                }

            } catch (SecurityException e) {
                Log.e(TAG, "Failed to schedule exact alarm due to security exception.", e);
            }
        }
    }

    public static void cancelAlarm(Context context, Event event) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        
        // 메인 알람 취소
        Intent mainIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent mainPendingIntent = PendingIntent.getBroadcast(
                context, event.getId(), mainIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(mainPendingIntent);

        // 미리 알람 취소
        Intent preAlarmIntent = new Intent(context, PreAlarmReceiver.class);
        PendingIntent preAlarmPendingIntent = PendingIntent.getBroadcast(
                context, event.getId() + PRE_ALARM_REQUEST_CODE_OFFSET, preAlarmIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(preAlarmPendingIntent);

        Log.d(TAG, "Alarms canceled for event: " + event.getTitle());
    }
}
